package com.example.rajasaboor.bluetoothprototype.fragments;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

public interface Contract {
    interface FragmentView {
        void invokePermissions();

        void permissionsValidation();

        void registerBluetoothBroadcast();

        void openBluetoothPermissionIntent();

        void openAppSettings();

        void unregisterBroadcast();

        void changeSearchingTextToNoDeviceFound(boolean noDeviceFound);
    }

    interface Presenter {
        void deleteSharedPrefs();

        boolean getSharedPreferences();

        void setSharedPreferences();

        boolean isDeviceHaveBluetooth();

        boolean isDeviceBluetoothIsTurnedOn();

        boolean isPermissionGranted(PackageManager manager, String permission, String packageName);

        void broadcastDefine();

        void showSearchFragment(FragmentManager fragmentManager, boolean show);
    }
}
