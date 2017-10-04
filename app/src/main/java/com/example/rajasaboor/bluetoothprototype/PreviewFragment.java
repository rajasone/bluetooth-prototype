package com.example.rajasaboor.bluetoothprototype;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.communication.CommunicationActivity;
import com.example.rajasaboor.bluetoothprototype.databinding.SelectedImagePreviewBinding;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * Created by rajaSaboor on 9/29/2017.
 */

public class PreviewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = PreviewFragment.class.getSimpleName();
    private SelectedImagePreviewBinding imagePreviewBinding;

    public static PreviewFragment getInstance(Uri selectedImageUri) {
        Log.d(TAG, "getInstance: start");
        Bundle bundle = new Bundle();
        bundle.putString(BuildConfig.SELECTED_IMAGE_URI, selectedImageUri.toString());
        PreviewFragment previewFragment = new PreviewFragment();
        previewFragment.setArguments(bundle);
        Log.d(TAG, "getInstance: end");
        return previewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: start");
        imagePreviewBinding = DataBindingUtil.inflate(inflater, R.layout.selected_image_preview, container, false);
        imagePreviewBinding.setConnectionHandler(this);
        ImageLoader.getInstance().displayImage(getArguments().getString(BuildConfig.SELECTED_IMAGE_URI, null), imagePreviewBinding.previewImageView);

        Log.d(TAG, "onCreateView: end");
        return imagePreviewBinding.getRoot();
    }


    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: start");
        Intent intent = new Intent(getContext(), CommunicationActivity.class);
        intent.setData(((PreviewActivity) getActivity()).getSelectedImageUri());
        intent.putExtra(BuildConfig.IS_IMAGE_SELECTED, true);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
        Log.d(TAG, "onClick: end");
    }
}
