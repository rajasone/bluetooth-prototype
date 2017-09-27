package com.example.rajasaboor.bluetoothprototype.communication;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.databinding.CommunicationFragmentBinding;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public class CommunicationFragment extends Fragment {
    private static final String TAG = CommunicationFragment.class.getSimpleName();
    private CommunicationFragmentBinding communicationFragmentBinding;

    public static CommunicationFragment newInstance() {
        return new CommunicationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        communicationFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.communication_fragment, container, false);

        return communicationFragmentBinding.getRoot();
    }
}
