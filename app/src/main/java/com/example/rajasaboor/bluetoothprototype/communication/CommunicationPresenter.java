package com.example.rajasaboor.bluetoothprototype.communication;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BluetoothApplication;
import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public class CommunicationPresenter implements CommunicationContract.Presenter, BluetoothConnectionService.MessageListener {
    private static final String TAG = CommunicationFragment.class.getSimpleName();
    private CommunicationContract.FragmentView fragmentView;
    private CommunicationContract.ActivityView activityView;
    private BluetoothConnectionService bluetoothConnectionService;
    private List<Message> messageList = new ArrayList<>();

    public CommunicationPresenter(CommunicationContract.FragmentView fragmentView, CommunicationContract.ActivityView activityView) {
        this.fragmentView = fragmentView;
        this.activityView = activityView;
        bluetoothConnectionService = ((BluetoothApplication) activityView.getApplicationInstance()).getService();
        bluetoothConnectionService.setMessageListener(this);
        defineConversationHandler();
    }

    @Override
    public void sendMessage(String message) {
        Log.d(TAG, "sendMessage: Send this message to the device ====> " + message);
        bluetoothConnectionService.write(message.getBytes());
    }


    @Override
    public List<Message> getMessageList() {
        return messageList;
    }

    @Override
    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public void defineConversationHandler() {
        Log.d(TAG, "defineConversationHandler: start");
        if (((BluetoothApplication) activityView.getApplicationInstance()).getService().getHandler() != null) {
            Log.d(TAG, "defineConversationHandler: Handler is not NULL | Setting it to the NULL");
            ((BluetoothApplication) activityView.getApplicationInstance()).getService().setHandler(null);
        } else {
            Log.d(TAG, "defineConversationHandler: Handler is already NULL");
        }
        ((BluetoothApplication) activityView.getApplicationInstance()).getService().setHandler(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(android.os.Message msg) {
                int messageStatus = msg.what;
                Log.d(TAG, "handleMessage: Message status ====> " + messageStatus);
                Message message = (Message) msg.obj;
                switch (messageStatus) {
                    case BuildConfig.MESSAGE_SENT:
                        fragmentView.resetChatEditText();
                        onMessageSent(message.getMyMessage());
                        break;
                    case BuildConfig.MESSAGE_RECEIVED:
                        fragmentView.resetChatEditText();
                        onMessageReceived(message.getSenderMessage());
                        break;
                    case BuildConfig.FAILED_TO_SEND_MESSAGE:
                        fragmentView.showToast(null, R.string.failed_to_send_message);
                        break;
                }
            }
        });

        Log.d(TAG, "defineConversationHandler: end");
    }

    @Override
    public BluetoothConnectionService getBluetoothConnectionService() {
        return ((BluetoothApplication) activityView.getApplicationInstance()).getService();
    }

    @Override
    public void onMessageReceived(String message) {
        messageList.add(new Message(null, message.trim(), System.currentTimeMillis()));
        fragmentView.updateConversationAdapter(messageList);
    }

    @Override
    public void onMessageSent(String message) {
        messageList.add(new Message(message.trim(), null, System.currentTimeMillis()));
        fragmentView.updateConversationAdapter(messageList);
    }
}
