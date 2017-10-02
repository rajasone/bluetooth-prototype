package com.example.rajasaboor.bluetoothprototype.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rajaSaboor on 9/28/2017.
 */

public class Message implements Parcelable {
    private static final String TAG = Message.class.getSimpleName();
    private String myMessage;
    private String senderMessage;
    private long messageTime;
    private Uri selectedImageUri;


    public Message() {
    }

    public Message(String myMessage, String senderMessage, long messageTime, Uri selectedImageUri) {
        this.myMessage = myMessage;
        this.senderMessage = senderMessage;
        this.messageTime = messageTime;
        this.selectedImageUri = selectedImageUri;
    }

    public String getMyMessage() {
        return myMessage;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.myMessage);
        dest.writeString(this.senderMessage);
        dest.writeLong(this.messageTime);
        dest.writeParcelable(this.selectedImageUri, flags);
    }

    protected Message(Parcel in) {
        this.myMessage = in.readString();
        this.senderMessage = in.readString();
        this.messageTime = in.readLong();
        this.selectedImageUri = in.readParcelable(Uri.class.getClassLoader());
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
