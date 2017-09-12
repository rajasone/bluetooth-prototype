package com.example.rajasaboor.bluetoothprototype.list;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by rajaSaboor on 9/12/2017.
 */

public class DevicesListPresenter implements DevicesListContract.Presenter, AdapterView.OnItemClickListener {
    private static final String TAG = DevicesListPresenter.class.getSimpleName();
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private List<String> deviceNameList = new ArrayList<>();
    private boolean isNewDeviceFound = false;
    private BroadcastReceiver bluetoothReceiver = null;


    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public List<String> getDeviceNameList() {
        return deviceNameList;
    }

    public boolean isNewDeviceFound() {
        return isNewDeviceFound;
    }

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
    public void handleListClick(int position) {
        Log.d(TAG, "handleListClick: start");
        Log.d(TAG, "handleListClick: Position ===> " + position);
        Log.d(TAG, "handleListClick: Device Name ===> " + deviceNameList.get(position));

        pairDevice(deviceList.get(position));
        Log.d(TAG, "handleListClick: end");
    }

    @Override
    public void pairDevice(final BluetoothDevice device) {
        Log.d(TAG, "pairDevice: start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothSocket socket = null;
                try {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID.randomUUID().toString()));
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    // if exception occoured then use this fall back  method
                    try {
                        Method method = device.getClass().getMethod(BuildConfig.SOCKET_CREATE_BOND_METHOD_NAME, (Class[]) null);
                        method.invoke(device, (Object[]) null);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (Exception e1) {
                        e1.getMessage();
                    }
                }
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
                    } else if (currentState == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "onReceive: Device UNPAIRED");
                    }
                }
            }
        };
        Log.d(TAG, "pairingProcessBroadcast: end");

    }
}
