package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;

import com.example.rajasaboor.bluetoothprototype.adapter.PairedDevicesAdapter;
import com.example.rajasaboor.bluetoothprototype.communication.BluetoothConnectionService;

import java.util.List;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

interface SearchContract {
    interface ActivityView {
        void unregisterBluetoothDiscoveryBroadcast();

        void registerPairBroadcast();

        Application getApplicationInstance();

        void checkBluetoothSwitch(boolean check);

        void registerBroadcastReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter);
    }

    interface FragmentView {
        void enableSearchButton(boolean enable);

        void showViews(boolean bluetoothOnViews);

        void showAvailableDeviceInRecyclerView(List<BluetoothDevice> deviceList, boolean isDiscoverAdapter);

        void showToast(String message, int resourceID);

        void updateListSize(int listSize, boolean isPairedList);

        void showDiscoveryProgressBar(boolean show);

        void startChatActivity();

        void isSelectedDeviceIsReachable();
    }

    interface Presenter {

        boolean isDeviceHaveBluetooth();

        boolean isDeviceBluetoothIsTurnedOn();

        boolean isPermissionGranted(PackageManager manager, String permission, String packageName);

        void broadcastDefine();

        IntentFilter getBlutoothDiscoveryIntent();

        BroadcastReceiver getDiscoveryReceiver();

        void setDiscoveryReceiver(BroadcastReceiver receiver);

        Intent getSettingsIntent(Uri uri);

        void registerDeviceDiscoveryBroadcast();

        boolean isDeviceDiscoveryInProgress();

        void setDeviceDiscoveryInProgress(boolean deviceDiscoveryInProgress);

        BroadcastReceiver getBluetoothEnableReceiver();

        void setBluetoothEnableReceiver(BroadcastReceiver bluetoothEnableReceiver);

        void defineBluetoothEnableBroadcast();

        void turnOnBluetooth(boolean turnOn);

        List<BluetoothDevice> getPairedDevices();

        List<BluetoothDevice> getDiscoveryDevicesList();

        void setDiscoveryDevicesList(List<BluetoothDevice> discoveryDevicesList);

        void unpairDevice(BluetoothDevice device);

        void pairDevice(BluetoothDevice device);

        void definePairBroadcast();

        BroadcastReceiver getPairBroadcast();

        void registerPairBroadcast();

        void setPairBroadcast(BroadcastReceiver pairBroadcastReceiver);

        void unregisterBluetoothDiscoveryBroadcast();

        boolean isDeviceDiscoveryForChatActivity();

        void setDeviceDiscoveryForChatActivity(boolean deviceDiscoveryForChatActivity);

        BluetoothDevice getSelectedDevice();

        void setSelectedDevice(BluetoothDevice selectedDevice);

        Handler getHandler();

        void defineHandler();

        void registerDeviceDiscoveryAndStartService();

        void checkAndRegisterBluetoothEnableReceiver();

        void loadViewBasedOnBluetoothState();

        void checkAndDefineBluetoothDiscoveryBroadcast();
    }
}
