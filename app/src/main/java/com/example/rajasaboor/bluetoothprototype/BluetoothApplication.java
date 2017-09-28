package com.example.rajasaboor.bluetoothprototype;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.communication.BluetoothConnectionService;

import static android.content.ContentValues.TAG;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public class BluetoothApplication extends Application {
    private static final String TAG = BluetoothApplication.class.getSimpleName();
    private BluetoothConnectionService service = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: start");
        startService();
    }

    public BluetoothConnectionService getService() {
        return service;
    }

    public void startService() {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            service = new BluetoothConnectionService();
        }
    }

    public void stopService() {
        if (service != null) {
            service.cancel();
        }
    }
}
