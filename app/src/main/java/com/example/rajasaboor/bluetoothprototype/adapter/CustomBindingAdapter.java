package com.example.rajasaboor.bluetoothprototype.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.model.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;

/**
 * Created by rajaSaboor on 9/22/2017.
 */

public class CustomBindingAdapter {
    private static final String TAG = CustomBindingAdapter.class.getSimpleName();

    private static void setUpImageResource(ImageView imageView, int imageResource) {
        imageView.setImageResource(imageResource);
    }

    @BindingAdapter("identifyAndSetDeviceIcon")
    public static void identifyAndSetDeviceIcon(ImageView imageView, BluetoothDevice device) {
        int imageResource = R.drawable.bluetooth_icon;

        switch (device.getBluetoothClass().getDeviceClass()) {
            case BluetoothClass.Device.PHONE_SMART:
                imageResource = R.drawable.phone_icon;
                break;
            case BluetoothClass.Device.COMPUTER_LAPTOP:
            case BluetoothClass.Device.COMPUTER_DESKTOP:
                imageResource = R.drawable.laptop_icon;
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO:
                imageResource = R.drawable.car_icon;
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES:
            case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:
                imageResource = R.drawable.head_phone_icon;
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR:
                imageResource = R.drawable.tv_icon;
                break;

        }
        setUpImageResource(imageView, imageResource);
    }

    @BindingAdapter("setTime")
    public static void setTime(View view, Message message) {
        if (TextUtils.isEmpty(message.getMyMessage()) || message.getMyMessage().length() == 0) {
            ((TextView) view).setText(new SimpleDateFormat("h:m a", Locale.US).format(new Date(message.getMessageTime())));
        } else {
            ((TextView) view).setText(new SimpleDateFormat("h:m a", Locale.US).format(new Date(message.getMessageTime())));
        }

    }

    @BindingAdapter("showMessage")
    public static void showMessage(View view, Message message) {
        Log.d(TAG, "showMessage: start");
        if (message.getSelectedImageUri() != null) {
            return;
        }

        if (TextUtils.isEmpty(message.getMyMessage()) || message.getMyMessage().length() == 0) {
            ((TextView) view).setText(message.getSenderMessage());
            ((TextView) view).setGravity(Gravity.RIGHT);
        } else {
            ((TextView) view).setText(message.getMyMessage());
            ((TextView) view).setGravity(Gravity.LEFT);
        }
        Log.d(TAG, "showMessage: end");
    }

    @BindingAdapter("showImage")
    public static void showImage(View view, Message message) {
        Log.e(TAG, "showImage: start");

        if (message.getSelectedImageUri() != null) {
            ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.START;
        } else {
            ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.END;
        }

        if (message.getReceivedImageBitmap() != null) {
            ((ImageView) view).setImageBitmap(message.getReceivedImageBitmap());
            view.setVisibility(View.VISIBLE);
            Log.d(TAG, "showImage: image set up successfully");
            return;
        }
        if (message.getSelectedImageUri() == null) {
            view.setVisibility(GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(message.getSelectedImageUri().toString(), ((ImageView) view));
        }
        Log.e(TAG, "showImage: end");
    }
}
