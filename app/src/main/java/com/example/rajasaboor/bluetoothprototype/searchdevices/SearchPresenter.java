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
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.rajasaboor.bluetoothprototype.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private BroadcastReceiver pairAndUnpairBluetoothBroadcast;

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

                    if ((device.getAddress() != null) && (!discoveryDevicesList.contains(device))) {
                        Log.d(TAG, "onReceive: Device Type ---> " + device.getBluetoothClass().getDeviceClass());
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
    public BroadcastReceiver getPairBroadcast() {
        return this.pairAndUnpairBluetoothBroadcast;
    }


    @Override
    public void registerPairBroadcast() {
        activityView.registerBroadcast();
    }

    @Override
    public void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRecyclerViewTapped(int position, boolean isPairedAdapter, boolean isSettingsTapped) {
        Log.d(TAG, "onRecyclerViewTapped: start");
        Log.d(TAG, "onRecyclerViewTapped: Paired Adapter --->" + isPairedAdapter);
        Log.d(TAG, "onRecyclerViewTapped: Setting Adapter --->" + isSettingsTapped);

        if ((!isPairedAdapter) && (!isSettingsTapped)) {
            Log.d(TAG, "onRecyclerViewTapped: in If");
//            fragmentView.showToast((TextUtils.isEmpty(discoveryDevicesList.get(position).getName()) ? discoveryDevicesList.get(position).getAddress() :
//                    discoveryDevicesList.get(position).getName()));
            if (getPairedDevices().contains(discoveryDevicesList.get(position))) {
                Log.d(TAG, "onRecyclerViewTapped: Device is already Paired");
            } else {
                Log.d(TAG, "onRecyclerViewTapped: Pair the Device");
                if (getPairBroadcast() == null) {
                    definePairBroadcast();
                }
                registerPairBroadcast();
                pairDevice(discoveryDevicesList.get(position));
            }
        } else if ((isPairedAdapter) && (isSettingsTapped)) {
            fragmentView.showToast("Settings tapped");
        } else {
            Log.d(TAG, "onRecyclerViewTapped: in ELSE");
//            fragmentView.showToast((TextUtils.isEmpty(getPairedDevices().get(position).getName()) ? getPairedDevices().get(position).getAddress() :
//                    getPairedDevices().get(position).getName()));

        }
        Log.d(TAG, "onRecyclerViewTapped: end");
    }

    @Override
    public void createPopUpMenu(int position, View view) {
        fragmentView.showPopUpMenu(getPairedDevices().get(position), view);
    }

    @Override
    public void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void definePairBroadcast() {
        Log.d(TAG, "definePairBroadcast: start");
        pairAndUnpairBluetoothBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: Intent action ===> " + intent.getAction());
                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                    int currentState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    Log.d(TAG, "onReceive: Current state ====> " + currentState);
                    Log.d(TAG, "onReceive: Previous state ====> " + previousState);

                    if (currentState == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "onReceive: Device PAIRED");
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        fragmentView.showToast((device.getName() != null ? device.getName() : device.getAddress() + " paired successfully"));
                        fragmentView.showAvailableDeviceInRecyclerView(getPairedDevices(), false);
                    } else if (currentState == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDING) {
                        Log.e(TAG, "onReceive: Cancel pressed");
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                        fragmentView.showToast((device.getName() != null ? device.getName() : device.getAddress() + " paired un successfully"));
                        fragmentView.showToast("paired un successfully");
                    } else if (currentState == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "onReceive: Device UNPAIRED");
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        fragmentView.showToast((device.getName() != null ? device.getName() : device.getAddress() + " Un paired successfully"));
                        fragmentView.showAvailableDeviceInRecyclerView(getPairedDevices(), false);
                    }
                }
            }
        };
        Log.d(TAG, "definePairBroadcast: end");
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
