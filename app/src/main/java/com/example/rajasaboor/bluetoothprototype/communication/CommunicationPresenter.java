package com.example.rajasaboor.bluetoothprototype.communication;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.rajasaboor.bluetoothprototype.BluetoothApplication;
import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.model.Message;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public class CommunicationPresenter implements CommunicationContract.Presenter, BluetoothConnectionService.MessageListener, ImageLoadingListener {
    private static final String TAG = CommunicationFragment.class.getSimpleName();
    private CommunicationContract.FragmentView fragmentView;
    private CommunicationContract.ActivityView activityView;
    private BluetoothConnectionService bluetoothConnectionService;
    private List<Message> messageList = new ArrayList<>();
    private Bitmap selectedImageBitmap;

    public CommunicationPresenter(CommunicationContract.FragmentView fragmentView, CommunicationContract.ActivityView activityView) {
        this.fragmentView = fragmentView;
        this.activityView = activityView;
        bluetoothConnectionService = ((BluetoothApplication) activityView.getApplicationInstance()).getService();
        bluetoothConnectionService.setMessageListener(this);
        defineConversationHandler();
    }

    @Override
    public void sendMessage(String message, Uri imageUri) {
        Log.d(TAG, "sendMessage: Send this message to the device ====> " + message);
        bluetoothConnectionService.write(message.getBytes(), imageUri);
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
                Message message = null;
                Log.d(TAG, "handleMessage: Message status ====> " + messageStatus);
                try {
                    message = (Message) msg.obj;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                switch (messageStatus) {
                    case BuildConfig.MESSAGE_SENT:
                        fragmentView.resetChatEditText();
                        onMessageSent(message.getMyMessage(), message.getSelectedImageUri());
                        break;
                    case BuildConfig.MESSAGE_RECEIVED:
                        fragmentView.resetChatEditText();
                        onMessageReceived(message.getSenderMessage(), message.getSelectedImageUri());
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
    public byte[] convertBitmapIntoBytesArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
        Log.d(TAG, "convertBitmapIntoBytesArray: Length of the byte array ===> " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    @Override
    public void onMessageReceived(String message, Uri selectedImageUri) {
        messageList.add(new Message(null, message.trim(), System.currentTimeMillis(), selectedImageUri));
        fragmentView.updateConversationAdapter(messageList);
    }

    @Override
    public void onMessageSent(String message, Uri selectedImageUri) {
        messageList.add(new Message(message.trim(), null, System.currentTimeMillis(), selectedImageUri));

        if (selectedImageUri != null) {
            Log.d(TAG, "onMessageSent: IMAGE IS SENT");
        } else {
            Log.e(TAG, "onMessageSent: STRING IS SENT");
        }
        fragmentView.updateConversationAdapter(messageList);
    }

    @Override
    public Bitmap getSelectedImageBitmap() {
        return this.selectedImageBitmap;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        Log.d(TAG, "onLoadingComplete: start");
        selectedImageBitmap = loadedImage;
        // TODO: 10/2/2017 Calling the send imahe from here
        sendMessage(new String(convertBitmapIntoBytesArray(selectedImageBitmap)), Uri.parse(imageUri));
        Log.d(TAG, "onLoadingComplete: end");

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
