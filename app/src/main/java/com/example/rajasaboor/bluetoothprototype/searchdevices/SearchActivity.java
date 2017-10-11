package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rajasaboor.bluetoothprototype.BluetoothApplication;
import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.communication.BluetoothConnectionService;
import com.example.rajasaboor.bluetoothprototype.databinding.ActivityMainBinding;

public class SearchActivity extends AppCompatActivity implements SearchContract.ActivityView {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private SearchContract.Presenter presenter;
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mainBinding.includeToolbar.bluetoothToolbar);

        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, searchFragment)
                    .commit();
        }

        presenter = new SearchPresenter(this, searchFragment);
        searchFragment.setPresenter(presenter);
        mainBinding.includeToolbar.bluetoothOnOff.setOnCheckedChangeListener(searchFragment);

        if (savedInstanceState != null) {
            presenter.setDeviceDiscoveryInProgress(savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS, false));
            presenter.setDeviceDiscoveryForChatActivity((savedInstanceState.getBoolean(BuildConfig.IS_SEARCH_FOR_CHAT, false)));
        }
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: start");
        super.onResume();

        presenter.loadViewBasedOnBluetoothState();
        registerBluetoothEnableBroadcast();
        presenter.registerDeviceDiscoveryAndStartService();

        Log.d(TAG, "onResume: end");
    }

    public void registerBluetoothEnableBroadcast() {
        presenter.checkAndRegisterBluetoothEnableReceiver();
        IntentFilter enableFilter = new IntentFilter();
        enableFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(presenter.getBluetoothEnableReceiver(), enableFilter);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: start");
        super.onPause();
        unregisterBluetoothDiscoveryBroadcast();
        unregisterBluetoothEnableBroadcast();
        Log.d(TAG, "onPause: end");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterPairBroadcast();
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
        super.onSaveInstanceState(outState);
        outState.putBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS, presenter.isDeviceDiscoveryInProgress());
        outState.putBoolean(BuildConfig.IS_SEARCH_FOR_CHAT, presenter.isDeviceDiscoveryForChatActivity());

    }

    @Override
    public void unregisterBluetoothDiscoveryBroadcast() {
        if (presenter.getDiscoveryReceiver() != null) {
            unregisterReceiver(presenter.getDiscoveryReceiver());
            presenter.setDiscoveryReceiver(null);
            Log.d(TAG, "unregisterBluetoothDiscoveryBroadcast: Broadcast Unregister Successfully");
        }
    }


    @Override
    public void registerPairBroadcast() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(presenter.getPairBroadcast(), intentFilter);
    }

    @Override
    public Application getApplicationInstance() {
        return getApplication();
    }


    @Override
    public void checkBluetoothSwitch(boolean check) {
        mainBinding.includeToolbar.bluetoothOnOff.setChecked(check);
    }

    @Override
    public void registerBroadcastReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        registerReceiver(broadcastReceiver, intentFilter);
    }

    void unregisterBluetoothEnableBroadcast() {
        if (presenter.getBluetoothEnableReceiver() != null) {
            unregisterReceiver(presenter.getBluetoothEnableReceiver());
            Log.d(TAG, "unregisterBluetoothEnableBroadcast: Unregister the bluetooth enable or disable broadcast successfully");
            presenter.setBluetoothEnableReceiver(null);
        }
    }

    void unregisterPairBroadcast() {
        if (presenter.getPairBroadcast() != null) {
            unregisterReceiver(presenter.getPairBroadcast());
            presenter.setPairBroadcast(null);
            Log.d(TAG, "unregisterPairBroadcast: Pair Broadcast Unregister Successfully");
        }
    }

}
