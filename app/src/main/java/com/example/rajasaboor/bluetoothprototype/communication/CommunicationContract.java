package com.example.rajasaboor.bluetoothprototype.communication;

import android.os.Handler;
import android.view.View;

/**
 * Created by rajaSaboor on 9/27/2017.
 */

public interface CommunicationContract {
    interface ActivityView {

    }

    interface FragmentView {
    }

    interface Presenter {
        BluetoothConnectionService getConnectionService();

        void setConnectionService(BluetoothConnectionService connectionService);

        Handler getHandler();

        void setHandler(Handler handler);

        void defineHandler();

        void sendMessage(String message);

    }
}
