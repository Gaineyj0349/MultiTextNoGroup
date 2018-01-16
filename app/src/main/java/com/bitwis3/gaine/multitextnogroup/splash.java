package com.bitwis3.gaine.multitextnogroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class splash extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN), WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();

    }
    private class LogoLauncher extends Thread{
        public void run(){
            try{sleep(750);
        }catch (
        InterruptedException e){
                e.printStackTrace();
            }
            Intent intent = new Intent(splash.this,MainActivity.class);
            startActivity(intent);
            splash.this.finish();
        }
    }
}
