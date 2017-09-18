package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListContract;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

public interface SearchContract {
    interface ActivityView {
        void registerBluetoothBroadcast();
    }

    interface FragmentView {
        void enableSearchButton(boolean enable);

        void showSearchProgressFragment(boolean show);
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

        void registerBroadcast();

        boolean isDeviceDiscoveryInProgress();

        void setDeviceDiscoveryInProgress(boolean deviceDiscoveryInProgress);

        BroadcastReceiver getBluetoothEnableReceiver();

        void setBluetoothEnableReceiver(BroadcastReceiver bluetoothEnableReceiver);

        void defineBluetoothEnableBroadcast();

        DevicesListContract.Presenter getListPresenter();
    }
}
