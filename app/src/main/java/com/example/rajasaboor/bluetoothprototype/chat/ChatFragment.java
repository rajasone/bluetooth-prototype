package com.example.rajasaboor.bluetoothprototype.chat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.connectionmanager.ConnectionManager;
import com.example.rajasaboor.bluetoothprototype.databinding.ChatFragmentBinding;

/**
 * Created by rajaSaboor on 9/20/2017.
 */

public class ChatFragment extends Fragment {
    private static final String TAG = ChatFragment.class.getSimpleName();
    private ChatFragmentBinding chatFragmentBinding;
    private ChatContract.Presenter chatPresenter;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: start");
        chatFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment, container, false);
        chatFragmentBinding.setHandler(this);
        Log.d(TAG, "onCreateView: end");

        return chatFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: start");
        super.onActivityCreated(savedInstanceState);
        chatPresenter.setConnectionManager();
//          chatPresenter.startCommunication();
        Log.d(TAG, "onActivityCreated: end");
    }

    public ChatContract.Presenter getChatPresenter() {
        return chatPresenter;
    }

    public void setChatPresenter(ChatPresenter chatPresenter) {
        Log.d(TAG, "setChatPresenter: start");
        this.chatPresenter = chatPresenter;
        Log.d(TAG, "setChatPresenter: end");
    }

    public void onClick() {
        Log.d(TAG, "onClick: start");
        chatPresenter.onSendButtonClick(chatFragmentBinding.chatTextEditText.getText().toString());
        Log.d(TAG, "onClick: end");
    }
}
