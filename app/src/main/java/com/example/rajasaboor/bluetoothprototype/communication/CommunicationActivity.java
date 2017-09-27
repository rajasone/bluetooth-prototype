package com.example.rajasaboor.bluetoothprototype.communication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.rajasaboor.bluetoothprototype.R;

public class CommunicationActivity extends AppCompatActivity {
    private static final String TAG = CommunicationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CommunicationFragment communicationFragment = (CommunicationFragment) getSupportFragmentManager().findFragmentById(R.id.communication_fragment_container);

        if (communicationFragment == null) {
            communicationFragment = CommunicationFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.communication_fragment_container, communicationFragment)
                    .commit();
        }
    }

}
