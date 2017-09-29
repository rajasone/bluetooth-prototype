package com.example.rajasaboor.bluetoothprototype.communication;

import android.app.Application;
import android.os.Handler;
import android.view.View;

import com.example.rajasaboor.bluetoothprototype.model.Message;

import java.util.List;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

interface CommunicationContract {
    interface ActivityView {
        Application getApplicationInstance();
    }

    interface FragmentView {
        void updateConversationAdapter(List<Message> conversationList);

        void showToast(String message, int messageID);

        void resetChatEditText();
    }

    interface Presenter {
        void sendMessage(String message);

        List<Message> getMessageList();

        void setMessageList(List<Message> messageList);

        void defineConversationHandler();

        BluetoothConnectionService getBluetoothConnectionService();
    }
}
