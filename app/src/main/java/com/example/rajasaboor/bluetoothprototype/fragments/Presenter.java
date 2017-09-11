package com.example.rajasaboor.bluetoothprototype.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.SearchFragment;


/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class Presenter implements Contract.Presenter, DevicesListFragment.OnDeviceClickListener {
    private static final String TAG = Presenter.class.getSimpleName();
    private OnDiscoveryComplete onDiscoveryComplete = null;
    private BroadcastReceiver mReceiver = null;
    private SharedPreferences preferences = null;

    public Presenter(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void deleteSharedPrefs() {
        preferences.edit().clear().apply();
    }

    @Override
    public boolean getSharedPreferences() {
        return preferences.getBoolean(BuildConfig.BROADCAST_PREFS_KEY, false);
    }

    @Override
    public void setSharedPreferences() {
        preferences.edit().putBoolean(BuildConfig.BROADCAST_PREFS_KEY, true).apply();
    }

    @Override
    public boolean isDeviceHaveBluetooth() {
        return (BluetoothAdapter.getDefaultAdapter() != null);
    }

    @Override
    public boolean isDeviceBluetoothIsTurnedOn() {
        return (BluetoothAdapter.getDefaultAdapter().isEnabled());
    }

    @Override
    public boolean isPermissionGranted(PackageManager manager, String permission, String packageName) {
        return (manager.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void broadcastDefine() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public synchronized void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: start");
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: Device Found");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if ((device.getName() != null)) {
                        onDiscoveryComplete.onDiscoveryComplete(device);
                    } else {
                        Log.e(TAG, "onReceive: Device is not full filling the condition ===> " + device.getName());
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: Discovery End e");
                    onDiscoveryComplete.onDiscoveryFinish();
                }
                Log.d(TAG, "onReceive: end");
            }
        };
    }

    @Override
    public void showSearchFragment(FragmentManager fragmentManager, boolean show) {
        Log.d(TAG, "showSearchFragment: start");
        SearchFragment searchFragment = (SearchFragment) fragmentManager.findFragmentById(R.id.search_fragment_container);
        FragmentTransaction transaction = null;
        if (searchFragment != null) {
            Log.d(TAG, "showSearchFragment: Search fragment is not NULL");
            transaction = fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        if (transaction != null) {
            if (show) {
                Log.d(TAG, "showSearchFragment: Showing the fragment");
                transaction.show(searchFragment).commit();
            } else {
                Log.d(TAG, "showSearchFragment: Hide the fragment");
                transaction.hide(searchFragment).commit();
            }
        }
        Log.d(TAG, "showSearchFragment: end");
    }

    public OnDiscoveryComplete getOnDiscoveryComplete() {
        return onDiscoveryComplete;
    }

    public void setOnDiscoveryComplete(OnDiscoveryComplete onDiscoveryComplete) {
        this.onDiscoveryComplete = onDiscoveryComplete;
    }

    BroadcastReceiver getmReceiver() {
        return mReceiver;
    }

    @Override
    public void onDeviceClickListener(BluetoothDevice device) {
        Log.d(TAG, "onDeviceClickListener: Device Name ===> " + device.getName());
    }

    interface OnDiscoveryComplete {
        void onDiscoveryComplete(BluetoothDevice bluetoothDevice);

        void onDiscoveryFinish();
    }

}
