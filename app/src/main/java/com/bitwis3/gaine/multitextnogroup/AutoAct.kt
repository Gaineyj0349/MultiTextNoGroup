package com.bitwis3.gaine.multitextnogroup

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_auto.*
import spencerstudios.com.bungeelib.Bungee
import spencerstudios.com.fab_toast.FabToast




class AutoAct : AppCompatActivity(), CompoundButton.OnCheckedChangeListener{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Auto Text & Wake-Up"
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.gradient1))

        val font = Typeface.createFromAsset(AutoAct@this.getAssets(), "Acme-Regular.ttf")
        autoET.setTypeface(font)
        switch_auto.setTypeface(font)
        switch_auto.setOnCheckedChangeListener(this)
        switch_wake.setOnCheckedChangeListener (BreakSwitcher(this));

        initSwitchStatus()


    }

    override fun onPause() {
        super.onPause()
        finish()
    }
    private fun initSwitchStatus() {
        val prefs = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE)
        val statusauto = prefs.getString("auto_reply_mode_on_off", null)
        val statuswake = prefs.getString("break_thru_mode_on_off", null)
        val message = prefs.getString("message", "")
        val code = prefs.getString("wakecode", "12345")

        Log.i("JOSHbreak", "statuswake: $statuswake and code: $code")
        if (statusauto != null) {
            when(statusauto){
                "on" ->{
                    toggleOn()
                    autoET.setText(message)}
                "off" -> {toggleOff()}
            }
        }
        if (statuswake != null) {
            when(statuswake){
                "on" ->{
                    toggleOnWake()
                    autoETwake.setText(code)}
                "off" -> {toggleOffWake()}
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


    inner class BreakSwitcher(context: Context) : CompoundButton.OnCheckedChangeListener {
        var context: Context = context
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

            val editor = context.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit()
            editor.putBoolean("showAd", true)
            editor.apply()

            var mode: String = ""
            if(isChecked){
                switch_wake.text = "Turn Off  "
                autoETwake.isEnabled = false
                mode = "on"
            }else{
                switch_wake.text = "Turn On  "
                autoETwake.isEnabled = true
                mode = "off"
            }

            if(autoETwake.text.toString().trim().length > 0){
                editor.putString("wakecode", autoETwake.getText().toString())
                editor.putString("break_thru_mode_on_off", mode)
                editor.apply()
            }else{
                FabToast.makeText(context, "Wake-Up code must can not be empty. It has been reset to 12345",
                        Toast.LENGTH_LONG, FabToast.WARNING, FabToast.POSITION_CENTER).show()
                editor.putString("wakecode", "12345")
                editor.putString("break_thru_mode_on_off", mode)
                editor.apply()
                autoETwake.setText("12345")
                autoOff2()
            }


            if(isChecked){
                autoOn2()
            }else{
                autoOff2()
            }
        }
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
    fun toggleOnWake(){
        Log.i("JOSHbreak", "helper1")
        autoETwake.isEnabled = false
        switch_wake.setOnCheckedChangeListener (null);
        switch_wake.setChecked(true);
        switch_wake.text = "Turn Off  "
        switch_wake.setOnCheckedChangeListener (BreakSwitcher(this));
    }
    fun toggleOffWake(){
        autoETwake.isEnabled = true
        switch_wake.setOnCheckedChangeListener (null);
        switch_wake.setChecked(false);
        switch_wake.text = "Turn On  "
        switch_wake.setOnCheckedChangeListener (BreakSwitcher(this));
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_emergency, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {

                Bungee.slideRight(this)
                this.finish()

                return true
            }
            R.id.emergencytoolbar-> {
                startActivity(Intent(this, Emergency::class.java))
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun autoOn2(){

        if(autoETwake.getText().toString().length > 0 ){
            val i = Intent(this, BreakThru::class.java)
            i.putExtra("break_thru", "break_thru")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(i)
            } else {
                startService(i)
            }
            FabToast.makeText(this, "Wake-Up mode is ON. A notification has been created for quick control.",
                    Toast.LENGTH_LONG, FabToast.SUCCESS, FabToast.POSITION_CENTER).show()

        }else{

//            FabToast.makeText(this, "Wake-Up code must can not be empty.", Toast.LENGTH_LONG, FabToast.WARNING, FabToast.POSITION_CENTER).show()

        }


    }
    fun autoOff2(){
        toggleOffWake()

        val editor = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit()
        editor.putString("wakecode", "12345")
//        editor.putString("break_thru", "off")
        editor.putString("break_thru", "off")
        editor.apply()
        val i = Intent(this, BreakThru::class.java)
        i.putExtra("turnOff", "turnOff")
        i.putExtra("break_thru", "break_thru")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i)
        } else {
            startService(i)
        }

        FabToast.makeText(this, "Wake-Up-Text Mode is off!", Toast.LENGTH_LONG, FabToast.INFORMATION, FabToast.POSITION_CENTER).show()
    }

}
