package com.bitwis3.gaine.multitextnogroup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class AutoReplyService extends Service {
    public AutoReplyService() {
    }
    DBRoom db;

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("JOSHser2", "ACTIVE");
        Log.i("JOSHser2", "JOSHHELPER1");
        db = Room.databaseBuilder(this,DBRoom.class, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        startForeground(2, getNotificationforAuto());
        if(intent != null) {
            if (intent.hasExtra("turnOff")) {
                Log.i("JOSHser2", "KILLED");
                stopForeground(true);
                stopSelf();
                onDestroy();
            }
        }else{
            SharedPreferences preferences = getSharedPreferences("AUTO_PREF", MODE_PRIVATE);
            String status = preferences.getString("auto_reply_mode_on_off", "off");
            if(status.equals("on")){
                startForeground(2, getNotificationforAuto());
           }else{
               stopForeground(true);
               stopSelf();
               onDestroy();
           }
        }

        return START_STICKY;
    }


    private Notification getNotificationforAuto(){
        RemoteViews contentView = new RemoteViews("com.bitwis3.gaine.multitextnogroup", R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.icon);
        contentView.setTextViewText(R.id.notificationtext, "Auto Text Mode is ON - tap to change.");

        Notification.Builder builder =
                new Notification.Builder(this);

        builder.setSmallIcon
                (R.drawable.ic_message_black_24dp);

        Intent intent2 =
                new Intent(this, AutoAct.class);


        int latestCode = db.multiDOA().getLatestCode();
        Log.i("JOSHser", String.valueOf(latestCode));
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 1000000001
                        , intent2, 0);



        builder.setContentIntent(pendingIntent);
        builder.setContent(contentView);
        builder.setContentTitle("Auto Text Mode is ON - tap to change.");
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "default use";
            String description = "get reminders from this app";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("ChannelID", name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            builder.setChannelId("ChannelID");
        }

        Notification notification = builder.build();

        return notification;
    }
}
