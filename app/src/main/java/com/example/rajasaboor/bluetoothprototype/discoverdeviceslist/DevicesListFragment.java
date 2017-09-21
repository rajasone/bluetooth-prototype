package com.example.rajasaboor.bluetoothprototype.discoverdeviceslist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.chat.ChatActivity;
import com.example.rajasaboor.bluetoothprototype.databinding.BluetoothListFragmentBinding;

import java.util.ArrayList;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class DevicesListFragment extends Fragment implements DevicesListContract.FragmentView {
    private static final String TAG = DevicesListFragment.class.getSimpleName();
    private ArrayAdapter<String> deviceNameAdapter = null;
    private DevicesListContract.Presenter presenter = null;
    private BluetoothListFragmentBinding listFragmentBinding = null;
    private int connectionStatus;


    public static DevicesListFragment newInstance() {
        return new DevicesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: end");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.bluetooth_list_fragment, container, false);
        initAdapter();
        return listFragmentBinding.getRoot();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: start");
        super.onResume();
        if (presenter.getBluetoothPairReceiver() == null) {
            presenter.pairingProcessBroadcast();
        }

        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(presenter.getBluetoothPairReceiver(), intent);

        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            presenter.getConnectionManager().start();
        }
        Log.d(TAG, "onResume: end");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: start");
        super.onPause();
        Log.d(TAG, "onPause: end");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: starrt");
        super.onDestroy();
        if (presenter.getBluetoothPairReceiver() != null) {
            getActivity().unregisterReceiver(presenter.getBluetoothPairReceiver());
        }
        Log.d(TAG, "onDestroy: end");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: start");
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: end");

        outState.putParcelableArrayList(BuildConfig.DEVICE_LIST_KEY, (ArrayList<? extends Parcelable>) presenter.getDeviceList());
        outState.putBoolean(BuildConfig.IS_NEW_DEVICE_FOUND_KEY, presenter.isNewDeviceFound());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: start");
        super.onActivityCreated(savedInstanceState);
        try {
            if (savedInstanceState != null) {
                Log.d(TAG, "onCreate: Result fetched from the bundle ===> " + savedInstanceState.getBoolean(BuildConfig.IS_NEW_DEVICE_FOUND_KEY, false));
                presenter.setNewDeviceFound(savedInstanceState.getBoolean(BuildConfig.IS_NEW_DEVICE_FOUND_KEY, false));
                presenter.setDeviceList(savedInstanceState.<BluetoothDevice>getParcelableArrayList(BuildConfig.DEVICE_LIST_KEY));
                presenter.addNameInListFromBluetoothList(presenter.getDeviceList());
            } else {
                Log.d(TAG, "onCreate: Bundle is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onActivityCreated: end");
    }

    public void setPresenter(DevicesListContract.Presenter presenter) {
        Log.d(TAG, "setPresenter: start");
        this.presenter = presenter;
        Log.d(TAG, "setPresenter: end");
    }

    public void initAdapter() {
        deviceNameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, presenter.getDeviceNameList());
        listFragmentBinding.availiableDevicesListView.setAdapter(deviceNameAdapter);
        listFragmentBinding.availiableDevicesListView.setOnItemClickListener((AdapterView.OnItemClickListener) presenter);
    }

    @Override
    public void resetDeviceListAdapter() {
        Log.d(TAG, "resetDeviceListAdapter: start");
        presenter.getDeviceList().clear();
        presenter.getDeviceNameList().clear();
        deviceNameAdapter.clear();
        deviceNameAdapter.notifyDataSetChanged();
        Log.d(TAG, "resetDeviceListAdapter: end");
    }


    @Override
    public void refreshListAdapter() {
        if (deviceNameAdapter != null) {
            deviceNameAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startChatActivity() {
        startActivity(new Intent(getContext(), ChatActivity.class));
    }
}
