package com.bitwis3.gaine.multitextnogroup;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;

import spencerstudios.com.fab_toast.FabToast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AutoReciever extends BroadcastReceiver {
    // Get the object of SmsManager
    SmsManager smsManager;
    Context context;
    Activity activity;
    DBRoom db;


    public void onReceive(Context context, Intent intent) {
        this.context = context;

         db = Room.databaseBuilder(context,DBRoom.class, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        smsManager = SmsManager.getDefault();

        Log.i("JOSHSMS", "HELPER2");



        if ((Telephony.Sms.Intents.SMS_RECEIVED_ACTION ).equals(intent.getAction()) || ("android.intent.action.DATA_SMS_RECEIVED").equals(intent.getAction())) {
            Log.i("JOSHSMS", "HELPER1");
            SharedPreferences prefs = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE);

            String statuswake = prefs.getString("break_thru_mode_on_off", null);


            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();
                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        // Display some error to the user
                        Log.i("JOSHSMS", "SmsBundle had no pdus key");
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                }
            }
            if (statuswake != null) {
                if(statuswake.equals("on")){
                    wakeup(smsBody);
                }
            }


            SharedPreferences.Editor editor = prefs.edit();

            String status = prefs.getString("auto_reply_mode_on_off", "off");

            if(status.equals("on")){

                if(needToStopThis(smsSender, smsBody)){

                }else {

                    String message = prefs.getString("message", "");
                    Contact c = new Contact();
                    c.setTypeEntry("auto_text");
                    c.setTimeInMillis(System.currentTimeMillis());
                    c.setMessage(message);
                    c.setNumber(smsSender);
                    c.setName("Name not available");

                    try {

                        if(Seed.isTelephonyMobileConnected(context)) {
                            c.setTypeEntry("auto_text");
                            Log.i("JOSHSMS", "inputted!!!");
                            if (message.length() > 155) {
                                ArrayList<String> parts = smsManager.divideMessage(message);
                                smsManager.sendMultipartTextMessage(smsSender, null,
                                        parts, null, null);
                            } else {
                                smsManager.sendTextMessage(smsSender, null,
                                        message, null, null);
                            }

                        }else{
                            c.setTypeEntry("missed");
                            showNotificationForOld();
                        }
                        db.multiDOA().insertAll(c);
                       }catch (Exception e){
                    FabToast.makeText(context, "We do apologize, but your device is not supported currently.", Toast.LENGTH_LONG, FabToast.ERROR, FabToast.POSITION_DEFAULT).show();

                }

                    editor.putString("lastSentMessage",message);
                    editor.putString("lastNumberReceived", smsSender);
                    editor.apply();

                }
                Log.i("JOSHSMSon", smsSender + " - " + smsBody);
            }
            Log.i("JOSHSMSoff", smsSender + " - " + smsBody);
        }

       }

    private void wakeup(String messageIn) {
        SharedPreferences prefs = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE);
        String code = prefs.getString("wakecode", "12345");
        if(code.equals(messageIn)){
            context.startActivity(new Intent(context, Wakeup.class));
        }

    }

    private boolean needToStopThis(String sender, String smsBody){
        boolean stop = false;

        SharedPreferences prefs = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE);
        String lastSentMessage = prefs.getString("lastSentMessage", "");
        String lastNumberReceived = prefs.getString("lastNumberReceived", "");
        if(sender.equals(lastNumberReceived)){

                             stop = true;

        }
        return stop;
       }

    Seed.CrashHandler handler = new Seed.CrashHandler(new AutoReciever.HandlingClass(), context);
    class HandlingClass implements Seed.CrashHandler.CrashHandlingInterface{

        @Override
        public void executeOnCrash() {
            Log.i("JOSHCRASH", "SUCCESSFULLY CAUGHT BROTHA");
            SharedPreferences.Editor editor = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit();
            editor.putString("message","");
            editor.putString("break_thru_mode_on_off", "off");
            editor.putString("auto_reply_mode_on_off", "off");
            editor.apply();

        }
    }

    private void showNotificationForOld() {
        RemoteViews contentView = new RemoteViews("com.bitwis3.gaine.multitextnogroup", R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.icon);
        contentView.setTextViewText(R.id.notificationtext, "Your Scheduled Message(s) were not sent! Click to handle.");

        Notification.Builder builder =
                new Notification.Builder(context);

        builder.setSmallIcon
                (R.drawable.ic_message_black_24dp);
        Intent intent2 =
                new Intent(context, ActivityLog.class);

        intent2.putExtra("missed", "missed");

        int latestCode = db.multiDOA().getLatestCode()+1000000;
        Log.i("JOSHser", String.valueOf(latestCode));
        PendingIntent pendingIntent;
        if(latestCode >=  0){

            pendingIntent =
                    PendingIntent.getActivity(context, db.multiDOA().getLatestCode()+1000000
                            , intent2, 0);
        }else{
            pendingIntent =
                    PendingIntent.getActivity(context, 1
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
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            builder.setChannelId("ChannelID");
        }

        Notification notification = builder.build();


        NotificationManager notificationMgr = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);


        notificationMgr.notify(latestCode, notification);
    }
}