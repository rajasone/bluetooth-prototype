package com.example.rajasaboor.bluetoothprototype.search;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import com.example.rajasaboor.bluetoothprototype.list.DevicesListFragment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;


/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class SearchPresenter implements SearchContract.Presenter, DevicesListFragment.OnDeviceClickListener {
    private static final String TAG = SearchPresenter.class.getSimpleName();
    private OnDiscoveryComplete onDiscoveryComplete = null;
    private BroadcastReceiver mReceiver = null;
    private SharedPreferences preferences = null;
    private BroadcastReceiver bluetoothReceiver = null;

    public SearchPresenter(SharedPreferences preferences) {
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
        Log.d(TAG, "showSearchFragment: Fragment to show ===> " + show);
        SearchFragment searchFragment = (SearchFragment) fragmentManager.findFragmentById(R.id.search_fragment_container);
        FragmentTransaction transaction = null;
        if (searchFragment != null) {
            Log.d(TAG, "showSearchFragment: Search fragment is NOT null");
            transaction = fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            Log.d(TAG, "showSearchFragment: Search fragment is NULL");
        }

        if (transaction != null) {
            if (show) {
                transaction.show(searchFragment).commit();
            } else {
                transaction.hide(searchFragment).commit();
            }
        }
        Log.d(TAG, "showSearchFragment: end");
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

    public void setOnDiscoveryComplete(OnDiscoveryComplete onDiscoveryComplete) {
        this.onDiscoveryComplete = onDiscoveryComplete;
    }

    BroadcastReceiver getmReceiver() {
        return mReceiver;
    }

    @Override
    public void onDeviceClickListener(final BluetoothDevice device) {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        Log.d(TAG, "onDeviceClickListener: Device Name ===> " + device.getName());
        Log.d(TAG, "onDeviceClickListener: UUID ===> " + UUID.randomUUID().toString().toUpperCase());


        pairDevice(device);
    }

    public BroadcastReceiver getBluetoothReceiver() {
        return bluetoothReceiver;
    }

    interface OnDiscoveryComplete {
        void onDiscoveryComplete(BluetoothDevice bluetoothDevice);

        void onDiscoveryFinish();
    }
}
