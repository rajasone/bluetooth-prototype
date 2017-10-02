package com.example.rajasaboor.bluetoothprototype.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.databinding.CommunicationLayoutBinding;
import com.example.rajasaboor.bluetoothprototype.model.Message;

import java.util.ArrayList;
import java.util.List;

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

        ViewHolder(CommunicationLayoutBinding communicationLayoutBinding) {
            super(communicationLayoutBinding.getRoot());
            this.communicationLayoutBinding = communicationLayoutBinding;
        }

        void setMessage(Message message) {
            communicationLayoutBinding.setMessage(message);
        }
    }

    public void updateList(List<Message> conversationList) {
        this.conversationList = conversationList;
        notifyDataSetChanged();

    }
}
