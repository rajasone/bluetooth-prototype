package com.example.rajasaboor.bluetoothprototype.chat;

import com.example.rajasaboor.bluetoothprototype.connectionmanager.ConnectionManager;

/**
 * Created by rajaSaboor on 9/20/2017.
 */

public interface ChatContract {
    interface View {

    }

    interface Presenter {
        void onSendButtonClick(String text);

        void setConnectionManager();

        ConnectionManager getConnectionManager();
    }
}
