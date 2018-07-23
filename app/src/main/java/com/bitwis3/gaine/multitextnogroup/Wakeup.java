package com.bitwis3.gaine.multitextnogroup;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class Wakeup extends AppCompatActivity {

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    MediaPlayer mp;
    AudioManager audioManager;
    int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
         current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);

        KeyguardManager.KeyguardLock lock = ((KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock(KEYGUARD_SERVICE);
        PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock wake = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        lock.disableKeyguard();
        wake.acquire(20000);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
         mp = MediaPlayer.create(this, notification);
         mp.setLooping(true);
         mp.start();
    }

    @Override
    protected void onDestroy() {
        Log.i("JOSH", "ondestroy");
        super.onDestroy();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        if(mp.isPlaying()){
            mp.stop();
            mp.release();
        }
    }

    public void clearAll(View v){
        SharedPreferences.Editor editor = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit();
        editor.putString("message","");
        editor.putString("break_thru_mode_on_off", "off");
        editor.putString("auto_reply_mode_on_off", "off");
        editor.apply();
        Intent i = new Intent(this, AutoReplyService.class);
        i.putExtra("turnOff", "turnOff");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i);
        } else {
            startService(i);
        }
        Intent i2 = new Intent(this, BreakThru.class);
        i2.putExtra("turnOff", "turnOff");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i2);
        } else {
            startService(i2);
        }
        this.finish();
    }


}
