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
import android.view.View;
import android.widget.CompoundButton;

import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListContract;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class SearchPresenter implements SearchContract.Presenter {
    private static final String TAG = SearchPresenter.class.getSimpleName();
    private BroadcastReceiver mReceiver = null;
    private boolean isDeviceDiscoveryInProgress = false;
    private BroadcastReceiver bluetoothEnableReceiver = null;

    private final SearchContract.FragmentView fragmentView;
    private final SearchContract.ActivityView activityView;
    private final DevicesListContract.Presenter listPresenter;

    private List<BluetoothDevice> discoveryDevicesList = new ArrayList<>();

    SearchPresenter(SearchContract.ActivityView activityView, SearchContract.FragmentView fragmentView, DevicesListContract.Presenter listPresenter) {
        this.activityView = activityView;
        this.fragmentView = fragmentView;
        this.listPresenter = listPresenter;

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
                        discoveryDevicesList.add(device);
                        fragmentView.showAvailableDeviceInRecyclerView(discoveryDevicesList, true);
                        //  listPresenter.addBluetoothDeviceInList(device);
//                        listPresenter.refreshListAdapter();
                    } else {
                        Log.e(TAG, "onReceive: Device is not full filling the condition ===> " + device.getName());
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: Discovery End");
                    fragmentView.enableSearchButton(true);
//                    setDeviceDiscoveryInProgress(false);
                    //listPresenter.onDeviceDiscoveryComplete();
//                    fragmentView.showSearchProgressFragment(false);
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
                final String action = intent.getAction();
                Log.d(TAG, "onReceive: Enable action ===> " + action);
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    Log.d(TAG, "onReceive: State ====> " + state);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(TAG, "onReceive: OFF");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG, "onReceive: ON");
                            fragmentView.showAvailableDeviceInRecyclerView(getPairedDevices(), false);
                            registerBroadcast();
                            break;
                    }
                }
            }
        };
        Log.d(TAG, "defineBluetoothEnableBroadcast: end");
    }

    @Override
    public DevicesListContract.Presenter getListPresenter() {
        return this.listPresenter;
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

    @Override
    public void turnOnBluetooth(boolean turnOn) {
        if (turnOn) {
            BluetoothAdapter.getDefaultAdapter().enable();
        } else {
            BluetoothAdapter.getDefaultAdapter().disable();
        }
    }

    @Override
    public List<BluetoothDevice> getPairedDevices() {
        return new ArrayList<>(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
    }

    @Override
    public List<BluetoothDevice> discoveryDevicesList() {
        return discoveryDevicesList;
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged: start");

        if (isChecked) {
            fragmentView.showViews(true);
            turnOnBluetooth(true);
        } else {
            fragmentView.showViews(false);
            turnOnBluetooth(false);
        }

        Log.d(TAG, "onCheckedChanged: end");
    }
}
