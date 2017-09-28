package com.example.rajasaboor.bluetoothprototype.communication;

import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BluetoothApplication;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public class CommunicationPresenter implements CommunicationContract.Presenter {
    private static final String TAG = CommunicationFragment.class.getSimpleName();
    private CommunicationContract.FragmentView fragmentView;
    private CommunicationContract.ActivityView activityView;
    private BluetoothConnectionService bluetoothConnectionService;

    public CommunicationPresenter(CommunicationContract.FragmentView fragmentView, CommunicationContract.ActivityView activityView) {
        this.fragmentView = fragmentView;
        this.activityView = activityView;

        bluetoothConnectionService = ((BluetoothApplication) activityView.getApplicationInstance()).getService();

    }

    @Override
    public void sendMessage(String message) {
        Log.d(TAG, "sendMessage: Send this message to the device ====> " + message);
        bluetoothConnectionService.write(message.getBytes());
    }

}
