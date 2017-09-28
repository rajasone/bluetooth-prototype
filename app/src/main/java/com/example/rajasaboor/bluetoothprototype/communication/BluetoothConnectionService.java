package com.example.rajasaboor.bluetoothprototype.communication;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by User on 12/21/2016.
 */

public class BluetoothConnectionService {
    private static final String TAG = BluetoothConnectionService.class.getSimpleName();
    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private ConnectedThread mConnectedThread;
    private Handler handler;
    private MessageListener messageListener;


    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public BluetoothConnectionService() {
        start();
    }

    public void setMessage(Message message, BluetoothDevice device, int msg) {
        message.arg1 = msg;
        message.obj = device;
    }

    public void cancel() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }


    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(BuildConfig.APP_NAME, UUID.fromString(BuildConfig.UUID));

                Log.d(TAG, "AcceptThread: Setting up Server using: " + BuildConfig.UUID);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread Running.");
            Message message = Message.obtain();
            BluetoothSocket socket = null;
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();

                setMessage(message, socket.getRemoteDevice(), BuildConfig.CONNECTED_SUCCESSFULLY);
                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
                try {
                    setMessage(message, socket.getRemoteDevice(), BuildConfig.CONNECTION_FAILED);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            Log.e(TAG, "run: Sending the message");
            try {
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //// TODO: 9/27/2017 start communicating
            //talk about this is in the 3rd
            if (socket != null) {
                connected(socket, mmDevice);
//                mConnectedThread.write("Hello from the server".getBytes());
            }

            Log.i(TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }

    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            Message message = Message.obtain();
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + BuildConfig.UUID);
                tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString(BuildConfig.UUID));
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Always cancel discovery because it will slow down a connection
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            // Make a connection to the BluetoothSocket


            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                setMessage(message, mmSocket.getRemoteDevice(), BuildConfig.CONNECTED_SUCCESSFULLY);
                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    mmSocket.getRemoteDevice();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                setMessage(message, mmSocket.getRemoteDevice(), BuildConfig.CONNECTION_FAILED);
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + BuildConfig.UUID);
            }
            Log.e(TAG, "run: Sending the message");
            try {
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // TODO: 9/27/2017 uncomment this while starting the communication
            //will talk about this in the 3rd video
            connected(mmSocket, mmDevice);
//            mConnectedThread.write("hello from the client".getBytes());
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }


    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    /**
     * AcceptThread starts and sits waiting for a connection.
     * Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
     **/

    public void startClient(BluetoothDevice device) {
        Log.d(TAG, "startClient: Started.");

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    /**
     * Finally the ConnectedThread which is responsible for maintaining the BTConnection, Sending the data, and
     * receiving incoming data through input/output streams respectively.
     **/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
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
                    Log.d(TAG, "run: Read msg ===> " + bytes);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    if ((messageListener != null) && (handler != null)) {
                        Log.d(TAG, "run: Message received ====> " + incomingMessage);
                        Log.d(TAG, "run: Message received length ====> " + incomingMessage);
                        Message message = Message.obtain();
                        message.what = BuildConfig.MESSAGE_RECEIVED;
                        message.obj = new com.example.rajasaboor.bluetoothprototype.model.Message(null, incomingMessage);
                        handler.sendMessage(message);
                        Log.d(TAG, "run: Message send to the handler successfully");
//                        messageListener.onMessageReceived(new String(buffer, 0, buffer.length));
                    } else {
                        Log.e(TAG, "run: Message Listener or Handler is NULL");
                    }
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

//                if (messageListener != null) {
//                    Log.d(TAG, "write: Message sent ====> " + new String(bytes));
//                    messageListener.onMessageSent(new String(bytes));
//                }

                if ((messageListener != null) && (handler != null)) {
                    Log.d(TAG, "write: Message sent ====> " + new String(bytes, 0, bytes.length));
                    Log.d(TAG, "write: Message sent length ====> " + new String(bytes, 0, bytes.length).length());
                    Message message = Message.obtain();
                    message.what = BuildConfig.MESSAGE_SENT;
                    message.obj = new com.example.rajasaboor.bluetoothprototype.model.Message(new String(bytes, 0, bytes.length), null);
                    handler.sendMessage(message);
                    Log.d(TAG, "run: Message send to the handler successfully");
//                        messageListener.onMessageReceived(new String(buffer, 0, buffer.length));
                } else {
                    Log.e(TAG, "run: Message Listener or Handler is NULL");
                }
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
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;

        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        mConnectedThread.write(out);

    }

    interface MessageListener {
        void onMessageReceived(String message);

        void onMessageSent(String message);
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}