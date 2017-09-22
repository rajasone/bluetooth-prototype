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
import android.widget.Toast;

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
            invokePermissions(Manifest.permission.ACCESS_COARSE_LOCATION, BuildConfig.ACCESS_COARSE_LOCATION_REQUEST_CODE);
        }

        Log.d(TAG, "onCreate: end");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: start");
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: end");
    }

    public void invokePermissions(String permission, int requestCode) {
        if (!(checkSelfPermission(permission))) {
            Log.d(TAG, "invokePermissions: Requesting the permission for ====> " + permission);
            requestPermissions(new String[]{permission}, requestCode);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        mainFragmentBinding.setHandler(this);
        setUpDiscoverDevicesRecyclerView();
        setUpPairedDevicesRecyclerView();

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreateView: Saving the state of no text view is ---> " + savedInstanceState.getBoolean("show_no_bluetooth_text_view"));
            updateListSize(savedInstanceState.getInt(BuildConfig.NUMBER_OF_PAIRED_DEVICES, 0), true);
            updateListSize(savedInstanceState.getInt(BuildConfig.NUMBER_OF_DISCOVERED_DEVICES, 0), false);
            showDiscoveryProgressBar(savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS));
            presenter.setDiscoveryDevicesList(savedInstanceState.<BluetoothDevice>getParcelableArrayList(BuildConfig.DISCOVER_DEVICES));
            showAvailableDeviceInRecyclerView(presenter.getDiscoveryDevicesList(), true);
        }

        return mainFragmentBinding.getRoot();
    }


    void setUpDiscoverDevicesRecyclerView() {
        PairedDevicesAdapter adapter = new PairedDevicesAdapter(new ArrayList<BluetoothDevice>(), false, presenter);
        mainFragmentBinding.availableDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainFragmentBinding.availableDevicesRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        mainFragmentBinding.availableDevicesRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    void setUpPairedDevicesRecyclerView() {
        PairedDevicesAdapter adapter = new PairedDevicesAdapter(new ArrayList<>(BluetoothAdapter.getDefaultAdapter().getBondedDevices()), true, presenter);
        mainFragmentBinding.pairedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainFragmentBinding.pairedDevicesRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        mainFragmentBinding.pairedDevicesRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void onClick() {
        Log.d(TAG, "onClick: start");
        presenter.onClick(mainFragmentBinding.searchBluetoothButton);
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
        outState.putParcelableArrayList(BuildConfig.DISCOVER_DEVICES, (ArrayList<? extends Parcelable>) presenter.getDiscoveryDevicesList());
        outState.putInt(BuildConfig.NUMBER_OF_PAIRED_DEVICES, presenter.getPairedDevices().size());
        outState.putInt(BuildConfig.NUMBER_OF_DISCOVERED_DEVICES, presenter.getDiscoveryDevicesList().size());
        outState.putBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS, presenter.isDeviceDiscoveryInProgress());
        Log.d(TAG, "onSaveInstanceState: end");
    }

    @Override
    public void permissionsValidation(String permission) {
        Log.d(TAG, "permissionsValidation: start");
        int selfPermissionForCoarseLocation = ContextCompat.checkSelfPermission(getContext(), permission);

        if (selfPermissionForCoarseLocation == PackageManager.PERMISSION_DENIED) {
            boolean result = shouldShowRequestPermissionRationale(permission);

            if ((!result) && (!presenter.isPermissionGranted(getContext().getPackageManager(), permission, getContext().getPackageName()))) {
                openAppSettings();
            } else {
                requestPermissions(new String[]{permission}, BuildConfig.ACCESS_COARSE_LOCATION_REQUEST_CODE);
            }
        }
        Log.d(TAG, "permissionsValidation: end");
    }


    @Override
    public boolean isDeviceHaveBluetoothAndPermissionGranted() {
        boolean result = false;

        if (presenter.isDeviceBluetoothIsTurnedOn() && presenter.isPermissionGranted(getContext().getPackageManager(),
                Manifest.permission.ACCESS_COARSE_LOCATION, getContext().getPackageName())) {
            result = true;
        }

        return result;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /*
    * if resetPairedAdapter is true reset the Paired adapter
    * Else reset the Discovery adapter
     */
    @Override
    public void resetAdapter(boolean resetPairedAdapter) {
        if (resetPairedAdapter) {
            showAvailableDeviceInRecyclerView(new ArrayList<BluetoothDevice>(), false);
        } else {
            if (presenter.getDiscoveryDevicesList() != null) {
                presenter.getDiscoveryDevicesList().clear();
                showAvailableDeviceInRecyclerView(new ArrayList<BluetoothDevice>(), true);
            }
        }
    }

    @Override
    public void updateListSize(int listSize, boolean isPairedList) {
        if (isPairedList) {
            mainFragmentBinding.numberOfPairedDevicesTextView.setText(String.valueOf(listSize));
        } else {
            mainFragmentBinding.numberOfAvailableDevices.setText(String.valueOf(listSize));
        }
    }

    @Override
    public void resetListSizeTextViews() {
        mainFragmentBinding.numberOfAvailableDevices.setText("");
    }

    @Override
    public void showDiscoveryProgressBar(boolean show) {
        mainFragmentBinding.discoverProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
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
            mainFragmentBinding.numberOfAvailableDevices.setVisibility(View.VISIBLE);
            mainFragmentBinding.numberOfPairedDevicesTextView.setVisibility(View.VISIBLE);
        } else {
            mainFragmentBinding.pairedDevicesTextView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.pairedDevicesRecyclerView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.availableDevicesTextView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.availableDevicesRecyclerView.setVisibility(View.INVISIBLE);
            mainFragmentBinding.bluetoothTurendOffTextView.setVisibility(View.VISIBLE);
            mainFragmentBinding.searchBluetoothButton.setEnabled(false);
            mainFragmentBinding.numberOfAvailableDevices.setVisibility(View.INVISIBLE);
            mainFragmentBinding.numberOfPairedDevicesTextView.setVisibility(View.INVISIBLE);


        }
    }
}
