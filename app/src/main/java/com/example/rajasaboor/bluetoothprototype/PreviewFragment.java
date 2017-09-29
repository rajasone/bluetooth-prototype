package com.example.rajasaboor.bluetoothprototype;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.databinding.SelectedImagePreviewBinding;

/**
 * Created by rajaSaboor on 9/29/2017.
 */

public class PreviewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = PreviewFragment.class.getSimpleName();
    private SelectedImagePreviewBinding imagePreviewBinding;
    private Uri selectedImageUri;

    public static PreviewFragment getInstance() {
        Log.d(TAG, "getInstance: start");
        Log.d(TAG, "getInstance: end");
        return new PreviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: start");
        imagePreviewBinding = DataBindingUtil.inflate(inflater, R.layout.selected_image_preview, container, false);
        imagePreviewBinding.setHandler(this);
        imagePreviewBinding.previewImageView.setImageURI(selectedImageUri);

        Log.d(TAG, "onCreateView: end");
        return imagePreviewBinding.getRoot();
    }

    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }

    public void setSelectedImageUri(Uri selectedImageUri) {
        Log.d(TAG, "setSelectedImageUri: start");
        this.selectedImageUri = selectedImageUri;
        Log.d(TAG, "setSelectedImageUri: end");
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: start");
        getActivity().finish();
        Log.d(TAG, "onClick: end");
    }
}
