package com.example.rajasaboor.bluetoothprototype.discoverdeviceslist;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;

import java.util.List;
import java.util.Set;

/**
 * Created by rajaSaboor on 9/12/2017.
 */

public interface DevicesListContract {
    interface View {
        void initAdapter();

        void resetDeviceListAdapter();

        void refreshListAdapter();
    }

    interface Presenter {
        List<BluetoothDevice> getDeviceList();

        BroadcastReceiver getBluetoothReceiver();

        boolean isNewDeviceFound();

        void setNewDeviceFound(boolean newDeviceFound);

        void addNameInListFromBluetoothList(List<BluetoothDevice> deviceList);

        void addBluetoothDeviceInList(BluetoothDevice device);

        void handleListClick(int position);

        void pairDevice(BluetoothDevice device);

        void unpairDevice(BluetoothDevice device);

        void pairingProcessBroadcast();

        void saveThePairedDevice(BluetoothDevice device, boolean save);

        Set<String> getThePairedDevicesFromSharedPrefs();

        boolean isDeviceIsPaired(BluetoothDevice device);
    }
}
