package com.example.ee_drivefinal.ViewModel;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.ee_drivefinal.Model.DriveData;
import com.example.ee_drivefinal.Repositories.BluetoothManager;
import com.example.ee_drivefinal.Repositories.GPSHandler;
import com.example.ee_drivefinal.Repositories.JsonHandler;
import com.example.ee_drivefinal.Repositories.ObdHandler;
import com.example.ee_drivefinal.Repositories.Repository;
import com.example.ee_drivefinal.Repositories.ServerHandler;
import com.example.ee_drivefinal.Service.DriveService;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.Utils.SharedPrefHelper;
import com.example.ee_drivefinal.View.ConfirmationActivity;
import com.example.ee_drivefinal.View.DriveHistoryActivity;
import com.example.ee_drivefinal.View.DrivingActivity;
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
import com.google.android.gms.location.LocationServices;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrivingViewModel {


    //Variables
    private Repository repository;
    private DrivingActivity drivingActivity;
    private LocationListener locationListener;
    private GPSHandler gpsHandler;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket bluetoothSocket = null;
    private JsonHandler jsonHandler;
    private DriveData driveData;
    private ObdHandler obdHandler;
    private ServerHandler serverHandler;
    private MutableLiveData<String> status;
    private Intent driveServiceIntent;
    final Handler handler = new Handler();

    //Make sure to init drivedatya from driving activity;
    public DrivingViewModel(DrivingActivity view) throws IOException {
        initialVariables(view);
        startListeners();

    }

    public void startListeners() {
        driveServiceIntent = new Intent(GlobalContextApplication.getContext(), DriveService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            drivingActivity.startForegroundService(driveServiceIntent);
        } else {
            drivingActivity.startService(driveServiceIntent);
        }
        DriveData.getInstance().setDriveInProcess(true);
        startAssistListener();
    }

    private void initialVariables(DrivingActivity view) throws IOException {
        repository = new Repository();
        serverHandler = new ServerHandler();
        jsonHandler = new JsonHandler();
        driveData = DriveData.getInstance();
        drivingActivity = view;
        bluetoothManager = new BluetoothManager();
        status = new MutableLiveData<>();
        gpsHandler = new GPSHandler(LocationServices.getSettingsClient(GlobalContextApplication.getContext()));
    }

    public DrivingViewModel() {
    }

    public void finish() {
        if (driveServiceIntent != null)
            drivingActivity.stopService(new Intent(GlobalContextApplication.getContext(), DriveService.class));
        gpsHandler.stopLocationChanged();
        if (bluetoothSocket != null)
            if (bluetoothSocket.isConnected())
                obdHandler.stopRecording();
        if(handler!=null)
            handler.removeCallbacksAndMessages(null);
        driveData.setDriveInProcess(false);
        driveData.resetData();
    }

    public void chooseDevice() {
        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(drivingActivity);
        ArrayAdapter adapter = new ArrayAdapter(GlobalContextApplication.getContext(), android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));
        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String deviceAddress = devices.get(position).toString();
                Log.d("Device Address", deviceAddress);
                drivingActivity.showProgressBar(true);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    //Background work here
                    try {
                        connect(deviceAddress);
                    } catch (IOException e) {
                        FileHandler.appendLog(e.toString());
                        e.printStackTrace();
                    }
                    handler.post(() -> {
                        //UI Thread work here
                        drivingActivity.showProgressBar(false);
                        if (bluetoothSocket != null) {
                            drivingActivity.updateButtons(true);
                            drivingActivity.setStatus("Connect");
                        } else {
                            drivingActivity.updateButtons(false);
                            status.postValue("Connection Failed");
                        }
                    });
                });
                // TODO save deviceAddress
            }
        });
        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

    private void connect(String deviceAddress) throws IOException {
        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
        bluetoothSocket = bluetoothManager.connect(device);
        if (bluetoothSocket.isConnected()) {
            obdHandler = new ObdHandler(bluetoothSocket, this);
            status.postValue("Connected");
            Log.d("Bluetooth Connected", Boolean.toString(bluetoothSocket.isConnected()));
        }
    }



    public void endDrive() throws IOException {
        serverHandler.getSendToServerStatus().observe(drivingActivity, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Working")) {
                    drivingActivity.showProgressBarEnd(true);
                } else if (s.equals("Failed")) {
                    drivingActivity.showFailedConfirmationActivity(ConfirmationActivity.class);
                } else {
                    drivingActivity.showConfirmationActivity(ConfirmationActivity.class);
                }

            }
        });
        sendDriveToServer();
        disconnect();
        stopService();
        stopWritingData();

        driveData.setDriveInProcess(false);
        //TODO: save to file and return to main activity / summarize drive
        if (bluetoothSocket.isConnected()) {
            gpsHandler.stopLocationChanged();
            try {
                bluetoothSocket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            driveData.resetData();
            if (!bluetoothSocket.isConnected()) {
            }
        } else {
        }

    }

    private void stopWritingData() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }

    }

    public void stopService() {
        drivingActivity.stopService(new Intent(GlobalContextApplication.getContext(), DriveService.class));
    }

    private void sendDriveToServer() {
        serverHandler.sendEndOfDrive(driveData);
    }

    private void disconnect() {
        obdHandler.disconnect();
    }


    public void updateAssist(boolean isChecked) {
        driveData.setDriverAssist(isChecked);
        drivingActivity.updateBar(isChecked);
    }


    public void updateScreen(double getmFuel, double getmSpeed) {
        drivingActivity.updateScreen(getmFuel, getmSpeed);
    }

    public void startDriveRecord() throws JSONException, UnirestException {
        driveData.setDriveInProcess(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    driveData.setId(serverHandler.sendStartOfDriveToServerAndGetDriveId(jsonHandler.toJsonServerStartOfDrive()).getString("createdItemId"));
                    Log.d("Drive id", driveData.getId());
                } catch (JSONException | UnirestException e) {
                    FileHandler.appendLog(e.toString());
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        Log.d("Start id", driveData.getId());

        obdHandler.startRecording();
        writeDataHandler();
        screenListeners();
    }

    private void screenListeners() {
        drivingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                status.observe(drivingActivity, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        drivingActivity.updateStatus(s);
                    }
                });
                obdHandler.getFuelLiveData().observe(drivingActivity, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        updateScreen(driveData.getmFuel(), driveData.getmSpeed());
                        drivingActivity.updateStatus("Recording");
                    }
                });


            }
        });
    }

    private void startAssistListener() {
        DriveData.getInstance().getCurrentRecommendedSpeed().observe(drivingActivity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer recommendedSpeed) {
                drivingActivity.updateAssistUpdate(recommendedSpeed);
            }
        });
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    private void writeDataHandler() {
        final int delay = 3000; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    driveData.writeData();
                } catch (IOException exception) {
                    FileHandler.appendLog(exception.toString());
                    exception.printStackTrace();
                }
                if(!DriveData.getInstance().getDriveInProcess()){
                    handler.removeCallbacks(this);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    public void setDriveAssist(boolean b) throws JSONException, UnirestException {
        if(b){
       //     if(driveData.getPoints()!=null && driveData.getPoints().size()>1)
  //          Log.d("Model", serverHandler.getOptimalModel(SharedPrefHelper.getInstance().getId(),DriveData.getInstance().getLastPoint().getLat(),DriveData.getInstance().getLastPoint().getLang()).toString());
            driveData.setDriverAssist(true);
        }else{

            driveData.setDriverAssist(false);
        }
    }
}

