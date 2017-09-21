package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.adapter.PairedDevicesAdapter;
import com.example.rajasaboor.bluetoothprototype.databinding.MainFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajaSaboor on 9/7/2017.
 */

public class SearchFragment extends Fragment implements SearchContract.FragmentView {
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: start");
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: end");
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
        setUpDiscoverDevicesRecyclerView();
        setUpPairedDevicesRecyclerView();

        // TODO: 9/21/2017 set the presenter lists and again set to the adapter

        return mainFragmentBinding.getRoot();
    }


    void setUpDiscoverDevicesRecyclerView() {
        PairedDevicesAdapter adapter = new PairedDevicesAdapter(new ArrayList<BluetoothDevice>(), false);
        mainFragmentBinding.availableDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainFragmentBinding.availableDevicesRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        mainFragmentBinding.availableDevicesRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    void setUpPairedDevicesRecyclerView() {
        PairedDevicesAdapter adapter = new PairedDevicesAdapter(new ArrayList<>(BluetoothAdapter.getDefaultAdapter().getBondedDevices()), true);
        mainFragmentBinding.pairedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainFragmentBinding.pairedDevicesRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        mainFragmentBinding.pairedDevicesRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void onClick() {
        Log.d(TAG, "onClick: start");
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothAdapter.getDefaultAdapter().enable();
        }

        if (presenter.isDeviceHaveBluetooth()) {
            permissionsValidation();
            registerReceiverAfterChecks();

            // TODO: 9/18/2017 is this approch is acceptable or not?
            presenter.getListPresenter().getDeviceListFragmentView().resetDeviceListAdapter();
        }
        Log.d(TAG, "onClick: end");
    }

    @Override
    public void showAvailableDeviceInRecyclerView(List<BluetoothDevice> deviceList, boolean isDiscoverAdapter) {
        if (isDiscoverAdapter) {
            Log.d(TAG, "showAvailableDeviceInRecyclerView: Updating the available devices adapter");
            ((PairedDevicesAdapter) mainFragmentBinding.availableDevicesRecyclerView.getAdapter()).updateList(deviceList);
        } else {
            ((PairedDevicesAdapter) mainFragmentBinding.pairedDevicesRecyclerView.getAdapter()).updateList(deviceList);
            Log.d(TAG, "showAvailableDeviceInRecyclerView: Updating the PAIRED devices adapter");
        }
    }

    public void openBluetoothPermissionIntent() {
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(bluetoothIntent, BuildConfig.BLUETOOTH_REQUEST_CODE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: start");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("pairedDevices", (ArrayList<? extends Parcelable>) presenter.getPairedDevices());
        outState.putParcelableArrayList("discoverDevices", (ArrayList<? extends Parcelable>) presenter.getPairedDevices());
        Log.d(TAG, "onSaveInstanceState: end");
    }

    public void permissionsValidation() {
        Log.d(TAG, "permissionsValidation: start");
        int selfPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (selfPermission == PackageManager.PERMISSION_DENIED) {
            boolean result = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);

            if ((!result) && (!presenter.isPermissionGranted(getContext().getPackageManager(), Manifest.permission.ACCESS_COARSE_LOCATION, getContext().getPackageName()))) {
                openAppSettings();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BuildConfig.ACCESS_COARSE_LOCATION);
            }
        }
        Log.d(TAG, "permissionsValidation: end");
    }

    private void registerReceiverAfterChecks() {
        Log.d(TAG, "registerReceiverAfterChecks: start");
        if ((isDeviceHaveBluetoothAndPermissionGranted())) {

//            showSearchProgressFragment(true);
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

    @Override
    public void showViews(boolean bluetoothOnViews) {
        if (bluetoothOnViews) {
            mainFragmentBinding.pairedDevicesTextView.setVisibility(View.VISIBLE);
            mainFragmentBinding.pairedDevicesRecyclerView.setVisibility(View.VISIBLE);
            mainFragmentBinding.availableDevicesTextView.setVisibility(View.VISIBLE);
            mainFragmentBinding.availableDevicesRecyclerView.setVisibility(View.VISIBLE);
            mainFragmentBinding.bluetoothTurendOffTextView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.searchBluetoothButton.setEnabled(true);

        } else {
            mainFragmentBinding.pairedDevicesTextView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.pairedDevicesRecyclerView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.availableDevicesTextView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.availableDevicesRecyclerView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.bluetoothTurendOffTextView.setVisibility(View.VISIBLE);
            mainFragmentBinding.searchBluetoothButton.setEnabled(false);


        }
    }

    /*
    @Override
    public void showSearchProgressFragment(boolean show) {
        SearchProgressFragment searchProgressFragment = (SearchProgressFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.search_fragment_container);
        FragmentTransaction transaction = null;
        if (searchProgressFragment != null) {
            Log.d(TAG, "showSearchProgressFragment: Search fragment is NOT null");
            transaction = getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            Log.d(TAG, "showSearchProgressFragment: Search fragment is NULL");
        }

        if (transaction != null) {
            if (show) {
                Log.d(TAG, "showSearchProgressFragment: Showing the fragment");
                transaction.show(searchProgressFragment).commit();
            } else {
                Log.d(TAG, "showSearchProgressFragment: Hide the fragment");
                transaction.hide(searchProgressFragment).commit();
            }
        }
        Log.d(TAG, "showSearchProgressFragment: end");
    }
    */
}
