package com.example.rajasaboor.bluetoothprototype.communication;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rajasaboor.bluetoothprototype.BluetoothApplication;
import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.PreviewActivity;
import com.example.rajasaboor.bluetoothprototype.R;

public class CommunicationActivity extends AppCompatActivity implements CommunicationContract.ActivityView {
    private static final String TAG = CommunicationActivity.class.getSimpleName();
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
                communicationFragment.openImagesIntent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Application getApplicationInstance() {
        return getApplication();
    }
}
