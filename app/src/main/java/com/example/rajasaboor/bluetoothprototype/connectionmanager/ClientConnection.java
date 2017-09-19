package com.example.rajasaboor.bluetoothprototype.connectionmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by rajaSaboor on 9/18/2017.
 */

public class ClientConnection extends Thread {
    public static final String TAG = ClientConnection.class.getSimpleName();
    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;


    public ClientConnection(BluetoothDevice device) {
        Log.d(TAG, "ClientConnection: start");
        BluetoothSocket temp = null;

        bluetoothDevice = device;

        try {
            temp = device.createRfcommSocketToServiceRecord(UUID.fromString(BuildConfig.UUID));
        } catch (IOException e) {
            Log.e(TAG, "ClientConnection: Failed to create socket ===> ", e.getCause());
        }

        bluetoothSocket = temp;
        Log.d(TAG, "ClientConnection: end");
    }

    @Override
    public void run() {
        Log.d(TAG, "run: start");
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "run: Unable to connect ===> ", e.getCause());
            e.printStackTrace();

            try {
                bluetoothSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, "run: Not able to close the socket ===> ", e1.getCause());
            }
            return;
        }
        //mange the work associated to the socket

        if (bluetoothSocket != null) {
            Log.d(TAG, "run: start your work");
        } else {
            Log.d(TAG, "run: something is wrong");
        }
        Log.d(TAG, "run: end");
    }


    public void close() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close: Not able to close the socket ===> ", e.getCause());
        }
    }

    /*
    * This method initiate the Accept thread
     */

    public void startAcceptThread() {
        close();
        new ServerConnection().start();
    }

    /*
    * This method initiate the connect thread
     */

    public void startConnectThread() {
        start();
    }
}
