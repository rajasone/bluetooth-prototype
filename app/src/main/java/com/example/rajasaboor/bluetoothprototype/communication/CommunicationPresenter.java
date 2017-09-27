package com.example.rajasaboor.bluetoothprototype.communication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public class CommunicationPresenter implements CommunicationContract.Presenter {
    private static final String TAG = CommunicationFragment.class.getSimpleName();
    private BluetoothConnectionService connectionService;
    private CommunicationContract.FragmentView fragmentView;
    private CommunicationContract.ActivityView activityView;
    private Handler handler;


    public CommunicationPresenter(CommunicationContract.FragmentView fragmentView, CommunicationContract.ActivityView activityView) {
        this.fragmentView = fragmentView;
        this.activityView = activityView;

        defineHandler();
    }

    @Override
    public BluetoothConnectionService getConnectionService() {
        return connectionService;
    }

    @Override
    public void setConnectionService(BluetoothConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void defineHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                return false;
            }
        });
    }

    @Override
    public void sendMessage(String message) {
        Log.d(TAG, "sendMessage: Send this message to the device ====> " + message);
        connectionService.write(message.getBytes());
    }

}
