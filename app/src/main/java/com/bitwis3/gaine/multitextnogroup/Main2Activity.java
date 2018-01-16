package com.bitwis3.gaine.multitextnogroup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    static final Integer SMS = 0x5;
    ArrayList<String> numbersToSend;
    ArrayList<String> namesToSend;
    String etMessage = "";
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText = (EditText)findViewById(R.id.editText);
        numbersToSend = getIntent().getStringArrayListExtra("array");
        namesToSend = getIntent().getStringArrayListExtra("names");
        FloatingActionButton fabsend = (FloatingActionButton) findViewById(R.id.fabsend);
        fabsend.setOnClickListener(ask);
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs


        Main2Activity.this.finish();
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Main2Activity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{permission}, requestCode);
            }
        } else {
            switch (requestCode) {
                case 5:

                    sendTheMessage(numbersToSend,etMessage);


                    break;
                //Accounts

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {


                case 5:
                    sendTheMessage(numbersToSend, etMessage);

                    break;
                //Accounts
                case 6:

            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied, reset permissions for this app in your System -> App settings", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Permission denied, reset permissions for this app in your System -> App settings", Toast.LENGTH_SHORT).show();

            Main2Activity.this.finish();
        }
    }

    public void sendTheMessage(ArrayList<String> numbers, String message) {
        ArrayList<String> AL = numbers;
        int size = AL.size();

        for (int i = 0; i < size; i++) {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numbers.get(i), null,
                    message, null, null);

        }
        Main2Activity.this.finish();
    }

    private View.OnClickListener ask = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {


                case R.id.fabsend:
                    etMessage = editText.getText().toString().trim();
                    if (etMessage.length()>0) {
                        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        askForPermission(Manifest.permission.SEND_SMS, SMS);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        break;
                                }

                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                        builder.setMessage("Roll out Message to: "+getStringOfPeople()).setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();



                    }else{
                        Toast.makeText(Main2Activity.this,"There is no message to send!",
                                Toast.LENGTH_LONG).show();
                    }
                    break;

            }
        }
    };

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

}

