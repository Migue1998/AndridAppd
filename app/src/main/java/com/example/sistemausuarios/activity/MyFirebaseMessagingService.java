package com.example.sistemausuarios.activity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG ="PRUEBAS" ;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

       String from = remoteMessage.getFrom();
       Log.d(TAG,"Mensaje recibido de: " + from );
       if(remoteMessage.getNotification() !=null){
           Log.d(TAG,"Cuerpo del Mensaje: " + remoteMessage.getNotification().getBody() );
       }

    }
}
