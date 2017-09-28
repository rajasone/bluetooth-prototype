package com.example.rajasaboor.bluetoothprototype.communication;

import android.app.Application;
import android.os.Handler;
import android.view.View;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public interface CommunicationContract {
    interface ActivityView {
        Application getApplicationInstance();
    }

    interface FragmentView {
    }

    interface Presenter {
        void sendMessage(String message);

    }
}
