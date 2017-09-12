package com.example.rajasaboor.bluetoothprototype.search;

import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;

import com.example.rajasaboor.bluetoothprototype.list.DevicesListFragment;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

public interface SearchContract {
    interface FragmentView {
        void invokePermissions();

        void permissionsValidation();

        void registerBluetoothBroadcast();

        void openBluetoothPermissionIntent();

        void openAppSettings();

        void unregisterBroadcast();

        void changeSearchingTextToNoDeviceFound(boolean noDeviceFound);

        DevicesListFragment getTheViewInstanceOrNewOne();
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
