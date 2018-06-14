package com.bitwis3.gaine.multitextnogroup;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import spencerstudios.com.bungeelib.Bungee;

public class DBtransition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN), WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dbtransition2);
        getSupportActionBar().hide();

      //create the Room db first, then the converter can transfer the data

       DBRoom db = Room.databaseBuilder(getApplicationContext(),
                DBRoom.class, "_room_database_contactDB")
                 .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        TextView textView = findViewById(R.id.transitions_tv);

        SQLiteToRoomConverter dbConverter = new SQLiteToRoomConverter(this);

            textView.setText("Please wait.. \nPreparing app for first run..");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DBtransition.this, Home.class);
                    startActivity(intent);
                    Bungee.fade(DBtransition.this);
                }
            }, 3000);

            try{
                dbConverter.transferData("_contactDB", db);
            }catch (Exception e){

            }

            dbConverter.deleteFile("_contactDB");

    }

    @Override
    protected void onPause() {
        super.onPause();
        DBtransition.this.finish();
    }
}
