package com.example.rajasaboor.bluetoothprototype.chat;

import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.connectionmanager.ConnectionManager;

/**
 * Created by rajaSaboor on 9/20/2017.
 */

public class ChatPresenter implements ChatContract.Presenter {
    public static final String TAG = ChatPresenter.class.getSimpleName();
    private ConnectionManager manager;

    @Override
    public void onSendButtonClick(String text) {
        Log.d(TAG, "onSendButtonClick: start");
        Log.d(TAG, "onSendButtonClick: Get this text for send ---> " + text);
        Log.d(TAG, "onSendButtonClick: end");
    }

    @Override
    public void setConnectionManager() {
        manager = new ConnectionManager();
        manager.start();
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return manager;
    }

    @Override
    public void startCommunication() {
        //manager.setConnectedHandler();
    }
}
