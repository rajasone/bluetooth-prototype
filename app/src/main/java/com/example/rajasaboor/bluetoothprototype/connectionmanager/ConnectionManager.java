package com.example.rajasaboor.bluetoothprototype.connectionmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by rajaSaboor on 9/20/2017.
 */

public class ConnectionManager {
    private static final String TAG = ConnectionManager.class.getSimpleName();
    private ConnectionManager.ServerConnection serverConnection;
    private ConnectionManager.ClientConnection clientConnection;
    private ConnectionManager.ConnectedHandler connectedHandler;
    private Handler handler;

    public ConnectionManager(Handler handler) {
        this.handler = handler;
    }

    public void setClientConnectionInstance(BluetoothDevice device) {
        clientConnection = new ClientConnection(device);
    }

    public synchronized void start() {
        if (clientConnection != null) {
            clientConnection.close();
            clientConnection = null;
        }

        if (serverConnection == null) {
            serverConnection = new ServerConnection();
            serverConnection.start();
        }

        if (connectedHandler != null) {
            connectedHandler.cancel();
            connectedHandler = null;
        }
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public ConnectedHandler getConnectedHandler() {
        return connectedHandler;
    }

    public Handler getHandler() {
        return handler;
    }

    public class ServerConnection extends Thread {
        private final String TAG = ServerConnection.class.getSimpleName();
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
                    Message message = new Message();
                    message.arg1 = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString(BuildConfig.CONNECTION_STATUS_KEY, "Connected with " + socket.getRemoteDevice().getName());
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d(TAG, "run: " + socket.getRemoteDevice().getName());
                    break;
                }
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

    public class ClientConnection extends Thread {
        public final String TAG = ClientConnection.class.getSimpleName();
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
                Log.d(TAG, "run: Connected with ===> " + bluetoothDevice.getName());
                Message message = new Message();
                message.arg1 = 1;
                Bundle bundle = new Bundle();
                bundle.putString(BuildConfig.CONNECTION_STATUS_KEY, "Connected with " + bluetoothDevice.getName());
                message.setData(bundle);
                handler.sendMessage(message);


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
    }


    public class ConnectedHandler extends Thread {
        private final String TAG = com.example.rajasaboor.bluetoothprototype.connectionmanager.ConnectedHandler.class.getSimpleName();
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

        public void connected(BluetoothSocket socket, BluetoothDevice device) {
            start();

        }

    }

}
