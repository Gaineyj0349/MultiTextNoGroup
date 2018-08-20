package com.bitwis3.gaine.multitextnogroup;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import spencerstudios.com.bungeelib.Bungee;
import spencerstudios.com.fab_toast.FabToast;

public class Main3Activity extends AppCompatActivity {

    ArrayList<String> numbersToSend;
    ArrayList<String> namesToSend;
    String messageIn;
    DBRoom db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Timed Text Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient3));
        final DatePicker datePicker = findViewById(R.id.datePicker);
        final TimePicker timePicker = findViewById(R.id.timePicker);
        numbersToSend = getIntent().getStringArrayListExtra("array");
        namesToSend = getIntent().getStringArrayListExtra("names");
        messageIn = getIntent().getStringExtra("message");
        db = Room.databaseBuilder(this,DBRoom.class, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();



        FloatingActionButton fabm3 = (FloatingActionButton) findViewById(R.id.fabm3);

        if(getIntent().hasExtra("edit")){

            fabm3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_save_black_24dp));
        }

        fabm3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().hasExtra("edit")) {
                    final Calendar cal2 = Calendar.getInstance();
                    cal2.set(Calendar.YEAR, datePicker.getYear());
                    cal2.set(Calendar.MONTH, datePicker.getMonth());
                    cal2.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                    cal2.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    cal2.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    cal2.set(Calendar.SECOND, 01);

                    db.multiDOA().updateDateWithID(getIntent().getIntExtra("edit", 0), cal2.getTimeInMillis());
                    FabToast.makeText(Main3Activity.this, "Successfully Updated", Toast.LENGTH_LONG, FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show();


                    finish();

                } else {


                    Log.i("pJOSHyear", String.valueOf(datePicker.getYear()));
                    Log.i("pJOSHmonth", String.valueOf(datePicker.getMonth()));
                    Log.i("pJOSHday", String.valueOf(datePicker.getDayOfMonth()));
                    Log.i("pJOSHHour", String.valueOf(timePicker.getCurrentHour()));
                    Log.i("pJOSHminute", String.valueOf(timePicker.getCurrentMinute()));

                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, datePicker.getYear());
                    cal.set(Calendar.MONTH, datePicker.getMonth());
                    cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                    cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    cal.set(Calendar.SECOND, 01);

                    Log.i("pJOSHtimeMill", String.valueOf(cal.getTimeInMillis()));


                    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    inputFutureMessages(cal, numbersToSend, messageIn);
                                    Bungee.slideRight(Main3Activity.this);
                                    Main3Activity.this.finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }


                        }
                    };
                    Seed seed = new Seed();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main3Activity.this);
                    builder.setMessage("NOTE - this is using your default texting service, if you are not on an unlimited texting plan, this could incur charges\n\nTimed Message will be sent on " + seed.getLocaleDateString(cal.getTimeInMillis()) + " to: " + getStringOfPeople()).setPositiveButton("Yes", dialogClickListener)

                            .setNegativeButton("No", dialogClickListener).show();


                }
            }
        });

    }

    private void inputFutureMessages(Calendar cal ,ArrayList<String> numbers, String message){

            ArrayList<String> AL = numbers;
            int size = AL.size();

            for (int i = 0; i < size; i++) {


                Contact contact = new Contact();
                contact.setName(namesToSend.get(i));
                contact.setNumber(numbersToSend.get(i));
                contact.setTypeEntry("timed_text_not_sent");
                contact.setMessage(message);
                contact.setTimeInMillis(cal.getTimeInMillis());

                int latestCode = db.multiDOA().getLatestCode();
                Log.i("JOSHdlatestcode", String.valueOf(latestCode));

                if(latestCode >=  0){
                    contact.setCode1(latestCode+1);
                }else{
                    contact.setCode1(1);
                }


                db.multiDOA().insertAll(contact);



            }


            AlarmManager alarmManager = (AlarmManager) Main3Activity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Main3Activity.this, MyReceiver.class);
        intent.putExtra("timed","timed");

        long calTime = cal.getTimeInMillis();
        intent.putExtra("timeInMillis",calTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Main3Activity.this, db.multiDOA().getLatestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calTime, pendingIntent);
        FabToast.makeText(Main3Activity.this , "Success, you can view your future text messages in your Logs, under the Pending tab.",
                Toast.LENGTH_LONG, FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show();
            Bungee.slideRight(this);

            Main3Activity.this.finish();






    }


    public String getStringOfPeople(){
        StringBuilder builders = new StringBuilder();
        for(int i = 0 ; i< namesToSend.size(); i++){
            if(i == 0){
                builders.append(namesToSend.get(i));
            }else {
                builders.append(", ");

                builders.append(namesToSend.get(i));
            }
        }
        return builders.toString();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emergency, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Bungee.slideRight(this);
                this.finish();

                return true;


            case R.id.emergencytoolbar:
                startActivity(new Intent(this, Emergency.class));
                this.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(this);

    }

}
