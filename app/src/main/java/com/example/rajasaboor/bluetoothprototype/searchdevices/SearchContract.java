package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.CompoundButton;

import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListContract;

import java.util.List;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

public interface SearchContract {
    interface ActivityView {
        void registerBluetoothBroadcast();
    }

    interface FragmentView {
        void enableSearchButton(boolean enable);

//        void showSearchProgressFragment(boolean show);

        void showViews(boolean bluetoothOnViews);

        void showAvailableDeviceInRecyclerView(List<BluetoothDevice> deviceList, boolean isDiscoverAdapter);

    }

    interface Presenter extends View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        boolean isDeviceHaveBluetooth();

        boolean isDeviceBluetoothIsTurnedOn();

        boolean isPermissionGranted(PackageManager manager, String permission, String packageName);

        void broadcastDefine();

        IntentFilter getBlutoothDiscoveryIntent();

        BroadcastReceiver getDiscoveryReceiver();

        void setDiscoveryReceiver(BroadcastReceiver receiver);

        Intent getSettingsIntent(Uri uri);

        void registerBroadcast();

        boolean isDeviceDiscoveryInProgress();

        void setDeviceDiscoveryInProgress(boolean deviceDiscoveryInProgress);

        BroadcastReceiver getBluetoothEnableReceiver();

        void setBluetoothEnableReceiver(BroadcastReceiver bluetoothEnableReceiver);

        void defineBluetoothEnableBroadcast();

        DevicesListContract.Presenter getListPresenter();

        @Override
        void onClick(View view);

        void turnOnBluetooth(boolean turnOn);

        @Override
        void onCheckedChanged(CompoundButton compoundButton, boolean b);

        List<BluetoothDevice> getPairedDevices();

        List<BluetoothDevice> discoveryDevicesList();
    }
}
