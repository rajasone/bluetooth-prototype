package com.example.rajasaboor.bluetoothprototype.list;

import android.bluetooth.BluetoothDevice;

import java.util.List;

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
        void addNameInListFromBluetoothList(List<BluetoothDevice> deviceList);

        void addBluetoothDeviceInList(BluetoothDevice device);

        void handleListClick(int position);

        void pairDevice(BluetoothDevice device);

        void unpairDevice(BluetoothDevice device);

        void pairingProcessBroadcast();
    }
}
