package com.example.rajasaboor.bluetoothprototype.communication;

import android.app.Application;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.example.rajasaboor.bluetoothprototype.model.Message;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

interface CommunicationContract {
    interface ActivityView {
        Application getApplicationInstance();

        ContentResolver getContentResolverInstance();
    }

    interface FragmentView {
        void updateConversationAdapter(List<Message> conversationList);

        void showToast(String message, int messageID);

        void resetChatEditText();
    }

    interface Presenter {
        void sendMessage(String message, Uri imageUri);

        List<Message> getMessageList();

        void setMessageList(List<Message> messageList);

        void defineConversationHandler();

        BluetoothConnectionService getBluetoothConnectionService();

        String convertBitmapIntoBytesArray(Bitmap bitmap);

        Bitmap getSelectedImageBitmap();
    }
}
