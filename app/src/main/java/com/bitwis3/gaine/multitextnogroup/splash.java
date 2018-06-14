package com.bitwis3.gaine.multitextnogroup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import org.ankit.perfectdialog.EasyCustomDialog;
import org.ankit.perfectdialog.EasyCustomDialogListener;

import spencerstudios.com.bungeelib.Bungee;
import spencerstudios.com.fab_toast.FabToast;

public class splash extends AppCompatActivity implements Animation.AnimationListener {



    AlphaAnimation alphaAnimation2;
    ImageView imageView;
    AlphaAnimation alphaAnimation;
    SQLiteToRoomConverter dbConverter;
    Intent intent = null;
    String[] p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN), WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
       getSupportActionBar().hide();


         dbConverter = new SQLiteToRoomConverter(splash.this);
         initIntent();

          p = new String[4];
         p[2] = Manifest.permission.READ_PHONE_STATE;
         p[1] = Manifest.permission.READ_CONTACTS;
         p[0] = Manifest.permission.SEND_SMS;
        p[3] = Manifest.permission.RECEIVE_SMS;



         if(!needPermissions(p,1)) {
             EasyCustomDialog dialog = new EasyCustomDialog.Builder(this, "Let's Begin!")
                     .setSubTitle("We need to request the following permissions: " +
                             "\n1 - Access to read your contacts. (So you can select recipients for messages.)" +
                             "\n2-  Access to send/receive SMS messages. (Which is the magic of this app!)" +
                             "\n3 - Access to read your phone state. (Many devices require this permission as well to send messages)" +
                             "\n\nIs this ok?\n\n" +
                             "Note - if you deny a permission permanently, this app will not open. You can change this in your phone's app settings or simply uninstall and reinstall the app." +
                             "")
                     .setHeader("Thank You For Downloading Multi-Text!")
                     .setIcon(getResources().getDrawable(R.mipmap.icon))
                     //.setIcon(ContextCompat.getDrawable(this, R.drawable.animlogo))
                     .setPositiveBtnText("OK!")
                     .setNegativeBtnText("No")
                     .onConfirm(new EasyCustomDialogListener() {
                         @Override
                         public void execute() {
                             askForPermission(p, 1);
                         }
                     })
                     .onCancel(new EasyCustomDialogListener() {
                         @Override
                         public void execute() {
                             FabToast.makeText(splash.this, "Permissions must be granted for this app to be functional.",
                                     Toast.LENGTH_LONG, FabToast.ERROR, FabToast.POSITION_CENTER).show();
                             finish();
                         }
                     })
                     .build();
         }else{
             startAll();
         }



    }

    public void startAll(){


        imageView = (ImageView)findViewById(R.id.image);
        alphaAnimation= new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(1500);
        alphaAnimation.setAnimationListener(splash.this);
        alphaAnimation2= new AlphaAnimation(1f, 0f);
        alphaAnimation2.setDuration(400);
        alphaAnimation2.setAnimationListener(splash.this);

        imageView.startAnimation(alphaAnimation);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }



    @Override
    public void onAnimationEnd(Animation animation) {

        if(animation.equals(alphaAnimation)){
            imageView.startAnimation(alphaAnimation2);
        }
        if(animation.equals(alphaAnimation2)){
            imageView.setVisibility(View.GONE);

                startActivity(intent);
                Bungee.slideUp(splash.this);



        }



    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void initIntent(){

        if(dbConverter.dbExists("_contactDB")){
            intent = new Intent(splash.this,DBtransition.class);
        }
        else{
            intent = new Intent(splash.this,Home.class);
        }

    }



    @Override
    protected void onStop() {
        super.onStop();
        splash.this.finish();
    }


    private void askForPermission(String[] permission, Integer requestCode) {
        for(int i = 0; i< permission.length; i++){
            if (ContextCompat.checkSelfPermission(splash.this, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(splash.this, permission, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i = 0; i< permissions.length; i++){
            if(grantResults.length>0 && ActivityCompat.checkSelfPermission(splash.this, permissions[i]) == PackageManager.PERMISSION_GRANTED){
            }else{
                //after asking for all permissions, if one was denied this will be called
                FabToast.makeText(splash.this, "Permissions must be granted for this app to be functional.",
                        Toast.LENGTH_LONG,FabToast.ERROR, FabToast.POSITION_CENTER).show();
                finish();
            }
            // all permissions are granted call your next method here
                startAll();
        }
    }

    private boolean needPermissions(String[] permission, Integer requestCode){
        boolean show = true;
        for(int i = 0; i< permission.length; i++){
            if (ContextCompat.checkSelfPermission(splash.this, permission[i]) != PackageManager.PERMISSION_GRANTED) {
              show = false;
            }
        }
        return show;
    }

    Seed.CrashHandler crashHandler = new Seed.CrashHandler(new HandlingClass());
    class HandlingClass implements Seed.CrashHandler.CrashHandlingInterface{

        @Override
        public void executeOnCrash() {
            Log.i("JOSHCRASH", "SUCCESSFULLY CAUGHT BROTHA");
            SharedPreferences.Editor editor = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit();
            editor.putString("message","");
            editor.putString("auto_reply_mode_on_off", "off");
            editor.apply();
        }
    }
}
