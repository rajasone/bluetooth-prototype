package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.communication.BluetoothConnectionService;
import com.example.rajasaboor.bluetoothprototype.databinding.ActivityMainBinding;

public class SearchActivity extends AppCompatActivity implements SearchContract.ActivityView {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private SearchContract.Presenter presenter;
    private SearchFragment searchFragment;
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mainBinding.includeToolbar.bluetoothToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bluetooth Prototype");
        }

        searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
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
            presenter.setDeviceDiscoveryInProgress(savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS));
            presenter.setDeviceDiscoveryForChatActivity((savedInstanceState.getBoolean(BuildConfig.IS_SEARCH_FOR_CHAT, false)));
        }
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: start");
        super.onResume();

        setUpViews();
        registerBluetoothEnableBroadcast();

        if (presenter.isDeviceDiscoveryInProgress() || presenter.isDeviceDiscoveryForChatActivity()) {
            presenter.registerDeviceDiscoveryBroadcast();
        }

        if ((presenter.getConnectionService() == null) && (BluetoothAdapter.getDefaultAdapter().isEnabled())) {
            Log.e(TAG, "onResume: Setting up the connection service");
            presenter.setConnectionService(new BluetoothConnectionService(presenter.getHandler()));
        }

        Log.d(TAG, "onResume: end");
    }

    void setUpViews() {
        Log.d(TAG, "setUpViews: Default adapter is enable ? ===>" + BluetoothAdapter.getDefaultAdapter().isEnabled());
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            searchFragment.showViews(true);
            mainBinding.includeToolbar.bluetoothOnOff.setChecked(true);
            searchFragment.updateListSize(presenter.getPairedDevices().size(), true);
        } else {
            searchFragment.showViews(false);
            mainBinding.includeToolbar.bluetoothOnOff.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: start");
        super.onPause();
        unregisterBluetoothDiscoveryBroadcast();
        unregisterBluetoothEnableBroadcast();

        if ((presenter.getConnectionService() != null) && (presenter.isDeviceBluetoothIsTurnedOn())) {
            Log.e(TAG, "onPause: Calling the Connection Service Cancel");
            presenter.getConnectionService().cancel();
            presenter.setConnectionService(null);

        }
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

        if (presenter.isDeviceDiscoveryInProgress()) {
            outState.putBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS, true);
        }

        if (presenter.isDeviceDiscoveryForChatActivity()) {
            outState.putBoolean(BuildConfig.IS_SEARCH_FOR_CHAT, presenter.isDeviceDiscoveryForChatActivity());
        }

    }

    @Override
    public void registerBluetoothDiscoveryBroadcast() {
        if (presenter.getDiscoveryReceiver() == null) {
            Log.d(TAG, "registerBluetoothDiscoveryBroadcast: Defining the broadcast");
            presenter.broadcastDefine();
        }
        registerReceiver(presenter.getDiscoveryReceiver(), presenter.getBlutoothDiscoveryIntent());
        presenter.setDeviceDiscoveryInProgress(true);
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
    public void registerBluetoothEnableBroadcast() {
        if (presenter.getBluetoothEnableReceiver() == null) {
            presenter.defineBluetoothEnableBroadcast();
            Log.d(TAG, "registerBluetoothEnableBroadcast: Registering the enable or disable broadcast successfully");
        }

        IntentFilter enableFilter = new IntentFilter();
        enableFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(presenter.getBluetoothEnableReceiver(), enableFilter);
    }

    @Override
    public void registerPairBroadcast() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(presenter.getPairBroadcast(), intentFilter);
    }

    @Override
    public void unregisterBluetoothEnableBroadcast() {
        if (presenter.getBluetoothEnableReceiver() != null) {
            unregisterReceiver(presenter.getBluetoothEnableReceiver());
            Log.d(TAG, "unregisterBluetoothEnableBroadcast: Unregister the bluetooth enable or disable broadcast successfully");
            presenter.setBluetoothEnableReceiver(null);
        }
    }

    public void unregisterPairBroadcast() {
        if (presenter.getPairBroadcast() != null) {
            unregisterReceiver(presenter.getPairBroadcast());
            presenter.setPairBroadcast(null);
            Log.d(TAG, "unregisterPairBroadcast: Pair Broadcast Unregister Successfully");
        }
    }

}
