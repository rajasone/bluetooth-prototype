package com.example.rajasaboor.bluetoothprototype.discoverdeviceslist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajaSaboor on 9/12/2017.
 */

public class DevicesListPresenter implements DevicesListContract.Presenter, AdapterView.OnItemClickListener {
    private static final String TAG = DevicesListPresenter.class.getSimpleName();
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private List<String> deviceNameList = new ArrayList<>();
    private boolean isNewDeviceFound = false;
    private BroadcastReceiver bluetoothReceiver = null;
    private final DevicesListContract.FragmentView fragmentView;

    public DevicesListPresenter(DevicesListContract.FragmentView fragmentView) {
        this.fragmentView = fragmentView;
    }

    @Override
    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    @Override
    public void setDeviceList(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public List<String> getDeviceNameList() {
        return deviceNameList;
    }

    @Override
    public void refreshListAdapter() {
        fragmentView.refreshListAdapter();
    }

    @Override
    public void onDeviceDiscoveryComplete() {
        if ((this.getDeviceList() == null || this.getDeviceList().size() == 0) && (!this.isNewDeviceFound())) {
            fragmentView.showToast("No Device found");
        } else {
            fragmentView.showToast("Devices found");
        }
        this.setNewDeviceFound(false);
    }

    @Override
    public DevicesListContract.FragmentView getDeviceListFragmentView() {
        return this.fragmentView;
    }

    @Override
    public boolean isNewDeviceFound() {
        return isNewDeviceFound;
    }

    @Override
    public void setNewDeviceFound(boolean newDeviceFound) {
        isNewDeviceFound = newDeviceFound;
    }

    @Override
    public void addNameInListFromBluetoothList(List<BluetoothDevice> deviceList) {
        for (BluetoothDevice device : deviceList) {
            deviceNameList.add(device.getName());
        }
    }

    @Override
    public void addBluetoothDeviceInList(BluetoothDevice device) {
        if ((device != null) && (!deviceList.contains(device))) {
            device.getBluetoothClass();
            deviceList.add(device);
            deviceNameList.add(device.getName());
            Log.d(TAG, "addDeviceInList: Device Added ===> " + device.getName() + " type ===> " + (device.getBluetoothClass().describeContents()));
            setNewDeviceFound(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick: Size ===> " + getDeviceList().size());
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        handleListClick(i);
    }

    @Override
    public void handleListClick(final int position) {
        Log.d(TAG, "handleListClick: start");
        Log.d(TAG, "handleListClick: Position ===> " + position);
        Log.d(TAG, "handleListClick: Device Name ===> " + deviceNameList.get(position));

        /*
        if (!isDeviceIsPaired(deviceList.get(position))) {
            Log.d(TAG, "handleListClick: Device is not pared send the paired request");
            pairDevice(deviceList.get(position));
        } else {
            Log.d(TAG, "handleListClick: Device is already paired start sending some data");
        }
        */
        final BluetoothDevice device = deviceList.get(position);
        BluetoothSocket socket = null;
        if (!BluetoothAdapter.getDefaultAdapter().getBondedDevices().contains(deviceList.get(position))) {
            Log.d(TAG, "handleListClick: Device is not pared send the paired request");
            pairDevice(deviceList.get(position));
        } else {
            Log.d(TAG, "handleListClick: Device is already paired start sending some data");

            Log.d(TAG, "handleListClick: Paired Device details===============");
            Log.d(TAG, "handleListClick: Name ===> " + device.getName());
            for (ParcelUuid uuid : device.getUuids()) {
                Log.d(TAG, "handleListClick: UUIDS ===> " + uuid.getUuid().toString());
            }
            Log.d(TAG, "handleListClick: --------------------------");
            for (ParcelUuid uuid : device.getUuids()) {
                Log.d(TAG, "handleListClick: UUIDS ===> " + uuid.getUuid().toString());
                try {
                    socket = device.createRfcommSocketToServiceRecord(uuid.getUuid());
                    socket.connect();

                    if (socket.isConnected()) {
                        Log.d(TAG, "handleListClick: Socket is connected with this UUID ===> " + uuid.getUuid());
                        break;
                    } else {
                        Log.e(TAG, "handleListClick: Socket is NOT connected with this UUID ===> " + uuid.getUuid());
                    }

                    Log.d(TAG, "handleListClick: ---------------------");
                } catch (IOException e) {
                    Log.d(TAG, "handleListClick: Exception ===> " + e.getMessage());
                }
            }
            Log.d(TAG, "handleListClick: Address ===> " + device.getAddress());
            Log.d(TAG, "handleListClick: Device bond state ===> " + device.getBondState());
            Log.d(TAG, "handleListClick: ***************************");


            if (socket.isConnected()) {
                try {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    byte[] arr = ("Raja" + " ").getBytes();
                    arr[arr.length - 1] = 0;
                    outputStream.write(arr);
                    Log.d(TAG, "handleListClick: Write to the output stream successfully");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "handleListClick: Socket is NOT connected ");
            }


            /*
            BluetoothSocket socket = null;
            try {
                socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 2);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                if (socket != null) {
                    Log.d(TAG, "run: socket is good to go");
                    socket.connect();
                    byte[] arr = new byte[1024];
                    arr = "adads".getBytes();
                    socket.getOutputStream().write(arr);
                    Log.d(TAG, "run: write to the stream");

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    Log.d(TAG, "run: Size of output stream ===> " + String.valueOf(out.size()));
                } else {
                    Log.e(TAG, "run: socket is NUll");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            /*
            try {
                Log.d(TAG, "handleListClick: UUID length ===> " + device.getUuids().length);


                BluetoothSocket socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                socket.connect();


                if (socket != null) {
                    Log.d(TAG, "handleListClick: Socket is okka ===> " + socket.getRemoteDevice().getName());
                    if (socket.getOutputStream() != null) {
                        Log.d(TAG, "handleListClick: Outputstream is oka ===> " + socket.getOutputStream());
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        if (dataOutputStream != null) {
                            Log.d(TAG, "handleListClick: Size of stream ===> " + dataOutputStream.size());
                            dataOutputStream.writeBytes("hello");
                        } else {
                            Log.e(TAG, "handleListClick: Data output stream is NULL");
                        }
                        Log.d(TAG, "handleListClick: Data sent successfully");
                    } else {
                        Log.d(TAG, "handleListClick: output stream is null");
                    }

                } else {
                    Log.e(TAG, "handleListClick: Socket is NULL");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            */
        }
        Log.d(TAG, "handleListClick: end");
    }

    @Override
    public void pairDevice(final BluetoothDevice device) {
        Log.d(TAG, "pairDevice: start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothSocket socket = null;

//                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("7350ae54-c701-4095-84c5-5aaf8c492cbb"));
                //socket.connect();
                // if exception occoured then use this fall back  method

                try {
//                    device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress());
                    Log.d(TAG, "run: in the fall back method");
                    if (device.getUuids() != null) {
                        Log.d(TAG, "run: Contains UUID");
                    } else {
                        Log.e(TAG, "run: UUID is NULL");
                    }
                    socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                    Log.d(TAG, "run: pair successfully");
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.getMessage();
                }
                try {
                    if (socket != null)
                        socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //7350ae54-c701-4095-84c5-5aaf8c492cbb
//
//                BluetoothSocket tmp = null;
//
//                try {
//                    // Get a BluetoothSocket to connect with the given BluetoothDevice.
//                    // MY_UUID is the app's UUID string, also used in the server code.
//                    tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(("7350ae54-c701-4095-84c5-5aaf8c492cbb")));
//                    Log.d(TAG, "run: temp is assigned successfully");
//                } catch (IOException e) {
//                    Log.e(TAG, "Socket's create() method failed", e);
//                }
//                mSocket = tmp;
//
//                try {
//                    // Connect to the remote device through the socket. This call blocks
//                    // until it succeeds or throws an exception.
//                    Log.d(TAG, "run: Connection the socket");
//                    mSocket.connect();
//                    Log.d(TAG, "run: Connected");
//                } catch (IOException connectException) {
//                    Log.e(TAG, "run: Error while connectiog ===> ", connectException.getCause());
//                    // Unable to connect; close the socket and return.
//                    try {
//                        mSocket.close();
//                    } catch (IOException closeException) {
//                        Log.e(TAG, "Could not close the client socket", closeException);
//                    }
//                    return;
//                }

            }
        }).start();
        Log.d(TAG, "pairDevice: end");
    }

    @Override
    public void unpairDevice(BluetoothDevice device) {
        Log.d(TAG, "unpairDevice: start");
        try {
            Method method = device.getClass().getMethod(BuildConfig.REMOVE_BOND_METHOD_NAME, (Class<?>[]) null);
            method.invoke(device, (Object[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "unpairDevice: end");

    }

    @Override
    public void pairingProcessBroadcast() {
        Log.d(TAG, "pairingProcessBroadcast: start");
        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: Intent action ===> " + intent.getAction());
                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                    int currentState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    if (currentState == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "onReceive: Device PAIRED");
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Log.d(TAG, "onReceive: Device name ===> " + device.getName());
                        Log.d(TAG, "onReceive: Device address ===> " + device.getAddress());
                    } else if (currentState == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "onReceive: Device UNPAIRED");
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    }
                }
            }
        };
        Log.d(TAG, "pairingProcessBroadcast: end");
    }

    @Override
    public BroadcastReceiver getBluetoothReceiver() {
        return bluetoothReceiver;
    }
}
