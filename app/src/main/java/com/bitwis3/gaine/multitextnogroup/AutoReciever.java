package com.bitwis3.gaine.multitextnogroup;

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
import android.widget.Toast;

import java.util.ArrayList;

import spencerstudios.com.fab_toast.FabToast;

public class AutoReciever extends BroadcastReceiver {
    // Get the object of SmsManager
    SmsManager smsManager;
    Context context;
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



            SharedPreferences prefs = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE);
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
                        db.multiDOA().insertAll(c);


                        Log.i("JOSHSMS", "inputted!!!");
                        if (message.length() > 155) {
                            ArrayList<String> parts = smsManager.divideMessage(message);
                            smsManager.sendMultipartTextMessage(smsSender, null,
                                    parts, null, null);
                        } else {
                            smsManager.sendTextMessage(smsSender, null,
                                    message, null, null);
                        }
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

       private boolean needToStopThis(String sender, String smsBody){
        boolean stop = false;

        SharedPreferences prefs = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE);
        String lastSentMessage = prefs.getString("lastSentMessage", "");
        String lastNumberReceived = prefs.getString("lastNumberReceived", "");
        if(sender.equals(lastNumberReceived)){
            if(lastSentMessage.equals(smsBody)){
                             stop = true;
            }
        }
        return stop;
       }

    Seed.CrashHandler handler = new Seed.CrashHandler(new AutoReciever.HandlingClass());
    class HandlingClass implements Seed.CrashHandler.CrashHandlingInterface{

        @Override
        public void executeOnCrash() {
            Log.i("JOSHCRASH", "SUCCESSFULLY CAUGHT BROTHA");
            SharedPreferences.Editor editor = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit();
            editor.putString("message","");
            editor.putString("auto_reply_mode_on_off", "off");
            editor.apply();

        }
    }

}