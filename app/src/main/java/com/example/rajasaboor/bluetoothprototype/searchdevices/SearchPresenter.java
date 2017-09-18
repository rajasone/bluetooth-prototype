package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListContract;


/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class SearchPresenter implements SearchContract.Presenter {
    private static final String TAG = SearchPresenter.class.getSimpleName();
    private BroadcastReceiver mReceiver = null;
    private boolean isDeviceDiscoveryInProgress = false;
    private BroadcastReceiver bluetoothEnableReceiver;

    private final SearchContract.FragmentView fragmentView;
    private final SearchContract.ActivityView activityView;
    private final DevicesListContract.Presenter listPresenter;

    SearchPresenter(SearchContract.ActivityView activityView, SearchContract.FragmentView fragmentView, DevicesListContract.Presenter listPresenter) {
        this.activityView = activityView;
        this.fragmentView = fragmentView;
        this.listPresenter = listPresenter;

    }
//
//    @Override
//    public void setFragmentView(SearchContract.FragmentView view) {
//        fragmentView = view;
//    }

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
                        listPresenter.addBluetoothDeviceInList(device);
                        listPresenter.refreshListAdapter();
                    } else {
                        Log.e(TAG, "onReceive: Device is not full filling the condition ===> " + device.getName());
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: Discovery End");
                    fragmentView.enableSearchButton(true);
                    setDeviceDiscoveryInProgress(false);
                    listPresenter.onDeviceDiscoveryComplete();
                    fragmentView.showSearchFragment(false);
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
                        registerBroadcast();
                    }
                    Log.d(TAG, "onReceive: Bluetooth state is changed in enable");
                }
            }
        };


        Log.d(TAG, "defineBluetoothEnableBroadcast: end");
    }

    @Override
    public IntentFilter getBlutoothDiscoveryIntent() {
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        return filter;
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
}
