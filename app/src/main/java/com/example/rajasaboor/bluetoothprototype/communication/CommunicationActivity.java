package com.example.rajasaboor.bluetoothprototype.communication;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.PreviewActivity;
import com.example.rajasaboor.bluetoothprototype.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommunicationActivity extends AppCompatActivity implements CommunicationContract.ActivityView {
    private static final String TAG = CommunicationActivity.class.getSimpleName();
    private static final int PREVIEW_ACTIVITY_REQUEST_CODE = 9001;
    private CommunicationContract.Presenter presenter;
    private CommunicationFragment communicationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        communicationFragment = (CommunicationFragment) getSupportFragmentManager().findFragmentById(R.id.communication_fragment_container);

        if (communicationFragment == null) {
            communicationFragment = CommunicationFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.communication_fragment_container, communicationFragment)
                    .commit();
        }
        presenter = new CommunicationPresenter(communicationFragment, this);
        communicationFragment.setPresenter(presenter);

        Log.d(TAG, "onCreate: end");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attach_file_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_attach_menu:
                openImagesIntent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openImagesIntent() {
        Intent imagesIntent = new Intent();
        imagesIntent.setType("image/*");
        imagesIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imagesIntent, BuildConfig.IMAGES_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d(TAG, "onActivityResult: start");
        switch (requestCode) {
            case BuildConfig.IMAGES_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.d(TAG, "onActivityResult: Image Uri ===> " + data.getData());
                    Intent intent = new Intent(this, PreviewActivity.class);
                    intent.putExtra(BuildConfig.SELECTED_IMAGE_URI, data.getData().toString());
                    startActivityForResult(intent, PREVIEW_ACTIVITY_REQUEST_CODE);
                }
                break;
            case PREVIEW_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ImageLoader.getInstance().loadImage(data.getData().toString(), ((CommunicationPresenter) presenter));
                }
                break;
        }
        Log.d(TAG, "onActivityResult: end");

    }

    @Override
    public Application getApplicationInstance() {
        return getApplication();
    }
}
