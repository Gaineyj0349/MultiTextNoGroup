package com.bitwis3.gaine.multitextnogroup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

Context context;



    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        Log.i("JOSHbroadcast", "helper1");


        // TODO: This method is called when the BroadcastReceiver is receiving


        if (intent.hasExtra("timed")) {
            Log.i("JOSHbroadcast", "helper2");
            Intent i = new Intent(context, ServiceToSendPending.class);
            if (intent.hasExtra("timeInMillis")) {
                long timeInMillis = intent.getLongExtra("timeInMillis", 0);
                i.putExtra("timeInMillis", timeInMillis);
            }

            i.putExtra("notfromboot", "notfromboot");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(i);
            } else {
                context.startService(i);
            }

        } else if ("android.intent.action.BOOT_COMPLETED" .equals(intent.getAction())) {

            clearAutoPrefs();
            Log.i("JOSH", "BOOT COMPLETE");
            Intent i = new Intent(context, ServiceToSendPending.class);
            i.putExtra("fromboot", "fromboot");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(i);
            } else {
                context.startService(i);
            }
        }



    }
   void clearAutoPrefs(){
       SharedPreferences.Editor editor = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit();
       editor.putString("message", "");
       editor.putString("auto_reply_mode_on_off", "off");
       editor.putString("break_thru_mode_on_off", "off");
       editor.apply();
    }

    Seed.CrashHandler handler = new Seed.CrashHandler(new MyReceiver.HandlingClass(), context);
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
}
