package com.example.rajasaboor.bluetoothprototype.handler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rajaSaboor on 9/7/2017.
 */

public class BluetoothHandler {
    /*

    private static final String TAG = BluetoothHandler.class.getSimpleName();
    private OnDiscoveryComplete onDiscoveryComplete = null;

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: start");
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                Log.d(TAG, "onReceive: Device Found");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if ((device.getName() != null)) {
                    Log.d(TAG, "onReceive: Adding the device in the list because its unique and not null ===> " + device.getName());
                    onDiscoveryComplete.onDiscoveryComplete(device);
                } else {
                    Log.e(TAG, "onReceive: Device is not full filling the condition ===> " + device.getName());
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                Log.d(TAG, "onReceive: Discovery End");
            }
            Log.d(TAG, "onReceive: end");
        }
    };

    public void setOnDiscoveryComplete(OnDiscoveryComplete onDiscoveryComplete) {
        this.onDiscoveryComplete = onDiscoveryComplete;
    }


    public interface OnDiscoveryComplete {

        void onDiscoveryComplete(BluetoothDevice bluetoothDevice);
    }
    */
}
