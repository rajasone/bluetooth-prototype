package com.example.rajasaboor.bluetoothprototype.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.model.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        ((TextView) view).setText(new SimpleDateFormat("hh:mm a", Locale.US).format(new Date(message.getMessageTime())));
    }

    @BindingAdapter("showMessage")
    public static void showMessage(View view, Message message) {

        if (message.getSelectedImageUri() != null) {
            view.setVisibility(View.GONE);
            return;
        }

        ((TextView) view).setText(message.getMyMessage());
        view.setVisibility(View.VISIBLE);
        if (!message.isMyMessage()) {
            ((TextView) view).setGravity(Gravity.END);
        } else {
            ((TextView) view).setGravity(Gravity.START);
        }
    }

    @BindingAdapter("showImage")
    public static void showImage(View view, Message message) {
        if (message.getSelectedImageUri() == null) {
            view.setVisibility(View.GONE);
            return;
        }

        if (message.isMyMessage()) {
            ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.START;
        } else {
            ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = Gravity.END;
        }

        switch (message.getSelectedImageUri().getScheme()) {
            case "content":
                ImageLoader.getInstance().displayImage(message.getSelectedImageUri().toString(), ((ImageView) view));
                view.setVisibility(View.VISIBLE);
                break;
            case "file":
                ImageLoader.getInstance().displayImage(Uri.fromFile(new File(message.getSelectedImageUri().getPath())).toString(), ((ImageView) view));
                view.setVisibility(View.VISIBLE);
                break;
        }
    }
}
