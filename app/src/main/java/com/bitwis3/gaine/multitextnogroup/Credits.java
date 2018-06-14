package com.bitwis3.gaine.multitextnogroup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import spencerstudios.com.fab_toast.FabToast;

public class Credits extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_javatest);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void MIT(View V) {

        try{
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://opensource.org/licenses/MIT?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=6607"));
        startActivity(browserIntent);
    } catch(Exception e){
            FabToast.makeText(this, "There was an error opening, please try with internet",
                    FabToast.LENGTH_LONG, FabToast.ERROR, FabToast.POSITION_DEFAULT).show();
        }
    }
    public void APACHE(View v){
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://opensource.org/licenses/Apache-2.0?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=6819"));
            startActivity(browserIntent);
        }catch(Exception e){
            FabToast.makeText(this, "There was an error opening, please try with internet",
                    FabToast.LENGTH_LONG, FabToast.ERROR, FabToast.POSITION_DEFAULT).show();
        }
    }
    public void PRIVACY(View V) {

        try{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vS0f1URBeRQ6Lrhi1W5KxC6eDjxB46OwZOLv8VKoE6DmN5kpESA7EqHNB0qbt08amyr5Iv-Yx_HXubK/pub"));
            startActivity(browserIntent);
        } catch(Exception e){
            FabToast.makeText(this, "There was an error opening, please try with internet",
                    FabToast.LENGTH_LONG, FabToast.ERROR, FabToast.POSITION_DEFAULT).show();
        }
    }

}