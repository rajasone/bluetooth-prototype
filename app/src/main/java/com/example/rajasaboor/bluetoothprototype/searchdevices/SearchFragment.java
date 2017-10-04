package com.example.rajasaboor.bluetoothprototype.searchdevices;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.rajasaboor.bluetoothprototype.BluetoothApplication;
import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.adapter.PairedDevicesAdapter;
import com.example.rajasaboor.bluetoothprototype.communication.BluetoothConnectionService;
import com.example.rajasaboor.bluetoothprototype.communication.CommunicationActivity;
import com.example.rajasaboor.bluetoothprototype.databinding.MainFragmentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by rajaSaboor on 9/7/2017.
 */

public class SearchFragment extends Fragment implements SearchContract.FragmentView, View.OnClickListener, CompoundButton.OnCheckedChangeListener, PairedDevicesAdapter.OnRecyclerViewTapped {
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
        mainFragmentBinding.setConnectionHandler(this);
        setUpDiscoverDevicesRecyclerView();
        setUpPairedDevicesRecyclerView();

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreateView: Saving the state of no text view is ---> " + savedInstanceState.getBoolean("show_no_bluetooth_text_view"));
            updateListSize(savedInstanceState.getInt(BuildConfig.NUMBER_OF_PAIRED_DEVICES, 0), true);
            updateListSize(savedInstanceState.getInt(BuildConfig.NUMBER_OF_DISCOVERED_DEVICES, 0), false);
            showDiscoveryProgressBar(savedInstanceState.getBoolean(BuildConfig.IS_SEARCHING_IN_PROGRESS));
            presenter.setDiscoveryDevicesList(savedInstanceState.<BluetoothDevice>getParcelableArrayList(BuildConfig.DISCOVER_DEVICES));
            presenter.setSelectedDevice((BluetoothDevice) savedInstanceState.getParcelable(BuildConfig.SELECTED_DEVICE));
            showAvailableDeviceInRecyclerView(presenter.getDiscoveryDevicesList(), true);
        }

        return mainFragmentBinding.getRoot();
    }


    void setUpDiscoverDevicesRecyclerView() {
        PairedDevicesAdapter adapter = new PairedDevicesAdapter(new ArrayList<BluetoothDevice>(), false, this);
        mainFragmentBinding.availableDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainFragmentBinding.availableDevicesRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        mainFragmentBinding.availableDevicesRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    void setUpPairedDevicesRecyclerView() {
        PairedDevicesAdapter adapter = new PairedDevicesAdapter(new ArrayList<>(BluetoothAdapter.getDefaultAdapter().getBondedDevices()), true, this);
        mainFragmentBinding.pairedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainFragmentBinding.pairedDevicesRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        mainFragmentBinding.pairedDevicesRecyclerView.addItemDecoration(dividerItemDecoration);
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
        outState.putParcelable(BuildConfig.SELECTED_DEVICE, presenter.getSelectedDevice());
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
    public void showToast(String message, int resourceID) {
        if (resourceID == BuildConfig.NO_RESOURCE) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            String temp = String.format(String.valueOf(getString(resourceID)), message);
            Log.d(TAG, "showToast: Temp ====> " + temp);
            Toast.makeText(getContext(), temp, Toast.LENGTH_SHORT).show();
        }
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
        mainFragmentBinding.numberOfAvailableDevices.setText("0");
    }

    @Override
    public void showDiscoveryProgressBar(boolean show) {
        mainFragmentBinding.discoverProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPopUpMenu(final BluetoothDevice device, View view) {
        Log.d(TAG, "showPopUpMenu: start");
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        getActivity().getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_unpair:
                        Log.d(TAG, "onMenuItemClick: Unpair start");

                        if (presenter.getPairBroadcast() == null) {
                            presenter.definePairBroadcast();
                        }
                        presenter.registerPairBroadcast();
                        presenter.unpairDevice(device);
                        Log.d(TAG, "onMenuItemClick: Unpair successfully");
                        return true;
                }
                return false;
            }
        });
        Log.d(TAG, "showPopUpMenu: end");
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: On Click Start");
        switch (view.getId()) {
            case R.id.search_bluetooth_button:
                if (presenter.isDeviceHaveBluetooth()) {
                    permissionsValidation(Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (isDeviceHaveBluetoothAndPermissionGranted()) {
                        Log.d(TAG, "onClick: START the view alterations");
                        showDiscoveryProgressBar(true);
                        resetListSizeTextViews();
                        resetAdapter(false);
                        presenter.registerDeviceDiscoveryBroadcast();
                        presenter.setDeviceDiscoveryForChatActivity(false);
                        Log.d(TAG, "onClick: END the view alterations");
                    } else {
                        showToast("Enable bluetooth", BuildConfig.NO_RESOURCE);
                    }
                } else {
                    Log.e(TAG, "onClick: Device have no Bluetooth");
                }
                break;
        }
        Log.d(TAG, "onClick: On Click End");
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged: start");

        if (isChecked) {
            showViews(true);
            presenter.turnOnBluetooth(true);
        } else {
            showViews(false);
            presenter.turnOnBluetooth(false);
            resetAdapter(false);
            presenter.unregisterBluetoothDiscoveryBroadcast();
            resetListSizeTextViews();
        }
        Log.d(TAG, "onCheckedChanged: end");
    }

    @Override
    public void onRecyclerViewTapped(int position, boolean isPairedAdapter, boolean isSettingsTapped, View view) {
        Log.d(TAG, "onRecyclerViewTapped: start");

        if (position == RecyclerView.NO_POSITION) {
            Log.e(TAG, "onRecyclerViewTapped: Something went wrong with the Position current position is ===> " + position);
            return;
        }
        if ((!isPairedAdapter) && (!isSettingsTapped)) {
            if (presenter.getPairedDevices().contains(presenter.getDiscoveryDevicesList().get(position))) {
                showToast(null, R.string.already_pair_msg);
            } else {
                Log.d(TAG, "onRecyclerViewTapped: Pair the Device");
                if (presenter.getPairBroadcast() == null) {
                    presenter.definePairBroadcast();
                }
                presenter.registerPairBroadcast();
                presenter.pairDevice(presenter.getDiscoveryDevicesList().get(position));
            }
        } else if ((isPairedAdapter) && (isSettingsTapped) && (view != null)) {
            showPopUpMenu(presenter.getPairedDevices().get(position), view);
        } else if ((isPairedAdapter) && (!isSettingsTapped) && (view == null)) {
            try {
                presenter.setSelectedDevice(presenter.getPairedDevices().get(position));
                if (isDeviceHaveBluetoothAndPermissionGranted()) {
                    checkIsDeviceReachAble();
                } else {
                    openAppSettings();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onRecyclerViewTapped: end");
        }
    }

    private void checkIsDeviceReachAble() {
        Log.d(TAG, "checkIsDeviceReachAble: start");
        if (!presenter.isDeviceBluetoothIsTurnedOn()) {
            presenter.turnOnBluetooth(true);
        }
        showDiscoveryProgressBar(true);
        resetAdapter(false);
        updateListSize(0, false);
        presenter.setDeviceDiscoveryForChatActivity(true);
        presenter.registerDeviceDiscoveryBroadcast();
        Log.d(TAG, "checkIsDeviceReachAble: end");
    }

    @Override
    public void isSelectedDeviceIsReachable() {
        Log.d(TAG, "isSelectedDeviceIsReachable: start");
        if (presenter.getDiscoveryDevicesList().contains(presenter.getSelectedDevice())) {
            if (presenter.getSelectedDevice() != null) {
                ((BluetoothApplication) getActivity().getApplication()).getService().startClient(presenter.getSelectedDevice());
            } else {
                Log.e(TAG, "isSelectedDeviceIsReachable: Selected device is NULL");
            }

        } else {
            showToast(null, R.string.device_not_reachable);
        }
        Log.d(TAG, "isSelectedDeviceIsReachable: end");
    }

    @Override
    public void startChatActivity() {
        Intent chatIntent = new Intent(getContext(), CommunicationActivity.class);
        startActivity(chatIntent);
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
            showView(mainFragmentBinding.pairedDevicesTextView, true);
            showView(mainFragmentBinding.pairedDevicesRecyclerView, true);
            showView(mainFragmentBinding.availableDevicesTextView, true);
            showView(mainFragmentBinding.availableDevicesRecyclerView, true);
            showView(mainFragmentBinding.bluetoothTurendOffTextView, false);
            showView(mainFragmentBinding.numberOfAvailableDevices, true);
            showView(mainFragmentBinding.numberOfPairedDevicesTextView, true);
            mainFragmentBinding.searchBluetoothButton.setEnabled(true);
        } else {
            showView(mainFragmentBinding.pairedDevicesTextView, false);
            showView(mainFragmentBinding.pairedDevicesRecyclerView, false);
            showView(mainFragmentBinding.availableDevicesTextView, false);
            showView(mainFragmentBinding.availableDevicesRecyclerView, false);
            showView(mainFragmentBinding.bluetoothTurendOffTextView, true);
            showView(mainFragmentBinding.numberOfAvailableDevices, false);
            showView(mainFragmentBinding.numberOfPairedDevicesTextView, false);
            mainFragmentBinding.searchBluetoothButton.setEnabled(false);
            showView(mainFragmentBinding.discoverProgressBar, false);
        }
    }

    private void showView(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}
