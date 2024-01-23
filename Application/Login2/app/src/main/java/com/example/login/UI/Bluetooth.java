package com.example.login.UI;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.retrofit.BluetoothConnectionService;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class Bluetooth extends AppCompatActivity {
    private static final String TAG= "FrugalLogs";

    private static final int REQUEST_ENABLE_BT = 1;


    private ActivityResultLauncher<Intent> enableBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        mBlueIv.setImageResource(R.drawable.ic_action_on);
                        showToast("Bluetooth is on");
                    } else {
                        showToast("Could not switch on Bluetooth");
                    }
                }
            });

    TextView mStatusBlueTv, mPairedTv;
    ImageView mBlueIv;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn, mStartConnectionBtn, mSendBtn;

    EditText etSend;

    BluetoothConnectionService mBluetoothConnection;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");



    BluetoothAdapter mBlueAdapter;
//
//    private static final String TAG = "BluetoothConnectionServ";
//    private static final String appName= "EarEcho";
//    private static final UUID MY_UUID_INSECURE =
//            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
//
//    Context mContext;
//    private AcceptThread mInsecureAcceptThread;
//
//    // Constructor
//    public Bluetooth(Context context){
//        mContext= context;
//        mBlueAdapter= BluetoothAdapter.getDefaultAdapter();
//
//    }
//
//    private class AcceptThread extends Thread{
//        // The local server socket
//        private final BluetoothServerSocket mmServerSocket;
//
//        public AcceptThread(){
//            BluetoothServerSocket tmp= null;
//
//            // New listening server socket
//            try {
//                tmp = mBlueAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
//
//                Log.d(TAG, "Accept Thread: Setting up Server using: " + MY_UUID_INSECURE);
//            }catch (IOException e){
//
//            }
//
//            mmServerSocket = tmp;
//        }
//
//        public void run(){
//            Log.d(TAG, "run: AcceptThread running");
//
//            BluetoothSocket socket= null;
//
//            try {
//                // This is a blocking call and will only return on a
//                // successful connection or exception
//                Log.d(TAG, "run: RFCOM server socket start......");
//                socket = mmServerSocket.accept();
//
//                Log.d(TAG, "run: RFCOM server socket accepted connection.");
//            }catch(IOException e){
//                Log.e(TAG, "AcceptThread: IOException: "+ e.getMessage());
//            }
//
//            if(socket != null){
//                connected                                                                     (socket, mmDevice);
//            }
//
//            Log.i(TAG, "END mAcceptThread");
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mStatusBlueTv = (TextView) findViewById(R.id.statusBluetoothTv);
        mPairedTv = (TextView) findViewById(R.id.pairedTv);
        mBlueIv = (ImageView) findViewById(R.id.bluetoothIv);
        mOnBtn = (Button) findViewById(R.id.onBtn);
        mOffBtn = (Button) findViewById(R.id.offBtn);
        mDiscoverBtn = (Button) findViewById(R.id.discoverableBtn);
        mPairedBtn = (Button) findViewById(R.id.pairedBtn);
//        mStartConnectionBtn= (Button) findViewById(R.id.StartConnectionBtn);
//        mSendBtn = (Button) findViewById(R.id.SendBtn);
//        etSend= (EditText) findViewById(R.id.editText);

        // adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bluetooth is available or not
        if (mBlueAdapter == null) {
            mStatusBlueTv.setText("Bluetooth is not available");
        } else {
            mStatusBlueTv.setText("Bluetooth is available");
        }

        // Set image according to bluetooth status(on/off)
        if (mBlueAdapter.isEnabled()) {
            mBlueIv.setImageResource((R.drawable.ic_action_on));
        } else {
            mBlueIv.setImageResource((R.drawable.ic_action_off));
        }



        //on btn click
        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBlueAdapter.isEnabled()) {
                    showToast("Turning on Bluetooth...");
                    //intent to on bluetooth
//                    Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(intent, REQUEST_ENABLE_BT);

                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    enableBluetoothLauncher.launch(enableBluetoothIntent);
                } else {
                    showToast("Bluetooth is already on");
                }
            }
        });

        //discover bluetooth btn
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mBlueAdapter.isDiscovering()){
                    showToast("Making device discoverable");
                }

            }
        });

        //off btn click
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBlueAdapter.isEnabled()){
                    mBlueAdapter.disable();
                    showToast("Turning bluetooth off");
                    mBlueIv.setImageResource(R.drawable.ic_action_off);
                }else{
                    showToast("Bluetooth is already off");
                }
            }
        });

        //get paired devices btn click
        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBlueAdapter.isEnabled()){
                    mPairedTv.setText("Paired Devices");
                    Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                    int i= 0;
                    for(BluetoothDevice device: devices){
                        mPairedTv.append("\nDevice: "+device.getName()+","+device);
                        testView();
                    }
                }else{
                    showToast("Turn on bluetooth to get paired devices.");
                }
            }
        });

//        mStartConnectionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mBlueAdapter.isEnabled()){
//                    startConnection();
//                }else{
//                    showToast("Turn on bluetooth to get paired devices.");
//                }
//            }
//        });

//        mSendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                byte[] bytes= etSend.getText().toString().getBytes(Charset.defaultCharset());
//                mBluetoothConnection.write(bytes);
//            }
//        });

    }

    //toast message function
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

//    // Start chat service method
//    public void startBTConnection(BluetoothDevice device, UUID uuid){
//        Log.d(TAG, "startBTConnection: INitializing RFCOM Bluetooth Connection.");
//        mBluetoothConnection.startClient(device, uuid);
//    }
//
//    public void startConnection(){
//        startBTConnection(mBTDevice, MY_UUID_INSECURE);
//    }

    public void testView(){
        Intent intent = new Intent(this, Test.class);
        startActivity(intent);
    }
}