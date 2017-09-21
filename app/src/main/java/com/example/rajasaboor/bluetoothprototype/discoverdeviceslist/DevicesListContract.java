package com.example.rajasaboor.bluetoothprototype.discoverdeviceslist;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.os.Handler;

import com.example.rajasaboor.bluetoothprototype.connectionmanager.ConnectionManager;

import java.util.List;

/**
 * Created by rajaSaboor on 9/12/2017.
 */

public interface DevicesListContract {
    interface FragmentView {
        void resetDeviceListAdapter();

        void refreshListAdapter();

        void showToast(String message);

        void startChatActivity();
    }

    interface Presenter {
        List<BluetoothDevice> getDeviceList();

        BroadcastReceiver getBluetoothPairReceiver();

        boolean isNewDeviceFound();

        void setNewDeviceFound(boolean newDeviceFound);

        void addNameInListFromBluetoothList(List<BluetoothDevice> deviceList);

        void addBluetoothDeviceInList(BluetoothDevice device);

        void handleListClick(int position);

        void pairDevice(BluetoothDevice device);

        void unpairDevice(BluetoothDevice device);

        void pairingProcessBroadcast();

        void setDeviceList(List<BluetoothDevice> deviceList);

        List<String> getDeviceNameList();

        void refreshListAdapter();

        void onDeviceDiscoveryComplete();

        DevicesListContract.FragmentView getDeviceListFragmentView();

        Handler getHandler();

        ConnectionManager getConnectionManager();
    }
}
