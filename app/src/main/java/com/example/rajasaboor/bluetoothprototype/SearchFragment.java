package com.example.rajasaboor.bluetoothprototype;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.databinding.SearchingFragmentBinding;

/**
 * Created by rajaSaboor on 9/10/2017.
 */

public class SearchFragment extends Fragment {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private SearchingFragmentBinding searchingFragmentBinding = null;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchingFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.searching_fragment, container, false);

        return searchingFragmentBinding.getRoot();
    }

    public void changeTextView(String text) {
        searchingFragmentBinding.searchingTextView.setText(text);
    }


}
