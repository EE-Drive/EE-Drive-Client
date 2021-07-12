package com.example.ee_drivefinal.Repositories;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drivefinal.Model.DriveData;
import com.example.ee_drivefinal.Utils.Calculator;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.ViewModel.DrivingViewModel;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

public class ObdHandler {

    //Variables
    private BluetoothSocket mSocket;
    private DrivingViewModel drivingViewModel;
    private String mObdType;
    private DriveData driveData;
    private Boolean mStop, mIsConnected;
    private Thread mThread;
    private Calculator calculator;
    private LinkedList<ObdCommand> mObdCommands;
    private MutableLiveData<String> speedLiveData ;
    private MutableLiveData<String> fuelLiveData;



    public ObdHandler(BluetoothSocket socket, DrivingViewModel ViewModel) {
        mSocket = socket;
        driveData = DriveData.getInstance();
        calculator = new Calculator();
        drivingViewModel = ViewModel;
        speedLiveData = new MutableLiveData<>();
        fuelLiveData = new MutableLiveData<>();
    }

    public void disconnect() {
        closeSocket();
        stopRecording();
        mIsConnected = false;
    }

    private void closeSocket() {
        if (mSocket != null)
            try {
                mSocket.close();
            } catch (IOException ignored) {
                FileHandler.appendLog(ignored.toString());
                ignored.printStackTrace();
            }
        mSocket = null;
      //  Log.d("Bluetooth Disconnected", Boolean.toString(mSocket.isConnected()));
    }

    private void resetOBD() {
        try {

            new EchoOffCommand().run(mSocket.getInputStream(), mSocket.getOutputStream());
            new LineFeedOffCommand().run(mSocket.getInputStream(), mSocket.getOutputStream());
            new TimeoutCommand(100).run(mSocket.getInputStream(), mSocket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(mSocket.getInputStream(), mSocket.getOutputStream());
        } catch (Exception ignored) {
            FileHandler.appendLog(ignored.toString());
            ignored.printStackTrace();
        }
    }

    public void startRecording() {
        mStop = false;
        resetOBD();
        testObdType();
        driveData.setObdType(mObdType);
        setCommands();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mStop) {
                    try {
                        mThread.sleep(100);
                    } catch (InterruptedException exception) {
                        FileHandler.appendLog(exception.toString());
                        exception.printStackTrace();
                    }
                    try {
                        for (ObdCommand command : mObdCommands) {
                            command.run(mSocket.getInputStream(), mSocket.getOutputStream()); //consider getting the streams outisde of the while
                            driveData.updateObd(new String[]{String.valueOf(System.currentTimeMillis()), command.getName(), command.getCalculatedResult()});
                        }
                        if (driveData.getObdType() == "RPM") {
                            double maf = calculator.calcMaf(driveData.getmRpm(), driveData.getmMap(), driveData.getmIat());
                            driveData.setmFuel(calculator.calcFuel(maf));
                        }
                        if (driveData.getObdType() == "MAF") {
                            driveData.setmFuel(calculator.calcFuel(driveData.getmMaf()));
                        }
                    } catch (Exception ignored) {
                        FileHandler.appendLog(ignored.toString());
                        ignored.printStackTrace();
                    }
                    if(driveData.getPoints().size()>2)
                    driveData.addInfoToLastPoint();

                    speedLiveData.postValue(Double.toString(driveData.getmSpeed()));
                    fuelLiveData.postValue(Double.toString(driveData.getmFuel()));
                }
            }
        });
        mThread.start();
    }

    public MutableLiveData<String> getSpeedLiveData() {
        return speedLiveData;
    }

    public void setSpeedLiveData(MutableLiveData<String> speedLiveData) {
        this.speedLiveData = speedLiveData;
    }

    public MutableLiveData<String> getFuelLiveData() {
        return fuelLiveData;
    }

    public void setFuelLiveData(MutableLiveData<String> fuelLiveData) {
        this.fuelLiveData = fuelLiveData;
    }

    public String testObdType() {
        OutputStream out = null;
        InputStream in = null;
        try {
            out = mSocket.getOutputStream();
            in = mSocket.getInputStream();
        } catch (Exception ignored) {
        }

        if (in != null && out != null) {
            try {
                new ConsumptionRateCommand().run(in, out);
                mObdType = "FUEL";
                return mObdType;
            } catch (Exception ignored) {
            }
            try {
                new MassAirFlowCommand().run(in, out);
                mObdType = "MAF";
                return mObdType;
            } catch (Exception ignored) {
            }
            mObdType = "RPM";
            return mObdType;
        }
        drivingViewModel.getStatus().postValue("Cant Define Type");
        Toast.makeText(GlobalContextApplication.getContext(), "Try to test the obd type again", Toast.LENGTH_SHORT).show();
        mObdType = null;
        return null;
    }

    public void setCommands() {
        mObdCommands = new LinkedList<>();
        mObdCommands.add(new SpeedCommand());
        if (mObdType.equals("FUEL"))
            mObdCommands.add(new ConsumptionRateCommand());
        else if (mObdType.equals("MAF"))
            mObdCommands.add(new MassAirFlowCommand());
        else {
            mObdCommands.add(new RPMCommand());
            mObdCommands.add(new IntakeManifoldPressureCommand());
            mObdCommands.add(new AirIntakeTemperatureCommand());
        }
    }

    public void stopRecording() {
        mStop = true;
        mObdCommands = null;
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

}

