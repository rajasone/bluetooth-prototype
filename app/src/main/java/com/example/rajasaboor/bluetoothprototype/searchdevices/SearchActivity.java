package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.SearchProgressFragment;
import com.example.rajasaboor.bluetoothprototype.databinding.ActivityMainBinding;
import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListContract;
import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListFragment;
import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListPresenter;

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
            Log.d(TAG, "onCreate: Setting the title");
            getSupportActionBar().setTitle("Bluetooth Prototype");
        } else {
            Log.e(TAG, "onCreate: Action bar is NULL");
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
        mainBinding.includeToolbar.bluetoothOnOff.setOnCheckedChangeListener(presenter);

        if (savedInstanceState != null)
            Log.d(TAG, "onCreate: Search in progress ===> " + savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS));

        if ((savedInstanceState != null) && (savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS))) {
            presenter.setDeviceDiscoveryInProgress(true);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        setUpViews();
        registerBluetoothEnableBroadcast();

        if (presenter.isDeviceDiscoveryInProgress()) {
            presenter.registerBroadcast();
        }
    }

    void setUpViews() {
        Log.d(TAG, "setUpViews: Default adapter is enable ? ===>" + BluetoothAdapter.getDefaultAdapter().isEnabled());
        if (mainBinding.includeToolbar.bluetoothOnOff.isChecked() || BluetoothAdapter.getDefaultAdapter().isEnabled()) {
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
        super.onPause();
        unregisterBluetoothDiscoveryBroadcast();
        unregisterBluetoothEnableBroadcast();
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
    public void unregisterBluetoothEnableBroadcast() {
        if (presenter.getBluetoothEnableReceiver() != null) {
            unregisterReceiver(presenter.getBluetoothEnableReceiver());
            Log.d(TAG, "unregisterBluetoothEnableBroadcast: Unregister the bluetooth enable or disable broadcast successfully");
            presenter.setBluetoothEnableReceiver(null);
        }
    }
}
