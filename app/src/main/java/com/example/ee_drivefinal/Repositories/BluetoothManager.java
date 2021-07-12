package com.example.ee_drivefinal.Repositories;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothManager {

    private static final String TAG = BluetoothManager.class.getName();
    public MutableLiveData<String> bluetoothErrorMessage = new MutableLiveData<>();

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public  BluetoothSocket connect(BluetoothDevice dev) throws IOException {
        BluetoothSocket sock = null;
        BluetoothSocket sockFallback = null;
        String msg = "Starting Bluetooth connection..";
        Log.d(TAG, msg);
        bluetoothErrorMessage.postValue(msg);
        try {
            sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
            sock.connect();
        } catch (Exception e1) {
            FileHandler.appendLog(e1.toString());
            e1.printStackTrace();
            bluetoothErrorMessage.postValue("Error While Connecting");
//            Toast.makeText(GlobalContextApplication.getContext(),"Error While Connecting",Toast.LENGTH_SHORT).show();
            Class<?> clazz = sock.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
                sockFallback.connect();
                sock = sockFallback;
            } catch (Exception e2) {
                FileHandler.appendLog(e2.toString());
                e2.printStackTrace();
                Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection.", e2);
                throw new IOException(e2.getMessage());
            }
        }
//        Toast.makeText(GlobalContextApplication.getContext(),"Connected",Toast.LENGTH_SHORT).show();
        return sock;
    }
}