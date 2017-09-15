package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.FragmentManager;

import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListFragment;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

public interface SearchContract {
    interface ActivityView {
        void registerBluetoothBroadcast();
    }

    interface FragmentView {
        void enableSearchButton(boolean enable);

        void showProgressFragment(boolean show);
    }

    interface Presenter {
        void setFragmentView(FragmentView view);

        void deleteSharedPrefs();

        boolean getSharedPreferences();

        void setSharedPreferences();

        boolean isDeviceHaveBluetooth();

        boolean isDeviceBluetoothIsTurnedOn();

        boolean isPermissionGranted(PackageManager manager, String permission, String packageName);

        void broadcastDefine();

        void showSearchFragment(FragmentManager fragmentManager, boolean show);

        IntentFilter getBlutoothDiscoveryIntent();

        BroadcastReceiver getDiscoveryReceiver();

        void setDiscoveryReceiver(BroadcastReceiver receiver);

        SearchPresenter.OnDiscoveryComplete getOnDiscoveryComplete();

        void setOnDiscoveryComplete(SearchPresenter.OnDiscoveryComplete onDiscoveryComplete);

        Intent getSettingsIntent(Uri uri);

        void registerBroadcast();

        boolean isDeviceDiscoveryInProgress();

        void setDeviceDiscoveryInProgress(boolean deviceDiscoveryInProgress);

        BroadcastReceiver getBluetoothEnableReceiver();

        void setBluetoothEnableReceiver(BroadcastReceiver bluetoothEnableReceiver);

        void defineBluetoothEnableBroadcast();
    }
}
