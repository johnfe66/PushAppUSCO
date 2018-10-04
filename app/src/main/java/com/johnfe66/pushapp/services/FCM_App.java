package com.johnfe66.pushapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.johnfe66.pushapp.MainActivity;
import com.johnfe66.pushapp.R;

public class FCM_App extends FirebaseMessagingService {


    public FCM_App() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        System.out.println("Token generado: "+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size()>0 && remoteMessage.getNotification()!=null){

            generarNotificacion(remoteMessage);
        }

    }

    private void generarNotificacion(RemoteMessage remoteMessage) {


        String mensaje = remoteMessage.getNotification().getBody();
        String titulo =remoteMessage.getNotification().getTitle();


        float valorDescuento= Float.valueOf(remoteMessage.getData().get("descuento"));

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        intent.putExtra("descuento", valorDescuento);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri sonidoDefectoUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String channelId = getString(R.string.channel_id);
        String channelName= getString(R.string.channel_name);

        NotificationCompat.Builder builderNotification = new NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.ic_comprar)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setAutoCancel(true)
                .setSound(sonidoDefectoUri)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            builderNotification.setColor(valorDescuento <=0.5 ?
                    ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)
                    :ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 50, 100});

            notificationManager.createNotificationChannel(channel);

            builderNotification.setChannelId(channelId);



        }

        notificationManager.notify("tag",0,builderNotification.build());
        //  builderNotification.notify("tag", 0, no);






    }
}
