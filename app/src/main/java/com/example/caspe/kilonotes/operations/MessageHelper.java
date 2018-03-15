package com.example.caspe.kilonotes.operations;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by caspe on 22-2-2018.
 */

public class MessageHelper extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("MESSAGE: ", remoteMessage.getData()  .toString());
    }
}
