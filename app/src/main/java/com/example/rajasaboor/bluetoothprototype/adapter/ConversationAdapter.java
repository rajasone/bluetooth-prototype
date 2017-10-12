package com.example.rajasaboor.bluetoothprototype.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.BuildConfig;
import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.communication.CommunicationFragment;
import com.example.rajasaboor.bluetoothprototype.databinding.CommunicationLayoutBinding;
import com.example.rajasaboor.bluetoothprototype.model.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

/**
 * Created by rajaSaboor on 9/28/2017.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private static final String TAG = ConversationAdapter.class.getSimpleName();
    private List<Message> conversationList = new ArrayList<>();

    public ConversationAdapter(List<Message> conversationList) {
        this.conversationList = conversationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommunicationLayoutBinding communicationLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.communication_layout, parent, false);
        return new ViewHolder(communicationLayoutBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setMessage(conversationList.get(position));
    }

    @Override
    public int getItemCount() {
        return (conversationList != null ? conversationList.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private CommunicationLayoutBinding communicationLayoutBinding;
        private ActionMode actionMode;

        ViewHolder(CommunicationLayoutBinding communicationLayoutBinding) {
            super(communicationLayoutBinding.getRoot());
            this.communicationLayoutBinding = communicationLayoutBinding;
        }

        void setMessage(Message message) {
            communicationLayoutBinding.setMessage(message);
            communicationLayoutBinding.setConversationHandler(ConversationAdapter.this);
        }
    }

    public void updateList(List<Message> conversationList) {
        this.conversationList = conversationList;
        notifyDataSetChanged();

    }

    public void updateConversation(Message message) {
        if (!conversationList.contains(message)) {
            this.conversationList.add(message);
        }
        notifyItemChanged(this.conversationList.size() - 1);
    }
}
