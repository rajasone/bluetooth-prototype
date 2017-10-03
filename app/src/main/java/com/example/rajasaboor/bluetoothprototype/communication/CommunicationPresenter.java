package com.example.rajasaboor.bluetoothprototype.communication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.rajasaboor.bluetoothprototype.BluetoothApplication;
import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.model.Message;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
                        onMessageSent(message);
                        break;
                    case BuildConfig.MESSAGE_RECEIVED:
                        fragmentView.resetChatEditText();
                        onMessageReceived(message);
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
    public void saveBitmapToFile(Bitmap bitmap, OutputStream outputStream) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
    }

    @Override
    public String getEncodedStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

    }

    @Override
    public void onMessageReceived(Message message) {
        if (message.getSenderMessage() != null) {
            Log.d(TAG, "onMessageReceived: TEXT RECEIVED");
            messageList.add(new Message(false, null, message.getSenderMessage(), System.currentTimeMillis(), null));
        } else {
            Log.d(TAG, "onMessageReceived: IMAGE RECEIVED");
            // TODO: 10/3/2017 start the image save process
            messageList.add(new Message(false, null, message.getSenderMessage(), System.currentTimeMillis(), saveReceivedImageInInternalStorage(message.getSelectedImageUri().toString().getBytes())));
        }
        fragmentView.updateConversationAdapter(messageList);
    }

    @Override
    public void onMessageSent(Message message) {
        if (message.getMyMessage() != null) {
            messageList.add(new Message(true, message.getMyMessage(), null, message.getMessageTime(), null));
            Log.e(TAG, "onMessageSent: STRING IS SENT");
        } else {
            messageList.add(new Message(true, null, null, message.getMessageTime(), message.getSelectedImageUri()));
            Log.d(TAG, "onMessageSent: IMAGE IS SENT");
        }
        fragmentView.updateConversationAdapter(messageList);
    }


    @Override
    public Uri saveReceivedImageInInternalStorage(byte[] imageInByte) {
        Uri imageUri = null;
        if (fragmentView.getImagesDirectory() != null) {
            imageUri = convertBitmapIntoFile(convertBytesArrayIntoImage(imageInByte));
        } else {
            Log.e(TAG, "saveReceivedImageInInternalStorage: Directory for images is not created");
        }

        return imageUri;
    }


    @Override
    public Uri convertBitmapIntoFile(Bitmap bitmap) {
        File pathFile = new File(fragmentView.getImagesDirectory(), "test_image_1.jpg");
        FileOutputStream outputStream = null;
        Uri imageUri = null;
        try {
            outputStream = new FileOutputStream(pathFile);
            saveBitmapToFile(bitmap, outputStream);
            imageUri = Uri.fromFile(pathFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return imageUri;
    }

    private Bitmap convertBytesArrayIntoImage(byte[] rawImage) {
        Bitmap bitmap = null;

        try {
            byte[] decodedString = Base64.decode(rawImage, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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
        // TODO: 10/2/2017 Calling the send imahe from here
        sendMessage(getEncodedStringFromBitmap(loadedImage), Uri.parse(imageUri));

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
