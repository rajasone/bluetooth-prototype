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
import java.util.UUID;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

class CommunicationPresenter implements CommunicationContract.Presenter, BluetoothConnectionService.MessageListener, ImageLoadingListener {
    private static final String TAG = CommunicationFragment.class.getSimpleName();
    private CommunicationContract.FragmentView fragmentView;
    private CommunicationContract.ActivityView activityView;
    private BluetoothConnectionService bluetoothConnectionService;
    private List<Message> messageList = new ArrayList<>();

    CommunicationPresenter(CommunicationContract.FragmentView fragmentView, CommunicationContract.ActivityView activityView) {
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

    private void defineConversationHandler() {
        Log.d(TAG, "defineConversationHandler: start");
        ((BluetoothApplication) activityView.getApplicationInstance()).getService().setCommunicationHandler(new Handler(Looper.getMainLooper()) {
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
    public void onMessageReceived(Message message) {
        Message temp = null;
        if (message.getMyMessage() != null) {
            Log.d(TAG, "onMessageReceived: TEXT RECEIVED");
            temp = new Message(false, message.getMyMessage(), System.currentTimeMillis(), null);
            messageList.add(temp);
        } else {
            Log.d(TAG, "onMessageReceived: IMAGE RECEIVED");
            // TODO: 10/3/2017 start the image save process
            temp = new Message(false, null, System.currentTimeMillis(), saveReceivedImageInInternalStorage(message.getSelectedImageUri().toString().getBytes()));
            messageList.add(temp);
        }
        fragmentView.updateConversation(temp);
    }

    private Uri saveReceivedImageInInternalStorage(byte[] imageInByte) {
        Uri imageUri = null;
        if (fragmentView.getImagesDirectory() != null) {
            imageUri = convertBitmapIntoFile(convertBytesArrayIntoImage(imageInByte));
        } else {
            Log.e(TAG, "saveReceivedImageInInternalStorage: Directory for images is not created");
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


    private Uri convertBitmapIntoFile(Bitmap bitmap) {
        String fileName = "test_image_" + UUID.randomUUID() + ".jpg";
        Log.d(TAG, "convertBitmapIntoFile: File Name ====> " + fileName);
        File pathFile = new File(fragmentView.getImagesDirectory(), fileName);
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

    private void saveBitmapToFile(Bitmap bitmap, OutputStream outputStream) {
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageSent(Message message) {
        Message temp = null;

        if (message.getMyMessage() != null) {
            temp = new Message(true, message.getMyMessage(), message.getMessageTime(), null);
            Log.d(TAG, "onMessageSent: Size of the list before insertion ====> " + getMessageList().size());
            messageList.add(temp);
            Log.d(TAG, "onMessageSent: Size of the list after insertion ====> " + getMessageList().size());
            Log.e(TAG, "onMessageSent: STRING IS SENT");
        } else {
            temp = new Message(true, null, message.getMessageTime(), message.getSelectedImageUri());
            messageList.add(temp);
            Log.d(TAG, "onMessageSent: IMAGE IS SENT");
        }
        fragmentView.updateConversation(temp);
    }


    public void deleteImagesDirectory(File file) {
        Log.d(TAG, "deleteImagesDirectory: start");

        if (file.isDirectory()) {
            for (File content : file.listFiles()) {
                Log.d(TAG, "deleteImagesDirectory: File Name ---->" + content.getName());
//                deleteImagesDirectory(content);
            }
        } else {
            Log.d(TAG, "deleteImagesDirectory: This isn't a directory");
        }
        Log.d(TAG, "deleteImagesDirectory: end");
//        file.delete();
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
        // TODO: 10/2/2017 Calling the send image from here
        sendMessage(getEncodedStringFromBitmap(loadedImage), Uri.parse(imageUri));

    }

    private String getEncodedStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
