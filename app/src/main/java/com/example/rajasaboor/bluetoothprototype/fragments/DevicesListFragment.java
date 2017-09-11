package com.example.rajasaboor.bluetoothprototype.fragments;

import android.bluetooth.BluetoothDevice;
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

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.databinding.BluetoothListFragmentBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajaSaboor on 9/8/2017.
 */

public class DevicesListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = DevicesListFragment.class.getSimpleName();
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private OnDeviceClickListener deviceClickListener = null;
    private List<String> deviceNameList = new ArrayList<>();
    private ArrayAdapter<String> deviceNameAdapter = null;

    public static DevicesListFragment newInstance() {
        return new DevicesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        try {
            if (savedInstanceState != null) {
                deviceList = savedInstanceState.getParcelableArrayList("key");
                for (BluetoothDevice device : deviceList) {
                    deviceNameList.add(device.getName());
                }
            } else {
                Log.d(TAG, "onCreate: Bundle is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate: end");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BluetoothListFragmentBinding listFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.bluetooth_list_fragment, container, false);
        deviceNameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, deviceNameList);
        listFragmentBinding.availiableDevicesListView.setAdapter(deviceNameAdapter);
        listFragmentBinding.availiableDevicesListView.setOnItemClickListener(this);
        return listFragmentBinding.getRoot();
    }

    public void setUpListAdapter() {
        if (deviceNameAdapter != null) {
            deviceNameAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: start");
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: end");

        outState.putParcelableArrayList("key", (ArrayList<? extends Parcelable>) deviceList);
    }

    public void addDeviceInList(BluetoothDevice device) {
        if ((device != null) && (!deviceList.contains(device))) {
            deviceList.add(device);
            deviceNameList.add(device.getName());
        }
    }

    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceClickListener(OnDeviceClickListener deviceClickListener) {
        this.deviceClickListener = deviceClickListener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick: Size ===> " + deviceList.size());
        deviceClickListener.onDeviceClickListener(deviceList.get(i));
    }

    public interface OnDeviceClickListener {
        void onDeviceClickListener(BluetoothDevice device);
    }
}
