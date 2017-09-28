package com.example.rajasaboor.bluetoothprototype.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rajaSaboor on 9/28/2017.
 */

public class Message implements Parcelable {
    private static final String TAG = Message.class.getSimpleName();
    private String myMessage;
    private String senderMessage;


    public Message() {
    }

    public Message(String myMessage, String senderMessage) {
        this.myMessage = myMessage;
        this.senderMessage = senderMessage;
    }

    public String getMyMessage() {
        return myMessage;
    }

    public String getSenderMessage() {
        return senderMessage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.myMessage);
        dest.writeString(this.senderMessage);
    }

    protected Message(Parcel in) {
        this.myMessage = in.readString();
        this.senderMessage = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
