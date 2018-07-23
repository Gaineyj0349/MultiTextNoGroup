package com.bitwis3.gaine.multitextnogroup

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.persistence.room.Room
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.telephony.SmsManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_emergency.*
import spencerstudios.com.bungeelib.Bungee
import spencerstudios.com.fab_toast.FabToast


class Emergency : AppCompatActivity() {
    var sourceLatitude = ""
    var sourceLongitude = ""
    lateinit var et: EditText

    lateinit var helper: AllPermissionsHelper
    lateinit var list: List<Contact>
    lateinit var db: DBRoom
    var oneReady = false;
    var twoReady = false;
    var threeReady = false;
    var signal = true;
    lateinit var smsManager: SmsManager
    var change = false
    var needToShow = false
    var iNum = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency)

        db = Room.databaseBuilder(applicationContext,DBRoom::class.java, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build()

        if(rapidOn()){
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

        }else{
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        change = rapidOn()
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.gradient8))
        supportActionBar?.title = "PRESET TEXTING"
        smsManager = SmsManager.getDefault()



        initLabels()



        db = Room.databaseBuilder(applicationContext,DBRoom::class.java, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build()

        respondll1.setOnLongClickListener(View.OnLongClickListener {
            send1()
            return@OnLongClickListener true
        })
        respondll2.setOnLongClickListener(View.OnLongClickListener {
            send2()
            return@OnLongClickListener true
        })
        respondll3.setOnLongClickListener(View.OnLongClickListener {
            send3()
            Log.i("JOSHtag", "e")

            return@OnLongClickListener true
        })
        respondll1.setOnClickListener { showToast() }
        respondll2.setOnClickListener { showToast() }
        respondll3.setOnClickListener { showToast() }

        llemergencysetupcard1.setOnLongClickListener(object: View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                var i = Intent(this@Emergency, EditEmergency::class.java)
                i.putExtra("which", 1)
                startActivity(i)
                return true
            }

        })
        llemergencysetupcard2.setOnLongClickListener(object: View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                var i = Intent(this@Emergency, EditEmergency::class.java)
                i.putExtra("which", 2)
                startActivity(i)
                return true
            }

        })
        llemergencysetupcard3.setOnLongClickListener(object: View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                var i = Intent(this@Emergency, EditEmergency::class.java)
                i.putExtra("which", 3)
                startActivity(i)
                return true
            }

        })


    }



    private fun initLabels() {

        //TODO
        emergencytvtitle1.append(" REQUIRES SETUP")
        emergencytvtitle2.append(" REQUIRES SETUP")
        emergencytvtitle3.append(" REQUIRES SETUP")
    }

    public fun reload(v: View){
        recreate()
    }
    private fun needPermissions(permission: Array<String?>, requestCode: Int?): Boolean {
        var show = true
        for (i in permission.indices) {
            if (ContextCompat.checkSelfPermission(this@Emergency, permission[i]!!) != PackageManager.PERMISSION_GRANTED) {
                show = false
            }

        }
        return show
    }


    override fun onResume() {
        super.onResume()
        Log.i("JOSHnew", "onResume called")
        val p = arrayOfNulls<String>(1)
        p[0] = Manifest.permission.ACCESS_FINE_LOCATION
        if (!needPermissions(p, 88)) {
            Log.i("JOSHdo", ""+true)
            db.multiDOA().unlocate()
        }else{
            Log.i("JOSHdo", ""+false)
        }
        try {
            if (rapidOn()) {
                mainLLmode.visibility = View.GONE
                respondLLmode.visibility = View.VISIBLE

            } else {
                mainLLmode.visibility = View.VISIBLE
                respondLLmode.visibility = View.GONE
            }
            Log.i("JOSHtester", "onresume")

            list = db.multiDOA().getEmergencyContacts()
            Log.i("JOSHemerg", "List size: ${list.size}")
            updateUI()

            if (!change && rapidOn()) {
                recreate()
            }
        }catch (e: Exception){
            finish()
        }
    }

    private fun groupExists(v: String): Boolean{
        var checkList = db.multiDOA().distinctGroupsForEmergency
        if(checkList.contains(v)) return true else return false
    }

    @SuppressLint("MissingPermission")
    private fun updateUI() {
        if(rapidOn()){
            respondll1.visibility = View.GONE
            respondll2.visibility = View.GONE
            respondll3.visibility = View.GONE




        }else {



            oneReady = false
            emergencytvtitle1.setBackgroundColor(resources.getColor(R.color.red))
            emergencytvtitle1.text = "Preset Text 1: (REQUIRES SETUP!)"
            twoReady = false
            emergencytvtitle2.setBackgroundColor(resources.getColor(R.color.red))
            emergencytvtitle2.text = "Preset Text 2: (REQUIRES SETUP!)"
            threeReady = false
            emergencytvtitle3.setBackgroundColor(resources.getColor(R.color.red))
            emergencytvtitle3.text = "Preset Text 3: (REQUIRES SETUP!)"
        }
        for(c in list){
            when(c.type){
                1->{if(c.isSatisfied && groupExists(c.group)){
                    if(Seed.isTelephonyMobileConnected(this)){
                        oneReady = true
                        emergencytvtitle1.setBackgroundColor(resources.getColor(R.color.green))
                        emergencytvtitle1.text = c.name + " (READY)!"

                    }else{
                        oneReady = false
                        signal = false
                        emergencytvtitle1.setBackgroundColor(resources.getColor(R.color.orange))
                        emergencytvtitle1.text = c.name + " (NO SIGNAL!)"
                    }
                    if(rapidOn()){
                        respondiv1.setImageLevel(c.code2)
                        respondtv1.text = c.name
                        respondll1.visibility = View.VISIBLE
                    }

                }else{
                    oneReady = false
                    emergencytvtitle1.setBackgroundColor(resources.getColor(R.color.red))
                    emergencytvtitle1.text = "(REQUIRES SETUP!)"
                }}
                2->{if(c.isSatisfied){
                    if(Seed.isTelephonyMobileConnected(this)){
                        twoReady = true
                        emergencytvtitle2.setBackgroundColor(resources.getColor(R.color.green))
                        emergencytvtitle2.text = c.name + " (READY)!"

                    }else{
                        twoReady = false
                        signal = false
                        emergencytvtitle2.setBackgroundColor(resources.getColor(R.color.orange))
                        emergencytvtitle2.text = c.name + " (NO SIGNAL!)"
                    }
                    if(rapidOn()){

                        respondiv2.setImageLevel(c.code2)
                        respondtv2.text = c.name
                        respondll2.visibility = View.VISIBLE
                    }

                }else{
                    twoReady = false
                    emergencytvtitle2.setBackgroundColor(resources.getColor(R.color.red))
                    emergencytvtitle2.text = "(REQUIRES SETUP!)"
                }}
                3->{if(c.isSatisfied){
                    if(Seed.isTelephonyMobileConnected(this)){
                        threeReady = true
                        emergencytvtitle3.setBackgroundColor(resources.getColor(R.color.green))
                        emergencytvtitle3.text = c.name + " (READY)!"

                    }else{
                        threeReady = false
                        signal = false
                        emergencytvtitle3.setBackgroundColor(resources.getColor(R.color.orange))
                        emergencytvtitle3.text = c.name + " (NO SIGNAL!)"
                    }
                    if(rapidOn()){
                        respondiv3.setImageLevel(c.code2)
                        respondtv3.text = c.name
                        respondll3.visibility = View.VISIBLE
                    }

                }else{
                    threeReady = false
                    emergencytvtitle3.setBackgroundColor(resources.getColor(R.color.red))
                    emergencytvtitle3.text = "(REQUIRES SETUP!)"
                }}
            }
        }
    }

    override fun onPause() {
        super.onPause()

        Log.i("JOSHnew", "onPause called")



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        helper.handleResult(requestCode, permissions, grantResults)
    }






    var reset = true
    val handler = Handler()
    var i = 0
    var j = 0


    fun SENDNOW(v: View){

        var num = v.id
        handler.postDelayed({
            reset = false
            i = 0
            j = 0
        }, 500)
        ++i
        ++j
        if (i > 1 && reset) {
            Log.i("JOSHemergency", "helper1")
            when(num){
                R.id.llemergencysendcard1 -> {send1()}
                R.id.llemergencysendcard2 -> {send2()}
                R.id.llemergencysendcard3 -> {send3()}
            }
        }
        if (j > 1 && !reset) {
            FabToast.makeText(this@Emergency, "TAP TWICE QUICKLY TO SEND!!", FabToast.LENGTH_LONG,
                    FabToast.WARNING, FabToast.POSITION_TOP).show()
        }
        reset = true
    }

    fun send1(){
        if(oneReady){
            var c = db.multiDOA().getEmergencyContact(1)
            var l1 = db.multiDOA().getAllInGroupList(c.group)
            sendMessagesWith(c.message,c.code1,l1, c)

        }else{
            if(!signal){
                if(rapidOn()){
                    FabToast.makeText(this@Emergency, "NO SIGNAL PLEASE USE THE LAST KNOWN LOCATION AT THE BOTTOM!", FabToast.LENGTH_LONG,
                            FabToast.INFORMATION, FabToast.POSITION_TOP).show()
                }else{
                    FabToast.makeText(this@Emergency, "NO SIGNAL - CLICK RELOAD!", FabToast.LENGTH_LONG,
                            FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
                }

            }else{
                FabToast.makeText(this@Emergency, "Profile needs setup", FabToast.LENGTH_LONG,
                        FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
            }

        }

    }


    private fun sendMessagesWith(message: String?, code1: Int, listIn: List<Contact>, defaultInfo: Contact) {

        var b = StringBuilder()
        try {

            if(code1 == 1) {
                //TODO start service
                val gson = Gson()
                val typeOfSource = object : TypeToken<List<Contact>>() {
                }.type
                val jsonContacts = gson.toJson(listIn, typeOfSource)
                val i = Intent(this@Emergency, MyServiceToSendLocation::class.java)
                i.putExtra("message", message)
                i.putExtra("code1", code1)
                i.putExtra("listIn", jsonContacts)
                i.putExtra("defaultInfo", defaultInfo)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(i)
                } else {
                    startService(i)
                }
                b.append("A text will follow shortly with location\n\n")
            }
        }catch(e: Exception){

        }
        b.append(message)

        val stringToSend = b.toString()
        if(Seed.isTelephonyMobileConnected(this@Emergency)){

            sendMessage(stringToSend,0,listIn,defaultInfo )
            FabToast.makeText(this@Emergency, "Success!", FabToast.LENGTH_LONG,
                    FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show()
            getNotification()
        }else{
            FabToast.makeText(this@Emergency, "(NO SIGNAL!)", FabToast.LENGTH_LONG,
                    FabToast.ERROR, FabToast.POSITION_DEFAULT).show()
        }
    }


    fun send2(){
        if(twoReady){
            var c = db.multiDOA().getEmergencyContact(2)
            var l1 = db.multiDOA().getAllInGroupList(c.group)
            sendMessagesWith(c.message,c.code1,l1, c)
        }else{
            if(!signal){
                FabToast.makeText(this@Emergency, "NO SIGNAL - CLICK RELOAD!", FabToast.LENGTH_LONG,
                        FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
            }else{
                FabToast.makeText(this@Emergency, "Profile needs setup", FabToast.LENGTH_LONG,
                        FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
            }

        }
    }
    fun send3(){
        if(threeReady){
            var c = db.multiDOA().getEmergencyContact(3)
            var l1 = db.multiDOA().getAllInGroupList(c.group)
            sendMessagesWith(c.message,c.code1,l1, c)

        }else{
            if(!signal){
                FabToast.makeText(this@Emergency, "NO SIGNAL - CLICK RELOAD!", FabToast.LENGTH_LONG,
                        FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
            }else{
                FabToast.makeText(this@Emergency, "Profile needs setup", FabToast.LENGTH_LONG,
                        FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
            }

        }

    }

    fun permlatlong(v: View){
        helper.requestPermissions()
    }

    private fun sendMessage(message: String, i: Int, listIn: List<Contact>, defaultInfo: Contact) {
        var con = Contact()
        con.timeInMillis = System.currentTimeMillis()
        con.name = listIn.get(i).name
        con.number = listIn.get(i).number
        con.message = message

        try {
            handler.postDelayed(Runnable {
                if(listIn[i].getNumber() != null) {
                    if (message.length > 155) {
                        val parts = smsManager.divideMessage(message)
                        smsManager.sendMultipartTextMessage(listIn[i].getNumber(), null,
                                parts, null, null)
                    } else {
                        smsManager.sendTextMessage(listIn[i].getNumber(), null,
                                message, null, null)
                    }

                    if (i < listIn.size - 1) {
                        sendMessage(message, i + 1, listIn, defaultInfo)
                    } else {
                        return@Runnable
                    }
                }

        }, 100)
            con.typeEntry = "EMERGENCY_TEXT_SENT"
            db.multiDOA().insertAll(con)
        }catch (e: Exception){
            con.typeEntry = "EMERGENCY_TEXT_EXCEPTION"
            db.multiDOA().insertAll(con)
        }
    }


    private fun getNotification() {
        val contentView = RemoteViews("com.bitwis3.gaine.multitextnogroup", R.layout.custom_notification)
        contentView.setImageViewResource(R.id.image, R.mipmap.icon)
        contentView.setTextViewText(R.id.notificationtext, "TEXTS SENT!")

        val builder = Notification.Builder(this)

        builder.setSmallIcon(R.drawable.ic_message_black_24dp)
        val intent2 = Intent(this, ActivityLog::class.java)

        val latestCode = db.multiDOA().latestCode
        Log.i("JOSHser", latestCode.toString())
        val pendingIntent: PendingIntent
        if (latestCode >= 0) {
            pendingIntent = PendingIntent.getActivity(this, db.multiDOA().latestCode + 1, intent2, 0)
        } else {
            pendingIntent = PendingIntent.getActivity(this, 1, intent2, 0)
        }
        intent2.putExtra("code", latestCode)

        builder.setContentIntent(pendingIntent)
        builder.setContent(contentView)
        builder.setContentTitle("TEXTS SENT!")
        builder.setAutoCancel(true)
        builder.setPriority(Notification.PRIORITY_HIGH)
        builder.setDefaults(Notification.DEFAULT_VIBRATE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "default use"
            val description = "get reminders from this app"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel("ChannelID", name, importance)
            mChannel.description = description
            mChannel.setSound(null, null)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = this.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
            builder.setChannelId("ChannelID")
        }

        val notification = builder.build()


        val notificationMgr = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        notificationMgr.notify(latestCode, notification)
    }

    public fun continueButtonClick(v: View){
        startActivity(Intent(this, splash::class.java))
        Bungee.fade(this)
        finish()
    }
    fun rapidOn(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return sharedPreferences.getBoolean("rapid", false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main2, menu)
        if(rapidOn()){
            menu.getItem(0).setVisible(false)
            menu.getItem(1).setVisible(false)
        }else{
            menu.getItem(2).setVisible(false)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings2) {
            startActivity(Intent(this, SettingsActivity::class.java))
            Bungee.card(this)
            return true
        }
        if (id == R.id.information) {
            //TODO



            watchYoutubeVideo("gFKKzgCfdmc")
            return true
        }
        if (id == R.id.continueApp) {
            val i = Intent(this, Home::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            Bungee.fade(this)
            finish()
            return true
        }



        return super.onOptionsItemSelected(item)
    }

    fun watchYoutubeVideo(id: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
        val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=$id"))
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(webIntent)
        }

    }
    fun showToast(){
        FabToast.makeText(this, "HOLD to send!!", FabToast.LENGTH_LONG, FabToast.INFORMATION, FabToast.POSITION_TOP).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("JOSHnew", "ondestroy")
    }
}
