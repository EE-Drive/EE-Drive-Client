package com.example.ee_drive_client.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.model.OBDData;
import com.example.ee_drive_client.repositories.Calculator;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

public class OBDHandler {
    private boolean mIsConnected = false;
    private BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private BluetoothSocket mSocket;
    private String mObdType;
    private volatile boolean mStop;
    private LinkedList<ObdCommand> mObdCommands;
    private Thread mThread;
    //Most recent drive data
    private double mlat = 0;
    private double mlon = 0;
    private int mSpeed = 0;
    private double mFuel = 0;
    private double mMaf = 0;
    private double mRpm = 0;
    private double mMap = 0;
    private double mIat = 0;
    ArrayList<Object> mObdData;
    private String ObdAddress;
    BluetoothDevice device;
    String deviceName;
    MainActivity activity;
    AppController mainController;
    Calculator calculator=new Calculator();
    private OBDData obdData=new OBDData();

    public OBDHandler(Context mContext) {
        this.mContext = mContext;
      mObdData = new ArrayList<>(); //consider putting initial capacity

    }

    public boolean isConnected() {
        return mIsConnected;
    }


    public boolean connect(MainActivity view, Context context) {
        mContext = context;
        this.activity = view;
        Log.d("Tag", "connect: ");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();
        int position;

        // BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }
        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String deviceAddress = (String) devices.get(position);
                ObdAddress = deviceAddress;
                ParcelUuid[] uuids = null;
                if (ObdAddress != null) {

                    device = mBluetoothAdapter.getRemoteDevice(ObdAddress);
                    deviceName = device.getName();
                    connectOBD(device);

                }
            }

        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
        if (mSocket != null){
            resetOBD();
            startRecording();

        }
        return mIsConnected;
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
        Toast.makeText(mContext, "Try to test the obd type again", Toast.LENGTH_SHORT).show();
        mObdType = null;
        return null;
    }
    private boolean enableBT() {
        if (mBluetoothAdapter == null) {
            return false;
        }
        if (!mBluetoothAdapter.isEnabled())
            Toast.makeText(mContext, "Enable Bluetooth", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void closeSocket() {
        mStop=true;
        if (mSocket != null)
            try {
                mSocket.close();
            } catch (IOException ignored) {
            }
        mSocket = null;
    }
    MutableLiveData<Boolean> getConnected =new MutableLiveData<Boolean>();

    public MutableLiveData<Boolean> getGetConnected() {
        return getConnected;
    }

    private boolean getOBDSocket() {

        return mSocket.isConnected();


    }


    private void resetOBD() {
        try {
            new EchoOffCommand().run(mSocket.getInputStream(), mSocket.getOutputStream());
            new LineFeedOffCommand().run(mSocket.getInputStream(), mSocket.getOutputStream());
            new TimeoutCommand(100).run(mSocket.getInputStream(), mSocket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(mSocket.getInputStream(), mSocket.getOutputStream());
        } catch (Exception ignored) {
        }
    }
    public void connectOBD(BluetoothDevice device) {
        closeSocket();
        try {
            Log.d("OBDHandler", "connectOBD: get socket");
            mSocket = (BluetoothSocket) device.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class}).invoke(device, 1);
        } catch (Exception e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
            mainController.updateError(e.toString());
            closeSocket();
        }
        if (mSocket != null) {
            try {
                Log.d("OBDHandler", "connectOBD: try to connect");
                mBluetoothAdapter.cancelDiscovery();
                Thread.sleep(500);
                //first ask the socket from the device
                mSocket.connect();
                Log.d("OBDHandler", "connectOBD: connected");
                mIsConnected=true;
                Toast.makeText(mContext, "Connected Successfully", Toast.LENGTH_SHORT).show();
                resetOBD();
                testObdType();
                Toast.makeText(mContext, "OBD Type: "+mObdType, Toast.LENGTH_SHORT).show();
               // Log.d("OBDHandler", "OBD Type: "+mObdType);
                startRecording();



            } catch (Exception e) {
                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                closeSocket();
            }
        }
    }
    MutableLiveData<OBDData> obdLiveData = new MutableLiveData<OBDData>();

    public void startRecording() {
        mStop = false;
        setCommands();
        mThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!mStop) {
                    try {
                        for (ObdCommand command : mObdCommands) {
                            command.run(mSocket.getInputStream(), mSocket.getOutputStream()); //consider getting the streams outisde of the while
                            updateObd(new String[]{String.valueOf(System.currentTimeMillis()), command.getName(), command.getCalculatedResult()});
                                obdLiveData.postValue(obdData);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

        });
        mThread.start();
    }
    private void setCommands() {
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

    public void updateObd(String[] obdCall) {
        obdData.setmType(mObdType);
        if (true)
            mObdData.add(obdCall);
        switch (obdCall[1].charAt(0)) {
            case 'V': //Vehicle Speed
                mSpeed = Integer.parseInt(obdCall[2]);
                obdData.setSpeed(mSpeed);
                break;
            case 'F': //Fuel consumption rate
                mFuel = Double.parseDouble(obdCall[2]);
                obdData.setFuel(mFuel);

                break;
            case 'M': //Mass Air Flow
                mMaf = Double.parseDouble(obdCall[2]);
                obdData.setmMaf(mMaf);

                break;
            case 'E': //Engine RPM
                mRpm = Double.parseDouble(obdCall[2]);
                obdData.setRpm(mRpm);
                break;
            case 'A': //Air Intake Temperature
                mIat = Double.parseDouble(obdCall[2]);
                obdData.setmIat(mIat);

                break;
            case 'I': //Intake Manifold Pressure
                mMap = Double.parseDouble(obdCall[2]);
                obdData.setmMap(mMap);
            break;
        }
    }
}
