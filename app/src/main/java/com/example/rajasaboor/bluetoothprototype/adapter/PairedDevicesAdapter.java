package com.example.rajasaboor.bluetoothprototype.adapter;

import android.bluetooth.BluetoothDevice;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.databinding.PairedDevicesLayoutBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajaSaboor on 9/21/2017.
 */

public class PairedDevicesAdapter extends RecyclerView.Adapter<PairedDevicesAdapter.ViewHolder> {
    private static final String TAG = PairedDevicesAdapter.class.getSimpleName();
    private List<BluetoothDevice> deviceList = new ArrayList<>();

    public PairedDevicesAdapter(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PairedDevicesLayoutBinding devicesLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.paired_devices_layout, parent, false);
        return new ViewHolder(devicesLayoutBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setItem(deviceList.get(position));
    }

    @Override
    public int getItemCount() {
        return (deviceList != null ? deviceList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private PairedDevicesLayoutBinding binding;

        ViewHolder(PairedDevicesLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setItem(BluetoothDevice item) {
            binding.setDeviceName(item.getName());
        }
    }

    void updateList(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
        notifyDataSetChanged();
    }
}
