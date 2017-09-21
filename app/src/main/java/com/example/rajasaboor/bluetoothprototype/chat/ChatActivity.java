package com.example.rajasaboor.bluetoothprototype.chat;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.rajasaboor.bluetoothprototype.R;
import com.example.rajasaboor.bluetoothprototype.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private ChatPresenter presenter;
    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat_fragment_container);

        if (chatFragment == null) {
            chatFragment = ChatFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chat_fragment_container, chatFragment)
                    .commit();
        }

        ChatPresenter chatPresenter = new ChatPresenter();
        chatFragment.setChatPresenter(chatPresenter);
        Log.d(TAG, "onCreate: end");
    }
}
