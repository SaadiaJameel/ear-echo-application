package com.example.login.UI;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.database.Observable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.login.Manifest;
import com.example.login.R;
import com.example.login.retrofit.ConnectThread;
import com.example.login.retrofit.ConnectedThread;

import java.util.Set;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Test extends AppCompatActivity {

    private ActivityResultLauncher<Intent> enableBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        showToast("Bluetooth is on");
                    } else {
                        showToast("Could not switch on Bluetooth");
                    }
                }
            });


    // Global variables we will use in the
    private static final String TAG = "FrugalLogs";
    private static final int REQUEST_ENABLE_BT = 1;
    //We will use a Handler to get the BT Connection statys
    public static Handler handler;
    private final static int ERROR_READ = 0; // used in bluetooth handler to identify message update
    BluetoothDevice arduinoBTModule = null;
    UUID arduinoUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //We declare a default UUID to create the global variable
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //Intances of BT Manager and BT Adapter needed to work with BT in Android.
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        //Intances of the Android UI elements that will will use during the execution of the APP
        TextView btReadings = findViewById(R.id.btReadings);
        TextView btDevices = findViewById(R.id.btDevices);
        Button connectToDevice = (Button) findViewById(R.id.connectToDevice);
        Button seachDevices = (Button) findViewById(R.id.seachDevices);
        Button clearValues = (Button) findViewById(R.id.refresh);
        Log.d(TAG, "Begin Execution");


        //Using a handler to update the interface in case of an error connecting to the BT device
        //My idea is to show handler vs RxAndroid
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    case ERROR_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        btReadings.setText(arduinoMsg);
                        break;
                }
            }
        };

        // Set a listener event on a button to clear the texts
        clearValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btDevices.setText("");
                btReadings.setText("");
            }
        });

        // Create an Observable from RxAndroid
        //The code will be executed when an Observer subscribes to the the Observable
        final Observable<String> connectToBTObservable = Observable.create(emitter -> {
            Log.d(TAG, "Calling connectThread class");
            //Call the constructor of the ConnectThread class
            //Passing the Arguments: an Object that represents the BT device,
            // the UUID and then the handler to update the UI
            ConnectThread connectThread = new ConnectThread(arduinoBTModule, arduinoUUID, handler);
            connectThread.run();
            //Check if Socket connected
            if (connectThread.getMmSocket().isConnected()) {
                Log.d(TAG, "Calling ConnectedThread class");
                //The pass the Open socket as arguments to call the constructor of ConnectedThread
                ConnectedThread connectedThread = new ConnectedThread(connectThread.getMmSocket());
                connectedThread.run();
                if(connectedThread.getValueRead()!=null)
                {
                    // If we have read a value from the Arduino
                    // we call the onNext() function
                    //This value will be observed by the observer
                    emitter.onNext(connectedThread.getValueRead());
                }
                //We just want to stream 1 value, so we close the BT stream
                connectedThread.cancel();
            }
            // SystemClock.sleep(5000); // simulate delay
            //Then we close the socket connection
            connectThread.cancel();
            //We could Override the onComplete function
            emitter.onComplete();

        });

        connectToDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btReadings.setText("");
                if (arduinoBTModule != null) {
                    Log.d(TAG, "Device not null");
                    //We subscribe to the observable until the onComplete() is called
                    //We also define control the thread management with
                    // subscribeOn:  the thread in which you want to execute the action
                    // observeOn: the thread in which you want to get the response
                    connectToBTObservable.
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribeOn(Schedulers.io()).
                            subscribe(valueRead -> {
                                //valueRead returned by the onNext() from the Observable
                                btReadings.setText(valueRead);
                                //We just scratched the surface with RxAndroid
                            });

                }else{
                    Log.d(TAG, "Device null");
                }
            }
        });

        seachDevices.setOnClickListener(new View.OnClickListener() {
            //Display all the linked BT Devices
            @Override
            public void onClick(View view) {
                //Check if the phone supports BT
                if (bluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    Log.d(TAG, "Device doesn't support Bluetooth");
                } else {
                    Log.d(TAG, "Device support Bluetooth");
                    //Check BT enabled. If disabled, we ask the user to enable BT
                    if (!bluetoothAdapter.isEnabled()) {
                        showToast("Turning on Bluetooth...");
                        //intent to on bluetooth
//                    Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(intent, REQUEST_ENABLE_BT);

                        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        enableBluetoothLauncher.launch(enableBluetoothIntent);

                    } else {
                        Log.d(TAG, "Bluetooth is enabled");
                    }
                    String btDevicesString="";
                    Set< BluetoothDevice > pairedDevices = bluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device: pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            Log.d(TAG, "deviceName:" + deviceName);
                            Log.d(TAG, "deviceHardwareAddress:" + deviceHardwareAddress);
                            //We append all devices to a String that we will display in the UI
                            btDevicesString=btDevicesString+deviceName+" || "+deviceHardwareAddress+"\n";
                            //If we find the HC 05 device (the Arduino BT module)
                            //We assign the device value to the Global variable BluetoothDevice
                            //We enable the button "Connect to HC 05 device"
                            if (deviceName.equals("ESP32")) {
                                Log.d(TAG, "ESP32");
                                arduinoUUID = device.getUuids()[0].getUuid();
                                arduinoBTModule = device;
                                //HC -05 Found, enabling the button to read results
                                connectToDevice.setEnabled(true);
                            }
                            btDevices.setText(btDevicesString);
                        }
                    }
                }
                Log.d(TAG, "Button Pressed");
            }
        });

    }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}