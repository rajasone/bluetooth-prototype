package com.example.rajasaboor.bluetoothprototype.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rajaSaboor on 9/28/2017.
 */

public class Message implements Parcelable {
    private static final String TAG = Message.class.getSimpleName();
    private boolean isMyMessage;
    private String textMessage;
    private long messageTime;
    private Uri selectedImageUri;


    public Message() {
    }

    public Message(boolean isMyMessage, String textMessage, long messageTime, Uri selectedImageUri) {
        this.isMyMessage = isMyMessage;
        this.textMessage = textMessage;
        this.messageTime = messageTime;
        this.selectedImageUri = selectedImageUri;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public String getMyMessage() {
        return textMessage;
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
        dest.writeByte(this.isMyMessage ? (byte) 1 : (byte) 0);
        dest.writeString(this.textMessage);
        dest.writeLong(this.messageTime);
        dest.writeParcelable(this.selectedImageUri, flags);
    }

    protected Message(Parcel in) {
        this.isMyMessage = in.readByte() != 0;
        this.textMessage = in.readString();
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
