package com.example.login.retrofit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.compose.material3.ProgressIndicatorDefaults;

import com.example.login.UI.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    BluetoothAdapter mBlueAdapter;

    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName= "EarEcho";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    Context mContext;
    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;

    AlertDialog mProgressDialog;

    private ConnectedThread mConnectedThread;

    // Constructor
    public BluetoothConnectionService(Context context){
        mContext= context;
        mBlueAdapter= BluetoothAdapter.getDefaultAdapter();
        start();
    }

    private class AcceptThread extends Thread{
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp= null;

            // New listening server socket
            try {
                tmp = mBlueAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "Accept Thread: Setting up Server using: " + MY_UUID_INSECURE);
            }catch (IOException e){

            }

            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread running");

            BluetoothSocket socket= null;

            try {
                // This is a blocking call and will only return on a
                // successful connection or exception
                Log.d(TAG, "run: RFCOM server socket start......");
                socket = mmServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection.");
            }catch(IOException e){
                Log.e(TAG, "AcceptThread: IOException: "+ e.getMessage());
            }

            if(socket != null){
                connected(socket, mmDevice);
            }

            Log.i(TAG, "END mAcceptThread");
        }

        public void cancel(){
            Log.d(TAG, "cancel: Cancelling AcceptThread.");

            try{
                mmServerSocket.close();
            }catch(IOException e){
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }

    }

    /*
    * This thread runs while attempting to make an outgoing connection
    * with a device. It runs straight through; the connection either
    * succeeds or fails.
    * */
    private class ConnectThread extends Thread{
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread: started.");
            mmDevice= device;
            deviceUUID= uuid;
        }

        public void run(){
            BluetoothSocket tmp= null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try{
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "+ MY_UUID_INSECURE);
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            }catch(IOException e){
                Log.e(TAG, "ConectThread: Could not create InsecureRfcommSocket "+ e.getMessage());
            }

            mmSocket= tmp;

            mBlueAdapter.cancelDiscovery();

            try {
                // This is a blocking call
                mmSocket.connect();

                Log.d(TAG, "run: ConnectThread connected.");
            }catch (IOException e){
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                }catch(IOException e1){
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket "+ e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread; Could not connect to UUID "+ MY_UUID_INSECURE);
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel(){
            try{
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            }catch(IOException e){
                Log.e(TAG, "cancel: close() of mmSocket in ConnectThread failed. " + e.getMessage());
            }
        }

    }
    /*
    * Start the chat service. Specifically start AcceptThread to begin a
    * session in listening (server) mode. Called by the Activity onResume()
    */
    public synchronized void start(){
        Log.d(TAG, "start");

        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }

    }

    /*
    * AcceptThread starts and sits waiting for a connection.
    * Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
    * */
    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Started.");

        // Create a ProgressDialog using AlertDialog.Builder
        AlertDialog.Builder progressDialogBuilder = new AlertDialog.Builder(mContext);
        progressDialogBuilder.setTitle("Connecting Bluetooth");
        progressDialogBuilder.setMessage("Please wait...");
        progressDialogBuilder.setCancelable(false);
        mProgressDialog =  progressDialogBuilder.create();

        // Show the AlertDialog
        mProgressDialog.show();

        // Start the ConnectThread
        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();


    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn= null;
            OutputStream tmpOut= null;

            try {
                mProgressDialog.dismiss();
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }

            mmInStream= tmpIn;
            mmOutStream= tmpOut;
        }

        public  void run(){
            byte[] buffer = new byte[1024];

            int bytes;

            while(true){
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage= new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: "+incomingMessage);
                }catch (IOException e){
                    Log.e(TAG, "write: Error reading InputStream. "+ e.getMessage());
                    break;
                }


            }
        }

        public void write(byte[] bytes){
            String text= new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputStream: "+ text);
            try{
                mmOutStream.write(bytes);
            }catch(IOException e){
                Log.e(TAG, "write: Error writing to outputstream. "+ e.getMessage());
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch(IOException e){

            }
        }
    }

    public void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice){
        Log.d(TAG, "connected: Starting.");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public void write(byte[] out){
        ConnectedThread r;
        Log.d(TAG, "write: Write Called.");
        mConnectedThread.write(out);
    }



}
