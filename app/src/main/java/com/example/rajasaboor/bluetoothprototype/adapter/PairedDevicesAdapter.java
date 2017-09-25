package com.example.rajasaboor.bluetoothprototype.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private final boolean isPairedAdapter;
    private final OnRecyclerViewTapped onRecyclerViewTapped;

    public PairedDevicesAdapter(List<BluetoothDevice> deviceList, boolean isPairedAdapter, OnRecyclerViewTapped onRecyclerViewTapped) {
        this.deviceList = deviceList;
        this.isPairedAdapter = isPairedAdapter;
        this.onRecyclerViewTapped = onRecyclerViewTapped;
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PairedDevicesLayoutBinding binding;

        ViewHolder(PairedDevicesLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.pairedDevicesParent.setOnClickListener(this);
        }

        void setItem(BluetoothDevice item) {
            binding.setDevice(item);
            binding.setDeviceType(getDeviceIcon(item.getBluetoothClass()));

            if (!isPairedAdapter) {
                binding.deviceSettingImageView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: start");
            if ((onRecyclerViewTapped == null) || (getAdapterPosition() == RecyclerView.NO_POSITION)) {
                Log.e(TAG, "onClick: Something is wrong with adapter position ---> " + getAdapterPosition());
                return;
            }

            if (isPairedAdapter)
                onRecyclerViewTapped.createPopUpMenu(getAdapterPosition(), view);

            switch (view.getId()) {
                case R.id.paired_devices_parent:
                    Log.d(TAG, "onClick: Case 1");
                    onRecyclerViewTapped.onRecyclerViewTapped(getAdapterPosition(), isPairedAdapter, false);
                    break;
                case R.id.device_setting_image_view:
                    Log.d(TAG, "onClick: Case Settings");
                    onRecyclerViewTapped.onRecyclerViewTapped(getAdapterPosition(), isPairedAdapter, true);
                    break;
            }
            Log.d(TAG, "onClick: end");
        }
    }

    public void updateList(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
        notifyDataSetChanged();
    }

    private int getDeviceIcon(BluetoothClass bluetoothClass) {
        int imageResource = R.drawable.bluetooth_icon;

        switch (bluetoothClass.getDeviceClass()) {
            case BluetoothClass.Device.PHONE_SMART:
                imageResource = R.drawable.phone_icon;
                break;
            case BluetoothClass.Device.COMPUTER_LAPTOP:
            case BluetoothClass.Device.COMPUTER_DESKTOP:
                imageResource = R.drawable.laptop_icon;
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO:
                imageResource = R.drawable.car_icon;
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES:
            case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:
                imageResource = R.drawable.head_phone_icon;
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR:
                imageResource = R.drawable.tv_icon;
                break;
            default:
                Log.d(TAG, "onReceive: Other device");

        }
        return imageResource;
    }

    public interface OnRecyclerViewTapped {
        void onRecyclerViewTapped(int position, boolean isPairedAdapter, boolean isSettingsTapped);

        void createPopUpMenu(int position, View view);
    }
}
