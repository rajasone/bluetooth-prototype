package com.example.rajasaboor.bluetoothprototype.handler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class DeviceConnection extends Thread {
    private final BluetoothSocket socket;
    private final BluetoothDevice device;
    private final UUID uuid;

    public DeviceConnection(BluetoothDevice device, UUID uuid) {
        this.device = device;
        this.uuid = uuid;
        BluetoothSocket temp = null;

        try {
            temp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = temp;
    }

    @Override
    public void run() {
        super.run();
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
