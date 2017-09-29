package com.example.rajasaboor.bluetoothprototype.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rajaSaboor on 9/22/2017.
 */

public class CustomBindingAdapter {
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

    @BindingAdapter("viewVisibility")
    public static void setVisibilityOfView(View view, String message) {
        if (TextUtils.isEmpty(message) || message.length() == 0) {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("setTime")
    public static void setTime(TextView view, long time) {
        view.setText(new SimpleDateFormat("h:m a", Locale.US).format(new Date(time)));
    }
}
