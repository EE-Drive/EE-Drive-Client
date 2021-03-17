package com.example.ee_drive_client.view;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.R;
import com.example.ee_drive_client.controller.AppController;
import com.example.ee_drive_client.controller.DrivingController;
import com.example.ee_drive_client.repositories.RepositoryCar;
import com.example.ee_drive_client.model.Car;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainScreenFragment extends Fragment {
    AppController mainController;
    DrivingController drivingController;
    FusedLocationProviderClient fusedLocationProviderClient;
    String ObddeviceAddress;
    BluetoothDevice device;
    BluetoothSocket socket;
    UUID id=UUID.randomUUID();
    String ID = id.toString();
    UUID Id=UUID.fromString(ID);
    Boolean chosen=false;
    private TextView isConnected, textViewDebug;
    private RepositoryCar repo;
    String obAddress;
    MainActivity mainActivity;
    Context context;
    public MainScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        repo = new RepositoryCar(getContext());
        mainController=new AppController((MainActivity) getActivity());
        drivingController =new DrivingController((MainActivity)getActivity());

        Button startBtn = view.findViewById(R.id.main_start_btn);
        isConnected = view.findViewById(R.id.main_isConnected_txt);
        textViewDebug = view.findViewById(R.id.textViewDebug);
        isConnected.setText("Not connected");
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Send parameters :action= MainScreenFragmentDirections.actionMainToRecording(String exmaple);
                Navigation.findNavController(view).navigate(R.id.action_main_to_recording);

            }
        });
        Button endBtn = view.findViewById(R.id.main_EndDrive_btn);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_main_to_first);

            }
        });

        ImageButton obdBtn = view.findViewById(R.id.main_obd_btn);
        obdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag","btn");
                mainController.onObdConnect((MainActivity) getActivity(),getContext());
                isConnected.setText(mainController.getError());
            }
        });
//        obdBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ArrayList deviceStrs = new ArrayList();
//                final ArrayList devices = new ArrayList();
//
//                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
//                if (pairedDevices.size() > 0)
//                {
//                    for (BluetoothDevice device : pairedDevices)
//                    {
//                        deviceStrs.add(device.getName() + "\n" + device.getAddress());
//                        devices.add(device.getAddress());
//                    }
//                }
//
//                // show list
//                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
//
//                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.select_dialog_singlechoice,
//                        deviceStrs.toArray(new String[deviceStrs.size()]));
//
//                alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        dialog.dismiss();
//                        int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
//                        String deviceAddress = (String) devices.get(position);
//                        chosen=true;
//                        // TODO save deviceAddress
//                        obAddress=deviceAddress;
//                    }
//                });
//
//                alertDialog.setTitle("Choose Bluetooth device");
//                alertDialog.show();
//
//                if (chosen==true)
//                {
//                     btAdapter = BluetoothAdapter.getDefaultAdapter();
//
//                    BluetoothDevice device = btAdapter.getRemoteDevice(obAddress);
//
//                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//
//                    BluetoothSocket socket = null;
//                    try {
//                        socket = device.createRfcommSocketToServiceRecord(uuid);
//                    } catch (IOException e) {
//                        textViewDebug.setText((CharSequence) e.getMessage());
//                    }
//
//                    try {
//
//                        socket.connect();
//                        if(socket.isConnected())
//                        {
//                            Toast.makeText(getContext(), "Connection success", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } catch (IOException e) {
//                        textViewDebug.setText((CharSequence) e.getMessage());
//                        try {
//                            socket.close();
//                        } catch (IOException ioException) {
//                            ioException.printStackTrace();
//                            textViewDebug.setText((CharSequence) ioException.getMessage());
//                        }
//                    }
//
//                    try {
//                        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
//
//                        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
//
//                        new TimeoutCommand(100).run(socket.getInputStream(), socket.getOutputStream());
//
//                        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
//
//                        RPMCommand engineRpmCommand = new RPMCommand();
//                        SpeedCommand speedCommand = new SpeedCommand();
//                        while (!Thread.currentThread().isInterrupted())
//                        {
//                            engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
//                            speedCommand.run(socket.getInputStream(), socket.getOutputStream());
//                            // TODO handle commands result
//                            Log.d(TAG, "RPM: " + engineRpmCommand.getFormattedResult());
//                            Log.d(TAG, "Speed: " + speedCommand.getFormattedResult());
//                            Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }catch (Exception ignored){}
//
//
//
//                }
//            }
//
//        });

        Spinner mainSpinner = (Spinner) view.findViewById(R.id.main_spinner);
        mainSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getAllCarsFromDB()));

        return view;
    }





    private ArrayList<String> getAllCarsFromDB() {

        ArrayList<Car> carArrayList = (ArrayList<Car>) repo.getCars();
        ArrayList<String> arrayListSpinner = new ArrayList<String>();


        for (int i = 0; i < carArrayList.size(); i++) {
            arrayListSpinner.add(carArrayList.get(i).loadFullModelForShow());
        }

        return arrayListSpinner;
    }




}