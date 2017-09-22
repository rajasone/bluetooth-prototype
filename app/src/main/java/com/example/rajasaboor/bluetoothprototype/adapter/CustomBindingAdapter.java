package com.example.rajasaboor.bluetoothprototype.adapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

/**
 * Created by rajaSaboor on 9/22/2017.
 */

public class CustomBindingAdapter {
    @BindingAdapter("app:setImageResource")
    public static void setUpImageResource(ImageView imageView, int imageResource) {
        imageView.setImageResource(imageResource);
    }
}
