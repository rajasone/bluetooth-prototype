package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.communication.BluetoothConnectionService;

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

    private boolean isDeviceDiscoveryForChatActivity;
    private BluetoothDevice selectedDevice;

    private BluetoothConnectionService connectionService;
    private Handler handler;


    SearchPresenter(SearchContract.ActivityView activityView, SearchContract.FragmentView fragmentView) {
        this.activityView = activityView;
        this.fragmentView = fragmentView;

        defineHandler();
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
                    if (isDeviceDiscoveryForChatActivity()) {
                        fragmentView.isSelectedDeviceIsReachable();
                    }
                    setDeviceDiscoveryForChatActivity(false);
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
                            setConnectionService(new BluetoothConnectionService(getHandler()));
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
    public void registerDeviceDiscoveryBroadcast() {
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
        activityView.registerPairBroadcast();
    }

    @Override
    public void setPairBroadcast(BroadcastReceiver pairBroadcastReceiver) {
        this.pairAndUnpairBluetoothBroadcast = pairBroadcastReceiver;
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
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                    if (currentState == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "onReceive: Device PAIRED");
                        fragmentView.showToast(device.getName() != null ? device.getName() : device.getAddress(), R.string.pair_msg);
                        fragmentView.showAvailableDeviceInRecyclerView(getPairedDevices(), false);
                        fragmentView.updateListSize(getPairedDevices().size(), true);
                    } else if (currentState == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDING) {
                        Log.e(TAG, "onReceive: Cancel pressed");
                        fragmentView.showToast(device.getName() != null ? device.getName() : device.getAddress(), R.string.pair_cancel_msg);
                    } else if (currentState == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "onReceive: Device UNPAIRED");
                        fragmentView.showToast(device.getName() != null ? device.getName() : device.getAddress(), R.string.unpair_msg);
                        fragmentView.updateListSize(getPairedDevices().size(), true);
                        fragmentView.showAvailableDeviceInRecyclerView(getPairedDevices(), false);
                    }
                }
            }
        };
        Log.d(TAG, "definePairBroadcast: end");
    }

    @Override
    public void unregisterBluetoothDiscoveryBroadcast() {
        activityView.unregisterBluetoothDiscoveryBroadcast();
    }

    @Override
    public boolean isDeviceDiscoveryForChatActivity() {
        return isDeviceDiscoveryForChatActivity;
    }

    @Override
    public void setDeviceDiscoveryForChatActivity(boolean deviceDiscoveryForChatActivity) {
        this.isDeviceDiscoveryForChatActivity = deviceDiscoveryForChatActivity;
    }

    @Override
    public BluetoothDevice getSelectedDevice() {
        return selectedDevice;
    }

    @Override
    public void setSelectedDevice(BluetoothDevice selectedDevice) {
        this.selectedDevice = selectedDevice;
    }


    @Override
    public BluetoothConnectionService getConnectionService() {
        return connectionService;
    }

    @Override
    public void setConnectionService(BluetoothConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void defineHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.arg1 == BuildConfig.CONNECTED_SUCCESSFULLY) {
                    fragmentView.showToast("Connected with " + (((BluetoothDevice) message.obj).getName() != null ? ((BluetoothDevice) message.obj).getName() : ((BluetoothDevice) message.obj).getAddress()), BuildConfig.NO_RESOURCE);
                    fragmentView.startChatActivity();
                } else {
                    if (message.obj != null) {
                        fragmentView.showToast("Failed to connect with " + (((BluetoothDevice) message.obj).getName() != null ? ((BluetoothDevice) message.obj).getName() : ((BluetoothDevice) message.obj).getAddress()), BuildConfig.NO_RESOURCE);
                    }
//                    else {
//                        fragmentView.showToast("Failed to connect", BuildConfig.NO_RESOURCE);
//                    }
                }
                return true;
            }
        });
    }
}
