package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.SearchProgressFragment;


/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class SearchPresenter implements SearchContract.Presenter {
    private static final String TAG = SearchPresenter.class.getSimpleName();
    private OnDiscoveryComplete onDiscoveryComplete = null;
    private BroadcastReceiver mReceiver = null;
    private SharedPreferences preferences = null;
    private boolean isDeviceDiscoveryInProgress = false;
    private BroadcastReceiver bluetoothEnableReceiver;

    private SearchContract.FragmentView fragmentView;
    private SearchContract.ActivityView activityView;

    SearchPresenter(SharedPreferences preferences, SearchContract.ActivityView activityView, SearchContract.FragmentView fragmentView) {
        this.preferences = preferences;
        this.activityView = activityView;
        this.fragmentView = fragmentView;

    }

    @Override
    public void setFragmentView(SearchContract.FragmentView view) {
        fragmentView = view;
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
        Log.d(TAG, "broadcastDefine: start");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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
                    fragmentView.enableSearchButton(true);
                    setDeviceDiscoveryInProgress(false);
                    onDiscoveryComplete.onDiscoveryFinish();
                }
                Log.d(TAG, "onReceive: end");
            }
        };
        Log.d(TAG, "broadcastDefine: end");
    }

    @Override
    public void defineBluetoothEnableBroadcast() {
        Log.d(TAG, "defineBluetoothEnableBroadcast: start");
        bluetoothEnableReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: Intent action ===> " + intent.getAction());
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                    if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        Log.d(TAG, "onReceive: Enabled");
                        fragmentView.showProgressFragment(true);
                        registerBroadcast();
                    }
                    Log.d(TAG, "onReceive: Bluetooth state is changed in enable");
                }
            }
        };


        Log.d(TAG, "defineBluetoothEnableBroadcast: end");
    }

    @Override
    public void showSearchFragment(FragmentManager fragmentManager, boolean show) {
        Log.d(TAG, "showSearchFragment: start");
        Log.d(TAG, "showSearchFragment: Fragment to show ===> " + show);
        SearchProgressFragment searchProgressFragment = (SearchProgressFragment) fragmentManager.findFragmentById(R.id.search_fragment_container);
        FragmentTransaction transaction = null;
        if (searchProgressFragment != null) {
            Log.d(TAG, "showSearchFragment: Search fragment is NOT null");
            transaction = fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            Log.d(TAG, "showSearchFragment: Search fragment is NULL");
        }

        if (transaction != null) {
            if (show) {
                Log.d(TAG, "showSearchFragment: Showing the fragment");
                transaction.show(searchProgressFragment).commit();
            } else {
                Log.d(TAG, "showSearchFragment: Hide the fragment");
                transaction.hide(searchProgressFragment).commit();
            }
        }
        Log.d(TAG, "showSearchFragment: end");
    }


    @Override
    public IntentFilter getBlutoothDiscoveryIntent() {
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        return filter;
    }

    @Override
    public void setOnDiscoveryComplete(OnDiscoveryComplete onDiscoveryComplete) {
        this.onDiscoveryComplete = onDiscoveryComplete;
    }

    @Override
    public BroadcastReceiver getDiscoveryReceiver() {
        return this.mReceiver;
    }

    @Override
    public void setDiscoveryReceiver(BroadcastReceiver receiver) {
        this.mReceiver = receiver;
        Log.e(TAG, "setDiscoveryReceiver: Make broadcast NULL");
    }

    @Override
    public OnDiscoveryComplete getOnDiscoveryComplete() {
        return this.onDiscoveryComplete;
    }

    @Override
    public Intent getSettingsIntent(Uri uri) {
        Intent appSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        appSettings.setData(uri);
        return appSettings;
    }

    @Override
    public void registerBroadcast() {
        activityView.registerBluetoothBroadcast();
        fragmentView.enableSearchButton(false);
    }

    @Override
    public boolean isDeviceDiscoveryInProgress() {
        return isDeviceDiscoveryInProgress;
    }

    @Override
    public void setDeviceDiscoveryInProgress(boolean deviceDiscoveryInProgress) {
        isDeviceDiscoveryInProgress = deviceDiscoveryInProgress;
    }

    @Override
    public BroadcastReceiver getBluetoothEnableReceiver() {
        return bluetoothEnableReceiver;
    }

    @Override
    public void setBluetoothEnableReceiver(BroadcastReceiver bluetoothEnableReceiver) {
        this.bluetoothEnableReceiver = bluetoothEnableReceiver;
    }

    interface OnDiscoveryComplete {
        void onDiscoveryComplete(BluetoothDevice bluetoothDevice);

        void onDiscoveryFinish();
    }
}
