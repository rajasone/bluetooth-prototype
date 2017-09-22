package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.rajasaboor.bluetoothprototype.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class SearchPresenter implements SearchContract.Presenter {
    private static final String TAG = SearchPresenter.class.getSimpleName();
    private BroadcastReceiver bluetoothDiscoveryReceiver = null;
    private boolean isDeviceDiscoveryInProgress = false;
    private BroadcastReceiver bluetoothEnableReceiver = null;

    private final SearchContract.FragmentView fragmentView;
    private final SearchContract.ActivityView activityView;

    private List<BluetoothDevice> discoveryDevicesList = new ArrayList<>();

    SearchPresenter(SearchContract.ActivityView activityView, SearchContract.FragmentView fragmentView) {
        this.activityView = activityView;
        this.fragmentView = fragmentView;
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
        bluetoothDiscoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: start");
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: Device Found");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if ((device.getAddress() != null)) {
                        discoveryDevicesList.add(device);
                        fragmentView.updateListSize(discoveryDevicesList.size(), false);
                        fragmentView.showAvailableDeviceInRecyclerView(discoveryDevicesList, true);
                    } else {
                        Log.e(TAG, "onReceive: Device is not full filling the condition ===> " + device.getName());
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: Discovery End");
                    fragmentView.updateListSize(discoveryDevicesList.size(), false);
                    setDeviceDiscoveryInProgress(false);
                    fragmentView.enableSearchButton(true);
                    fragmentView.showDiscoveryProgressBar(false);
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
                            fragmentView.updateListSize(getPairedDevices().size(), true);
                            break;
                    }
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
        return this.bluetoothDiscoveryReceiver;
    }

    @Override
    public void setDiscoveryReceiver(BroadcastReceiver receiver) {
        this.bluetoothDiscoveryReceiver = receiver;
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
        setDeviceDiscoveryInProgress(true);
        activityView.registerBluetoothDiscoveryBroadcast();
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
    public List<BluetoothDevice> getDiscoveryDevicesList() {
        return discoveryDevicesList;
    }

    @Override
    public void setDiscoveryDevicesList(List<BluetoothDevice> discoveryDevicesList) {
        this.discoveryDevicesList = discoveryDevicesList;
    }

    @Override
    public void onRecyclerViewTapped(int position, boolean isPairedAdapter, boolean isSettingsTapped) {
        Log.d(TAG, "onRecyclerViewTapped: start");
        Log.d(TAG, "onRecyclerViewTapped: Paired Adapter --->" + isPairedAdapter);
        Log.d(TAG, "onRecyclerViewTapped: Setting Adapter --->" + isSettingsTapped);
        if ((!isPairedAdapter) && (!isSettingsTapped)) {
            fragmentView.showToast((TextUtils.isEmpty(discoveryDevicesList.get(position).getName()) ? discoveryDevicesList.get(position).getAddress() : discoveryDevicesList.get(position).getName()));
        } else if ((isPairedAdapter) && (isSettingsTapped)) {
            fragmentView.showToast("Settings tapped");
        } else {
            fragmentView.showToast((TextUtils.isEmpty(getPairedDevices().get(position).getName()) ? getPairedDevices().get(position).getAddress() : getPairedDevices().get(position).getName()));
        }
        Log.d(TAG, "onRecyclerViewTapped: end");
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: start");
        switch (view.getId()) {
            case R.id.search_bluetooth_button:
                if (isDeviceHaveBluetooth()) {
                    fragmentView.permissionsValidation(Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (fragmentView.isDeviceHaveBluetoothAndPermissionGranted()) {
                        fragmentView.showDiscoveryProgressBar(true);
                        fragmentView.resetListSizeTextViews();
                        fragmentView.updateListSize(discoveryDevicesList.size(), false);
                        fragmentView.resetAdapter(false);
                        registerBroadcast();
                    } else {
                        fragmentView.showToast("Enable bluetooth");
                    }
                } else {
                    Log.e(TAG, "onClick: Device have no Bluetooth");
                }
                break;
        }
        Log.d(TAG, "onClick: end");
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
            fragmentView.resetAdapter(false);
            activityView.unregisterBluetoothDiscoveryBroadcast();
            fragmentView.resetListSizeTextViews();
        }
        Log.d(TAG, "onCheckedChanged: end");
    }
}
