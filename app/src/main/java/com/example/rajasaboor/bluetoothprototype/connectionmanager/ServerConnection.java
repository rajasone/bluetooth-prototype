package com.example.rajasaboor.bluetoothprototype.connectionmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by rajaSaboor on 9/18/2017.
 */

public class ServerConnection extends Thread {
    private static final String TAG = ServerConnection.class.getSimpleName();
    private final BluetoothServerSocket bluetoothServerSocket;

    public ServerConnection() {
        BluetoothServerSocket temp = null;
        try {
            temp = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BuildConfig.APP_NAME, UUID.fromString(BuildConfig.UUID));
            Log.d(TAG, "ServerConnection: Connection start using the UUID ===> " + BuildConfig.UUID);
        } catch (IOException e) {
            Log.e(TAG, "ServerConnection: Socket listen method failed ===> ", e.getCause());
        }
        bluetoothServerSocket = temp;
    }

    @Override
    public void run() {
        Log.d(TAG, "run: start");
        BluetoothSocket socket = null;

        /*
        while (true) {
            Log.d(TAG, "run: Inside the loop");
            try {
                socket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "run: Server accept failed ===> ", e.getCause());
                e.printStackTrace();
                break;
            }

            if (socket != null) {
                Log.d(TAG, "run: Socket id NOT NULL !! start the connection");
                // manage the work
                Log.d(TAG, "run: " + socket.getRemoteDevice().getName());
                try {
                    socket.getOutputStream().write("hello".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                try {
//                    bluetoothServerSocket.close();
//                    Log.d(TAG, "run: Close successfully");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
        */


        if (bluetoothServerSocket == null) {
            Log.e(TAG, "run: Bluetooth server socket is NULL");
        } else {
            Log.d(TAG, "run: Bluetooth server socket is OK");
        }

        try {
            Log.d(TAG, "run: REFCOMM server socket start");
            socket = bluetoothServerSocket.accept();
            Log.d(TAG, "run: Server socket accepted the connection");
        } catch (Exception e) {
            Log.e(TAG, "run: Exception occoured while accepting the connection", e.getCause());
            e.printStackTrace();
        }

        if (socket != null) {
            Log.d(TAG, "run: Socket is good now start the communication");
           ConnectedHandler handler = new ConnectedHandler(socket);
            handler.start();
            handler.write("hello i have a new message".getBytes());
        } else {
            Log.e(TAG, "run: Socket is NULL");
        }

        Log.d(TAG, "run: end");
    }

    public void close() {
        try {
            bluetoothServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close: Not able to close the socket ===> ", e.getCause());
        }
    }
}
