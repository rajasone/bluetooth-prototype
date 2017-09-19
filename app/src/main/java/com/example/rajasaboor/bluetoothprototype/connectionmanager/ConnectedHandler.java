package com.example.rajasaboor.bluetoothprototype.connectionmanager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by rajaSaboor on 9/19/2017.
 */

public class ConnectedHandler extends Thread {
    private static final String TAG = ConnectedHandler.class.getSimpleName();
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedHandler(BluetoothSocket socket) {
        Log.d(TAG, "ConnectedThread: Starting.");

        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;


        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream

        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            // Read from the InputStream
            try {
                bytes = mmInStream.read(buffer);
                String incomingMessage = new String(buffer, 0, bytes);
                Log.d(TAG, "InputStream: " + incomingMessage);
            } catch (IOException e) {
                Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                break;
            }
        }
    }

    //Call this from the main activity to send data to the remote device
    public void write(byte[] bytes) {
        String text = new String(bytes, Charset.defaultCharset());
        Log.d(TAG, "write: Writing to outputstream: " + text);
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

    public void connected(BluetoothSocket socket, BluetoothDevice device){
        start();

    }

}
