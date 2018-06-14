package com.bitwis3.gaine.multitextnogroup

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_auto.*
import spencerstudios.com.fab_toast.FabToast




class AutoAct : AppCompatActivity(), CompoundButton.OnCheckedChangeListener{



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Auto Text Mode"
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.gradient1))

        val font = Typeface.createFromAsset(AutoAct@this.getAssets(), "Acme-Regular.ttf")
        autoET.setTypeface(font)
        switch_auto.setTypeface(font)
        switch_auto.setOnCheckedChangeListener(this)

        initSwitchStatus()


    }

    private fun initSwitchStatus() {
        val prefs = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE)
          val status = prefs.getString("auto_reply_mode_on_off", null)
        val message = prefs.getString("message", "")
        if (status != null) {
           when(status){
               "on" ->{
                   toggleOn()
                   autoET.setText(message)}
               "off" -> {toggleOff()}
           }
        }

    }

    fun autoOn(){

        if(autoET.getText().toString().length > 0 ){
            val i = Intent(this, AutoReplyService::class.java)
            i.putExtra("message", autoET.getText().toString())
            i.putExtra("auto_reply", "auto_reply")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(i)
            } else {
                startService(i)
            }
            FabToast.makeText(this, "Auto-Text mode is ON. A notification has been created for quick control.",
                    Toast.LENGTH_LONG, FabToast.SUCCESS, FabToast.POSITION_CENTER).show()

        }else{
            toggleOff()
            autoET.setText("")
            val editor = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit()
            editor.putString("message", autoET.getText().toString())
            editor.putString("auto_reply_mode_on_off", "off")
            editor.apply()

            val i = Intent(this, AutoReplyService::class.java)
            i.putExtra("turnOff", "turnOff")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(i)
            } else {
                startService(i)
            }
            FabToast.makeText(this, "Message Body can not be empty.", Toast.LENGTH_LONG, FabToast.WARNING, FabToast.POSITION_CENTER).show()

        }


    }
    fun autoOff(){
        toggleOff()

        val editor = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit()
        editor.putString("message", autoET.getText().toString())
        editor.putString("auto_reply_mode_on_off", "off")
        editor.apply()
        val i = Intent(this, AutoReplyService::class.java)
        i.putExtra("turnOff", "turnOff")
        i.putExtra("auto_reply", "auto_reply")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i)
        } else {
            startService(i)
        }

        FabToast.makeText(this, "Auto-Text Mode is off!", Toast.LENGTH_LONG, FabToast.INFORMATION, FabToast.POSITION_CENTER).show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        val editor = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit()
        editor.putBoolean("showAd", true)
        editor.apply()


        Seed.hideSoftKeyboard(AutoAct@this)
        var mode: String = ""
        if(isChecked){
            switch_auto.text = "Turn Off  "
            autoET.isEnabled = false
            mode = "on"
        }else{
            switch_auto.text = "Turn On  "
            autoET.isEnabled = true
            mode = "off"
        }


        editor.putString("message", autoET.getText().toString())
        editor.putString("auto_reply_mode_on_off", mode)
        editor.apply()

        if(isChecked){
            autoOn()
        }else{
            autoOff()
        }

    }

    fun toggleOn(){
        autoET.isEnabled = false
        switch_auto.setOnCheckedChangeListener (null);
        switch_auto.setChecked(true);
        switch_auto.text = "Turn Off  "
        switch_auto.setOnCheckedChangeListener (this);
    }
    fun toggleOff(){
        autoET.isEnabled = true
        switch_auto.setOnCheckedChangeListener (null);
        switch_auto.setChecked(false);
        switch_auto.text = "Turn On  "
        switch_auto.setOnCheckedChangeListener (this);
    }

    override fun onStop() {
        super.onStop()
        finish()
    }


}
