package com.example.rajasaboor.bluetoothprototype.discoverdeviceslist;

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
import android.widget.Toast;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
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
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: start");
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: end");

        outState.putParcelableArrayList(BuildConfig.DEVICE_LIST_KEY, (ArrayList<? extends Parcelable>) ((DevicesListPresenter) presenter).getDeviceList());
        outState.putBoolean(BuildConfig.IS_NEW_DEVICE_FOUND_KEY, ((DevicesListPresenter) presenter).isNewDeviceFound());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: start");
        super.onActivityCreated(savedInstanceState);
        try {
            if (savedInstanceState != null) {
                Log.d(TAG, "onCreate: Result fetched from the bundle ===> " + savedInstanceState.getBoolean(BuildConfig.IS_NEW_DEVICE_FOUND_KEY, false));
                // TODO: 9/12/2017 Is this typecast is a normal thing while getting the members of the presenter ???
                ((DevicesListPresenter) presenter).setNewDeviceFound(savedInstanceState.getBoolean(BuildConfig.IS_NEW_DEVICE_FOUND_KEY, false));
                ((DevicesListPresenter) presenter).setDeviceList(savedInstanceState.<BluetoothDevice>getParcelableArrayList(BuildConfig.DEVICE_LIST_KEY));
                presenter.addNameInListFromBluetoothList(((DevicesListPresenter) presenter).getDeviceList());
            } else {
                Log.d(TAG, "onCreate: Bundle is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        presenter.pairingProcessBroadcast();
        IntentFilter bluetoothIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(((DevicesListPresenter) presenter).getBluetoothReceiver(), bluetoothIntent);
        */
        Log.d(TAG, "onActivityCreated: end");
    }

    public DevicesListContract.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(DevicesListContract.Presenter presenter) {
        Log.d(TAG, "setPresenter: start");
        this.presenter = presenter;
        Log.d(TAG, "setPresenter: end");
    }

    public void initAdapter() {
        deviceNameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ((DevicesListPresenter) presenter).getDeviceNameList());
        listFragmentBinding.availiableDevicesListView.setAdapter(deviceNameAdapter);
        listFragmentBinding.availiableDevicesListView.setOnItemClickListener((AdapterView.OnItemClickListener) presenter);
    }

    @Override
    public void resetDeviceListAdapter() {
        ((DevicesListPresenter) presenter).getDeviceList().clear();
        ((DevicesListPresenter) presenter).getDeviceNameList().clear();
        deviceNameAdapter.clear();
        deviceNameAdapter.notifyDataSetChanged();
    }


    @Override
    public void refreshListAdapter() {
        if (deviceNameAdapter != null) {
            deviceNameAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
