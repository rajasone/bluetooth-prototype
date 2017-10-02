package com.example.rajasaboor.bluetoothprototype;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class PreviewActivity extends AppCompatActivity {
    private static final String TAG = PreviewActivity.class.getSimpleName();
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // start picker to get image for cropping and then use the image in cropping activity

        if (getIntent().getExtras() != null) {
            selectedImageUri = Uri.parse(getIntent().getExtras().getString(BuildConfig.SELECTED_IMAGE_URI, null));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PreviewFragment previewFragment = (PreviewFragment) getSupportFragmentManager().findFragmentById(R.id.preview_fragment_container);

        if (previewFragment == null) {
            previewFragment = PreviewFragment.getInstance(selectedImageUri);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.preview_fragment_container, previewFragment)
                    .commit();
        }
        Log.d(TAG, "onCreate: end");
    }

    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: start");
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        Log.d(TAG, "onOptionsItemSelected: end");
        return super.onOptionsItemSelected(item);
    }
}
