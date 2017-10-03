package com.example.rajasaboor.bluetoothprototype.communication;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.PreviewActivity;
import com.example.rajasaboor.bluetoothprototype.PreviewFragment;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.adapter.ConversationAdapter;
import com.example.rajasaboor.bluetoothprototype.databinding.CommunicationFragmentBinding;
import com.example.rajasaboor.bluetoothprototype.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public class CommunicationFragment extends Fragment implements CommunicationContract.FragmentView, View.OnClickListener {
    private static final String TAG = CommunicationFragment.class.getSimpleName();
    private CommunicationFragmentBinding communicationFragmentBinding;
    private CommunicationContract.Presenter presenter;

    public static CommunicationFragment newInstance() {
        return new CommunicationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        communicationFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.communication_fragment, container, false);
        communicationFragmentBinding.setHandler(this);
        initConversationRecyclerView();

        if (savedInstanceState != null) {
            presenter.setMessageList(savedInstanceState.<Message>getParcelableArrayList(BuildConfig.CONVERSATION_LIST));
            ((ConversationAdapter) communicationFragmentBinding.conversationRecyclerView.getAdapter()).updateList(presenter.getMessageList());
        }

        return communicationFragmentBinding.getRoot();
    }

    public void setPresenter(CommunicationContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: start");
        switch (view.getId()) {
            case R.id.send_message_button:
                Log.d(TAG, "onClick: Send button tapped");
                if (communicationFragmentBinding.sendMessageEditText.getText().toString().trim().length() > 0) {
                    presenter.sendMessage(communicationFragmentBinding.sendMessageEditText.getText().toString(), null);
                } else {
                    showToast(null, R.string.enter_valid_input);
                }
                break;
        }
        Log.d(TAG, "onClick: end");
    }

    private void initConversationRecyclerView() {
        communicationFragmentBinding.conversationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ConversationAdapter conversationAdapter = new ConversationAdapter(new ArrayList<Message>());
        communicationFragmentBinding.conversationRecyclerView.setAdapter(conversationAdapter);
    }

    private void printConversation() {
        Log.d(TAG, "updateConversationAdapter: ------------------------------------");
        for (Message message : presenter.getMessageList()) {
            Log.d(TAG, "updateConversationAdapter: My ---> " + message.getMyMessage());
            Log.d(TAG, "updateConversationAdapter: Sender ---> " + message.getSenderMessage());
            Log.d(TAG, "updateConversationAdapter: ***********************");
        }
        Log.d(TAG, "updateConversationAdapter: ------------------------------------");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: start");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BuildConfig.CONVERSATION_LIST, (ArrayList<? extends Parcelable>) presenter.getMessageList());
        Log.d(TAG, "onSaveInstanceState: end");
    }

    @Override
    public void updateConversationAdapter(List<Message> conversationList) {
        ConversationAdapter conversationAdapter = (ConversationAdapter) communicationFragmentBinding.conversationRecyclerView.getAdapter();

        if (conversationAdapter != null) {
            Log.d(TAG, "updateConversationAdapter: Adapter is good ! Updating");
            conversationAdapter.updateList(conversationList);
            communicationFragmentBinding.conversationRecyclerView.scrollToPosition(conversationList.size() - 1);
        } else {
            Log.d(TAG, "updateConversationAdapter: Conversation adapter is NULL ! Not able to update the adapter");
        }

        Log.d(TAG, "updateConversationAdapter: Size of the conversation list ===> " + conversationList.size());
        Log.d(TAG, "updateConversationAdapter: Scroll position ===> " + (conversationList.size() - 1));

        printConversation();
    }

    @Override
    public void showToast(String message, int messageID) {
        if (message == null) {
            Toast.makeText(getContext(), getString(messageID), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void resetChatEditText() {
        communicationFragmentBinding.sendMessageEditText.setText(null);
    }
}
