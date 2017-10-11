package com.example.rajasaboor.bluetoothprototype.communication;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.adapter.ConversationAdapter;
import com.example.rajasaboor.bluetoothprototype.databinding.CommunicationFragmentBinding;
import com.example.rajasaboor.bluetoothprototype.model.Message;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        communicationFragmentBinding.setConnectionHandler(this);
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
        Log.d(TAG, "printConversation: ------------------------------------");
        for (Message message : presenter.getMessageList()) {
            Log.d(TAG, "printConversation: Is My Message ---> " + message.isMyMessage());
            Log.d(TAG, "printConversation: My ---> " + message.getMyMessage());
            Log.d(TAG, "printConversation: Message Time ---> " + new SimpleDateFormat("h:m a", Locale.US).format(new Date(message.getMessageTime())));
            Log.d(TAG, "printConversation: Image Uri ---> " + message.getSelectedImageUri());
            Log.d(TAG, "printConversation: ***********************");
        }
        Log.d(TAG, "printConversation: ------------------------------------");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: start");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BuildConfig.CONVERSATION_LIST, (ArrayList<? extends Parcelable>) presenter.getMessageList());
        Log.d(TAG, "onSaveInstanceState: end");
    }

    @Override
    public void updateConversation(Message message) {
        ConversationAdapter conversationAdapter = (ConversationAdapter) communicationFragmentBinding.conversationRecyclerView.getAdapter();

        if (conversationAdapter != null) {
            Log.d(TAG, "updateConversation: Adapter is good ! Updating");
            conversationAdapter.updateConversation(message);
            communicationFragmentBinding.conversationRecyclerView.scrollToPosition(conversationAdapter.getItemCount() - 1);
        } else {
            Log.d(TAG, "updateConversation: Conversation adapter is NULL ! Not able to update the adapter");
        }
//        presenter.deleteImagesDirectory(getImagesDirectory());
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

    @Override
    public File getImagesDirectory() {
        try {
            return getContext().getDir(BuildConfig.IMAGES_DIR_NAME, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
