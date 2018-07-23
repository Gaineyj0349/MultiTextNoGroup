package com.bitwis3.gaine.multitextnogroup;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import spencerstudios.com.fab_toast.FabToast;

public class ServiceToSendPending extends Service {

    DBRoom db;
        Handler handler;
        SmsManager smsManager;

    public ServiceToSendPending() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i("JOSHser", "Service has been started!!");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        db = Room.databaseBuilder(this,DBRoom.class, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();


        startForeground(1, getNotificationforService());
        handler = new Handler();
         smsManager = SmsManager.getDefault();
        Log.i("JOSHser", "onstartcommand has been started!!");



        if (intent.hasExtra("notfromboot")) {

            List<Contact> pendingTextsList = db.multiDOA().allPendingWithMillis(intent.getLongExtra("timeInMillis", 0));
            Log.i("JOSHser", "listsize " + pendingTextsList.size());
            sendMessages(0, pendingTextsList);
        }else if(intent.hasExtra("fromboot")){

            if(checkForOldAndMissedMessages()){
                showNotificationForOld();
            }

            List<Contact> rebuildPendingList = db.multiDOA().getAllPendingAfterBoot(System.currentTimeMillis());

            int size = rebuildPendingList.size();
            int latestCode = db.multiDOA().getLatestCode();
            Log.i("JOSHdlatestcode", String.valueOf(latestCode));

            for(int i = 0; i< size; i++) {
                    AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                    Intent intent1 = new Intent(this, MyReceiver.class);
                    intent1.putExtra("timed", "timed");
                    intent1.putExtra("timeInMillis", rebuildPendingList.get(i).getTimeInMillis());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, rebuildPendingList.get(i).getCode1(),
                            intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, rebuildPendingList.get(i).getTimeInMillis(), pendingIntent);

            }

            stopForeground(true);
            stopSelf();
            onDestroy();

        }




            return START_NOT_STICKY;
    }

    private void showNotificationForOld() {
        RemoteViews contentView = new RemoteViews("com.bitwis3.gaine.multitextnogroup", R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.icon);
        contentView.setTextViewText(R.id.notificationtext, "Your Scheduled Message(s) were not sent! Click to handle.");

        Notification.Builder builder =
                new Notification.Builder(this);

        builder.setSmallIcon
                (R.drawable.ic_message_black_24dp);
        Intent intent2 =
                new Intent(this, ActivityLog.class);

        intent2.putExtra("missed", "missed");

        int latestCode = db.multiDOA().getLatestCode()+1000000;
        Log.i("JOSHser", String.valueOf(latestCode));
        PendingIntent pendingIntent;
        if(latestCode >=  0){

            pendingIntent =
                    PendingIntent.getActivity(this, db.multiDOA().getLatestCode()+1000000
                            , intent2, 0);
        }else{
            pendingIntent =
                    PendingIntent.getActivity(this, 1
                            , intent2, 0);
        }
        intent2.putExtra("code", latestCode);

        builder.setContentIntent(pendingIntent);
        builder.setContent(contentView);
        builder.setContentTitle("Your Scheduled Messages were not sent! Click to handle.");
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_LOW);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "default use";
            String description = "get reminders from this app";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel mChannel = new NotificationChannel("ChannelID", name, importance);
            mChannel.setDescription(description);
            mChannel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            builder.setChannelId("ChannelID");
        }

        Notification notification = builder.build();


        NotificationManager notificationMgr = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);


        notificationMgr.notify(latestCode, notification);
    }

    private boolean checkForOldAndMissedMessages() {
        boolean check = false;
        List<Contact> oldMessages = db.multiDOA().getAllMissed(System.currentTimeMillis());
        List<Contact> missedMessages = db.multiDOA().getAllUnsentForService();
        oldMessages.addAll(missedMessages);
        if(oldMessages.size()>0){
            check = true;

            for(Contact c : oldMessages){
                db.multiDOA().updateTypeEntryWithID(c.getId(), "missed");
            }
        }
        return check;
    }


    private void sendMessages(int i, List<Contact> pendingTextsList) {



try{
        if(Seed.isTelephonyMobileConnected(this)) {

            sendMessage(i, pendingTextsList);

            getNotification();
        }
        else{
            for (Contact c : pendingTextsList) {
                db.multiDOA().updateTypeEntryWithID(c.getId(), "missed");
                }
                showNotificationForOld();
        }
}catch (Exception e){
    FabToast.makeText(this, "We do apologize, but your device is not supported currently.", Toast.LENGTH_LONG, FabToast.ERROR, FabToast.POSITION_DEFAULT).show();

}

        stopForeground(true);
        stopSelf();
        onDestroy();
    }

    private void sendMessage(final int i, final List<Contact> pendingTextsList){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("JOSHser", "iteration happened sending to" + pendingTextsList.get(i).getNumber());
                if(pendingTextsList.get(i).getNumber() != null) {
                    if (pendingTextsList.get(i).getMessage().length() > 155) {
                        ArrayList<String> parts = smsManager.divideMessage(pendingTextsList.get(i).getMessage());
                        smsManager.sendMultipartTextMessage(pendingTextsList.get(i).getNumber(), null,
                                parts, null, null);
                    } else {
                        smsManager.sendTextMessage(pendingTextsList.get(i).getNumber(), null,
                                pendingTextsList.get(i).getMessage(), null, null);
                    }
                    db.multiDOA().updateTypeEntryWithID(pendingTextsList.get(i).getId(), "timed_text_sent");

                    if (i < pendingTextsList.size() - 1) {
                        sendMessage(i + 1, pendingTextsList);
                    }
                }else{
                    return;
                }
            }
        },100);
    }

    private void getNotification(){
        RemoteViews contentView = new RemoteViews("com.bitwis3.gaine.multitextnogroup", R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.icon);
        contentView.setTextViewText(R.id.notificationtext, "Your scheduled message(s) were sent! Click to see.");

        Notification.Builder builder =
                new Notification.Builder(this);

        builder.setSmallIcon
                (R.drawable.ic_message_black_24dp);
        Intent intent2 =
                new Intent(this, ActivityLog.class);

        int latestCode = db.multiDOA().getLatestCode();
        Log.i("JOSHser", String.valueOf(latestCode));
        PendingIntent pendingIntent;
        if(latestCode >=  0){
             pendingIntent =
                    PendingIntent.getActivity(this, db.multiDOA().getLatestCode()+1
                            , intent2, 0);
        }else{
             pendingIntent =
                    PendingIntent.getActivity(this, 1
                            , intent2, 0);
        }
        intent2.putExtra("code", latestCode);

        builder.setContentIntent(pendingIntent);
        builder.setContent(contentView);
        builder.setContentTitle("Your scheduled message(s) were sent! Click to see.");
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_LOW);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "default use";
            String description = "get reminders from this app";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel mChannel = new NotificationChannel("ChannelID", name, importance);
            mChannel.setDescription(description);
            mChannel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            builder.setChannelId("ChannelID");
        }

        Notification notification = builder.build();


        NotificationManager notificationMgr = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);


        notificationMgr.notify(latestCode, notification);
    }

    private Notification getNotificationforService(){
        RemoteViews contentView = new RemoteViews("com.bitwis3.gaine.multitextnogroup", R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.icon);
        contentView.setTextViewText(R.id.notificationtext, "Multi-Text is briefly working in the background..");

        Notification.Builder builder =
                new Notification.Builder(this);

        builder.setSmallIcon
                (R.drawable.ic_message_black_24dp);

        Intent intent2 =
                new Intent(this, ActivityLog.class);


        int latestCode = db.multiDOA().getLatestCode();
        Log.i("JOSHser", String.valueOf(latestCode));
        PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 1000000000
                            , intent2, 0);



        builder.setContentIntent(pendingIntent);
        builder.setContent(contentView);
        builder.setContentTitle("Your scheduled message(s) are sending.");
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_LOW);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "default use";
            String description = "get reminders from this app";
            int importance = NotificationManager.IMPORTANCE_LOW;
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
