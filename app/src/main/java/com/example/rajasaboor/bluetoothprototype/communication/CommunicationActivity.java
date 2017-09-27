package com.example.rajasaboor.bluetoothprototype.communication;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.rajasaboor.bluetoothprototype.BluetoothApplication;
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
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: start");

        if ((presenter.getConnectionService()) == null && (BluetoothAdapter.getDefaultAdapter().isEnabled())) {
            Log.e(TAG, "onResume: Enabling the Connection Service");
            presenter.setConnectionService(((BluetoothApplication) getApplication()).getService());
        }
        Log.d(TAG, "onResume: end");
    }

}
