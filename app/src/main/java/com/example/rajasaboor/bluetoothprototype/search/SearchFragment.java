package com.example.rajasaboor.bluetoothprototype.search;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.databinding.MainFragmentBinding;
import com.example.rajasaboor.bluetoothprototype.list.DevicesListFragment;
import com.example.rajasaboor.bluetoothprototype.list.DevicesListPresenter;

/**
 * Created by rajaSaboor on 9/7/2017.
 */

public class SearchFragment extends Fragment implements SearchPresenter.OnDiscoveryComplete, SearchContract.FragmentView {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private SearchContract.Presenter presenter = null;


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);


        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: Bundle is empty requesting a permission");
            invokePermissions();
        }
        Log.d(TAG, "onCreate: end");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainFragmentBinding mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        mainFragmentBinding.setHandler(this);
        return mainFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: start");
        super.onActivityCreated(savedInstanceState);
        presenter.deleteSharedPrefs();
        Log.d(TAG, "onActivityCreated: end");
    }

    private boolean isBaseBluetoothSettingsAreFine() {
        boolean result = false;

        if (presenter.isDeviceHaveBluetooth() && presenter.isDeviceBluetoothIsTurnedOn() && presenter.isPermissionGranted(getContext().getPackageManager(),
                Manifest.permission.ACCESS_COARSE_LOCATION, getContext().getPackageName())) {
            result = true;
        }

        return result;
    }

    private void registerReceiverAfterChecks() {
        Log.d(TAG, "registerReceiverAfterChecks: start");
        Log.d(TAG, "registerReceiverAfterChecks: Shared Prefs result ===> " + presenter.getSharedPreferences());
        if ((isBaseBluetoothSettingsAreFine()) && (!presenter.getSharedPreferences())) {
            // show the fragment
            presenter.showSearchFragment(getActivity().getSupportFragmentManager(), true);
            registerBluetoothBroadcast();
            presenter.setSharedPreferences();
        } else {
            Log.e(TAG, "registerReceiverAfterChecks: Device have no bluetooth/bluetooth is off");
        }
        Log.d(TAG, "registerReceiverAfterChecks: end");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: start");
        super.onResume();
//        getTheViewInstanceOrNewOne().resetListAdapter();
        getTheViewInstanceOrNewOne().resetDeviceListAdapter();
        registerReceiverAfterChecks();
        Log.d(TAG, "onResume: end");
    }

    public void onClick() {
        Log.d(TAG, "onClick: start");
        if (presenter.isDeviceHaveBluetooth()) {
            if (!presenter.isDeviceBluetoothIsTurnedOn()) {
                openBluetoothPermissionIntent();
            } else {
                permissionsValidation();
                unregisterBroadcast();
                registerReceiverAfterChecks();
//                getTheViewInstanceOrNewOne().resetListAdapter();
                getTheViewInstanceOrNewOne().resetDeviceListAdapter();
            }
        } else {
            Log.e(TAG, "onClick: Device is not supporting the bluetooth");
        }
        Log.d(TAG, "onClick: end");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: start");
        super.onPause();
        Log.d(TAG, "onPause: Result in onPause ===> " + presenter.getSharedPreferences());

        unregisterBroadcast();
        Log.d(TAG, "onPause: end");
    }

    @Override
    public void unregisterBroadcast() {
        if (presenter.getSharedPreferences() && presenter.isDeviceBluetoothIsTurnedOn()) {
            getActivity().unregisterReceiver(((SearchPresenter) presenter).getmReceiver());
            presenter.deleteSharedPrefs();
        }
    }

    @Override
    public void changeSearchingTextToNoDeviceFound(boolean noDeviceFound) {
        DevicesListFragment listFragment = getTheViewInstanceOrNewOne();

        if (listFragment != null) {
            Log.d(TAG, "changeSearchingTextToNoDeviceFound: IS New Device Found ===> " + ((DevicesListPresenter) listFragment.getPresenter()).isNewDeviceFound());

            if ((((DevicesListPresenter) listFragment.getPresenter()).getDeviceList() == null || ((DevicesListPresenter) listFragment.getPresenter()).getDeviceList().size() == 0) && (!((DevicesListPresenter) listFragment.getPresenter()).isNewDeviceFound())) {
                Toast.makeText(getContext(), "No Device found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Devices found", Toast.LENGTH_SHORT).show();
            }
            ((DevicesListPresenter) listFragment.getPresenter()).setNewDeviceFound(false);
        }
    }

    @Override
    public DevicesListFragment getTheViewInstanceOrNewOne() {
        DevicesListFragment listFragment = (DevicesListFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.list_fragment_container);

        if (listFragment == null) {
            listFragment = DevicesListFragment.newInstance();
        }

        return listFragment;
    }

    @Override
    public void onDiscoveryComplete(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onDiscoveryComplete: start");
        DevicesListFragment listFragment = (DevicesListFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.list_fragment_container);

        if (listFragment == null) {
            listFragment = DevicesListFragment.newInstance();
        }

//        listFragment.addDeviceInList(bluetoothDevice);

        listFragment.getPresenter().addBluetoothDeviceInList(bluetoothDevice);
        listFragment.refreshListAdapter();
        Log.d(TAG, "onDiscoveryComplete: end");
    }

    @Override
    public void onDiscoveryFinish() {
        presenter.showSearchFragment(getActivity().getSupportFragmentManager(), false);
        changeSearchingTextToNoDeviceFound(true);
    }


    public void setPresenter(SearchContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void invokePermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BuildConfig.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void permissionsValidation() {
        int selfPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (selfPermission == PackageManager.PERMISSION_DENIED) {
            boolean result = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);

            if ((!result) && (!presenter.isPermissionGranted(getContext().getPackageManager(), Manifest.permission.ACCESS_COARSE_LOCATION, getContext().getPackageName()))) {
                openAppSettings();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BuildConfig.ACCESS_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void registerBluetoothBroadcast() {
        presenter.broadcastDefine();
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(((SearchPresenter) presenter).getmReceiver(), filter);
    }

    @Override
    public void openBluetoothPermissionIntent() {
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(bluetoothIntent, BuildConfig.BLUETOOTH_REQUEST_CODE);
    }

    @Override
    public void openAppSettings() {
        Intent appSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        Log.d(TAG, "openUpAppSettings: Uri ===> " + uri.toString());
        appSettings.setData(uri);
        startActivity(appSettings);
    }
}
