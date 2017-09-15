package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.SearchProgressFragment;
import com.example.rajasaboor.bluetoothprototype.databinding.ActivityMainBinding;
import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListFragment;
import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListPresenter;

public class SearchActivity extends AppCompatActivity implements SearchContract.ActivityView {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private ActivityMainBinding mainBinding = null;

    private SearchContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SearchFragment mainFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (mainFragment == null) {
            mainFragment = SearchFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, mainFragment)
                    .commit();
        }

        presenter = new SearchPresenter(getSharedPreferences(BuildConfig.BROADCAST_PREFS_NAME, MODE_PRIVATE), this, mainFragment);
        mainFragment.setPresenter(presenter);
        presenter.setOnDiscoveryComplete(mainFragment);
        presenter.setFragmentView(mainFragment);

        // TODO: 9/15/2017 Register the receiver
//        presenter.broadcastDefine();

        SearchProgressFragment searchProgressFragment = (SearchProgressFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment_container);

        if (searchProgressFragment == null) {
            Log.d(TAG, "onCreate: Search fragment is setting up");
            searchProgressFragment = SearchProgressFragment.newInstance();
            getSupportFragmentManager().beginTransaction().
                    add(R.id.search_fragment_container, searchProgressFragment)
                    .hide(searchProgressFragment)
                    .commit();
            Log.d(TAG, "onCreate: Setting up done");
        }

        if ((savedInstanceState != null) && (savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS))) {
            presenter.showSearchFragment(getSupportFragmentManager(), true);
            presenter.setDeviceDiscoveryInProgress(true);
        }

        DevicesListFragment listFragment = (DevicesListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_fragment_container);

        if (listFragment == null) {
            Log.d(TAG, "addListFragment: List fragment is NULL");
            listFragment = DevicesListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.list_fragment_container, listFragment)
                    .commit();
        }

        listFragment.setPresenter(new DevicesListPresenter(getSharedPreferences(BuildConfig.PAIRED_DEVICES_FILE, MODE_PRIVATE)));

//        listFragment.setDeviceClickListener((DevicesListFragment.OnDeviceClickListener) presenter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (presenter.isDeviceDiscoveryInProgress()) {
            presenter.registerBroadcast();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBluetoothBroadcast();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: start");
        super.onSaveInstanceState(outState);

        if (presenter.isDeviceDiscoveryInProgress()) {
            outState.putBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS, true);
        }
        Log.d(TAG, "onSaveInstanceState: end");
    }

    @Override
    public void registerBluetoothBroadcast() {
        Log.e(TAG, "registerBluetoothBroadcast: REGISTER START");
        if (presenter.getDiscoveryReceiver() == null) {
            Log.d(TAG, "registerBluetoothBroadcast: Defining the broadcast");
            presenter.broadcastDefine();
        } else {
            Log.e(TAG, "registerBluetoothBroadcast: Broadcast is already define");
        }
        registerReceiver(presenter.getDiscoveryReceiver(), presenter.getBlutoothDiscoveryIntent());
        presenter.setDeviceDiscoveryInProgress(true);
        Log.e(TAG, "registerBluetoothBroadcast: REGISTER END");
    }

    public void unregisterBluetoothBroadcast() {
        Log.e(TAG, "unregisterBluetoothBroadcast: UN-REGISTER START");
        if (presenter.getDiscoveryReceiver() != null) {
            unregisterReceiver(presenter.getDiscoveryReceiver());
            presenter.setDiscoveryReceiver(null);
            Log.d(TAG, "unregisterBluetoothBroadcast: Broadcast Unregister Successfully");
        }
        Log.e(TAG, "unregisterBluetoothBroadcast: UN-REGISTER START");
    }
}