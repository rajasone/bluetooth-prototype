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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
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

        // setting up the Discovered list devices fragment
        DevicesListFragment listFragment = (DevicesListFragment) addFragment(BuildConfig.DEVICE_LIST_FRAGMENT);
        DevicesListContract.Presenter devicePresenter = new DevicesListPresenter(listFragment);
        listFragment.setPresenter(devicePresenter);

        // setting up the Main fragment
        SearchFragment mainFragment = (SearchFragment) addFragment(BuildConfig.SEARCH_FRAGMENT);
        presenter = new SearchPresenter(this, mainFragment, devicePresenter);
        mainFragment.setPresenter(presenter);


        // setting up the Discovered list devices fragment
        addFragment(BuildConfig.SEARCH_PROGRESS_FRAGMENT);

        if (savedInstanceState != null)
            Log.d(TAG, "onCreate: Search in progress ===> " + savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS));

        if ((savedInstanceState != null) && (savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS))) {
            mainFragment.showSearchProgressFragment(true);
            presenter.setDeviceDiscoveryInProgress(true);
        }
    }

    private Fragment addFragment(int fragmentIdentifier) {
        Fragment temp = null;

        switch (fragmentIdentifier) {
            case BuildConfig.SEARCH_FRAGMENT:
                temp = getFragmentInstance(BuildConfig.SEARCH_FRAGMENT);
                break;
            case BuildConfig.SEARCH_PROGRESS_FRAGMENT:
                temp = getFragmentInstance(BuildConfig.SEARCH_PROGRESS_FRAGMENT);
                break;
            case BuildConfig.DEVICE_LIST_FRAGMENT:
                temp = getFragmentInstance(BuildConfig.DEVICE_LIST_FRAGMENT);
                break;
        }

        return temp;
    }


    /*
    * Util method which find the fragment
    * If fragment is NULL a new instance of fragment is created and add the fragment in the container
    * If fragment is already in container just return the instance of the fragment
     */
    private Fragment getFragmentInstance(int requireFragment) {
        switch (requireFragment) {
            case BuildConfig.SEARCH_FRAGMENT:
                SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
                if (searchFragment == null) {
                    searchFragment = SearchFragment.newInstance();
                    addFragmentInContainer(R.id.main_fragment_container, searchFragment, false);
                }

                return searchFragment;
            case BuildConfig.SEARCH_PROGRESS_FRAGMENT:
                SearchProgressFragment searchProgressFragment = (SearchProgressFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment_container);
                if (searchProgressFragment == null) {
                    searchProgressFragment = SearchProgressFragment.newInstance();
                    addFragmentInContainer(R.id.search_fragment_container, searchProgressFragment, true);
                }
                return searchProgressFragment;
            case BuildConfig.DEVICE_LIST_FRAGMENT:
                DevicesListFragment listFragment = (DevicesListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment_container);
                if (listFragment == null) {
                    listFragment = DevicesListFragment.newInstance();
                    addFragmentInContainer(R.id.list_fragment_container, listFragment, false);
                }
                return listFragment;
            default:
                throw new IllegalArgumentException("Invalid require fragment ===> " + requireFragment);
        }
    }

    private void addFragmentInContainer(int containerID, Fragment fragmentToAdd, boolean hideFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .add(containerID, fragmentToAdd);

        if (hideFragment) {
            transaction.hide(fragmentToAdd)
                    .commit();
        } else {
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBluetoothEnableReceiver();
        if (presenter.isDeviceDiscoveryInProgress()) {
            presenter.registerBroadcast();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBluetoothBroadcast();

        if (presenter.getBluetoothEnableReceiver() != null) {
            unregisterReceiver(presenter.getBluetoothEnableReceiver());
            Log.e(TAG, "onPause: Unregister the bluetooth enable or disable broadcast successfully");
            presenter.setBluetoothEnableReceiver(null);
        }
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
        Log.e(TAG, "unregisterBluetoothBroadcast: UN-REGISTER END");
    }

    private void registerBluetoothEnableReceiver() {
        if (presenter.getBluetoothEnableReceiver() == null) {
            presenter.defineBluetoothEnableBroadcast();
            Log.d(TAG, "onCreate: Registering the enable or disable broadcast successfully");
        }

        IntentFilter enableFilter = new IntentFilter();
        enableFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(presenter.getBluetoothEnableReceiver(), enableFilter);
    }
}
