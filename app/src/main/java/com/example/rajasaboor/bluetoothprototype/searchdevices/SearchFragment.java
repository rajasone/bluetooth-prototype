package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.databinding.MainFragmentBinding;
import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListFragment;
import com.example.rajasaboor.bluetoothprototype.discoverdeviceslist.DevicesListPresenter;

/**
 * Created by rajaSaboor on 9/7/2017.
 */

public class SearchFragment extends Fragment implements SearchPresenter.OnDiscoveryComplete, SearchContract.FragmentView {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private SearchContract.Presenter presenter = null;
    private MainFragmentBinding mainFragmentBinding;


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

    public void invokePermissions() {
        if (!(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION))) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BuildConfig.ACCESS_COARSE_LOCATION);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        mainFragmentBinding.setHandler(this);
        return mainFragmentBinding.getRoot();
    }

    public void onClick() {
        Log.d(TAG, "onClick: start");
        if ((presenter.isDeviceHaveBluetooth()) && (!presenter.isDeviceBluetoothIsTurnedOn())) {
            openBluetoothPermissionIntent();
        } else {
            permissionsValidation();
            registerReceiverAfterChecks();
            //getTheViewInstanceOrNewOne().resetDeviceListAdapter();
        }
        Log.d(TAG, "onClick: end");
    }

    public void openBluetoothPermissionIntent() {
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(bluetoothIntent, BuildConfig.BLUETOOTH_REQUEST_CODE);
    }

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

    private void registerReceiverAfterChecks() {
        Log.d(TAG, "registerReceiverAfterChecks: start");
        if ((isDeviceHaveBluetoothAndPermissionGranted())) {
            // show the fragment
            presenter.showSearchFragment(getActivity().getSupportFragmentManager(), true);
            presenter.registerBroadcast();

            Log.d(TAG, "registerReceiverAfterChecks: Broadcast register successfully");
        }
        Log.d(TAG, "registerReceiverAfterChecks: end");
    }

    public boolean isDeviceHaveBluetoothAndPermissionGranted() {
        boolean result = false;

        if (presenter.isDeviceHaveBluetooth() && presenter.isDeviceBluetoothIsTurnedOn() && presenter.isPermissionGranted(getContext().getPackageManager(),
                Manifest.permission.ACCESS_COARSE_LOCATION, getContext().getPackageName())) {
            result = true;
        }

        return result;
    }

    public void openAppSettings() {
        startActivity(presenter.getSettingsIntent(Uri.fromParts("package", getContext().getPackageName(), null)));
    }

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

        listFragment.getPresenter().addBluetoothDeviceInList(bluetoothDevice);
        listFragment.refreshListAdapter();
        Log.d(TAG, "onDiscoveryComplete: end");
    }

    @Override
    public void onDiscoveryFinish() {
        presenter.showSearchFragment(getActivity().getSupportFragmentManager(), false);
        changeSearchingTextToNoDeviceFound();
    }

    public void changeSearchingTextToNoDeviceFound() {
        DevicesListFragment listFragment = getTheViewInstanceOrNewOne();

        if (listFragment != null) {
            Log.d(TAG, "changeSearchingTextToNoDeviceFound: IS New Device Found ===> " + listFragment.getPresenter().isNewDeviceFound());

            if ((listFragment.getPresenter().getDeviceList() == null || listFragment.getPresenter().getDeviceList().size() == 0) && (!listFragment.getPresenter().isNewDeviceFound())) {
                Toast.makeText(getContext(), "No Device found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Devices found", Toast.LENGTH_SHORT).show();
            }
            listFragment.getPresenter().setNewDeviceFound(false);
        }
    }


    public void setPresenter(SearchContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public boolean checkSelfPermission(String permission) {
        return (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void enableSearchButton(boolean enable) {
        mainFragmentBinding.searchBluetoothButton.setEnabled(enable);
    }
}
