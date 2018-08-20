package com.bitwis3.gaine.multitextnogroup

import android.arch.persistence.room.Room
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_log.*
import spencerstudios.com.bungeelib.Bungee




class ActivityLog : AppCompatActivity() {

    lateinit var db: DBRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.gradient8))
        val layoutManager =  LinearLayoutManager(this);
        rvlog.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(rvlog.getContext(),
                layoutManager.getOrientation())
        rvlog.addItemDecoration(dividerItemDecoration)
         db =  Room.databaseBuilder(applicationContext,
                DBRoom::class.java, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build()

        changeTabsFont()
//        initHistory();
        tabLayoutLog.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
               when (tab.position){
                   0 -> {initHistory()}
                   1 -> {initPending()}
                   2 -> {initMissed()}
               }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


        if(intent.hasExtra("missed")){
            val tab = tabLayoutLog.getTabAt(2)
            tab?.select()
        }
    }

    override fun onResume() {
        super.onResume()
        when (tabLayoutLog.selectedTabPosition){
            0 -> {initHistory()}
            1 -> {initPending()}
            2 -> {initMissed()}
        }

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_emergency, menu)
        return true
    }



    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(ActivityLog@this, Home::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
        Bungee.slideRight(this)
        this.finish()
    }

    fun changeTabsFont()
    {
        val font = Typeface.createFromAsset(ActivityLog@this.getAssets(), "Acme-Regular.ttf")
        val vg = tabLayoutLog.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = font
                }
            }
        }
    }



    fun initHistory(){
        Log.i("JOSHlog", "historyClicked")
       var allRecordsOfMessages = db.multiDOA().allRecords

        if(allRecordsOfMessages.size  == 0){
            supportActionBar?.title = "No History"
        }else{
            supportActionBar?.title = "History Log"
        }

        var myAdapter = LogAdapter(this, allRecordsOfMessages, db, false)
        rvlog.adapter = myAdapter
    }

    fun initPending(){
        Log.i("JOSHlog", "pendingclicked")
        var allRecordsOfMessages = db.multiDOA().allFutureRecords(System.currentTimeMillis())
        if(allRecordsOfMessages.size  == 0){
            supportActionBar?.title = "No Future Texts Set"
        }else{
            supportActionBar?.title = "Pending Log"
        }

        var myAdapter = LogAdapter(this, allRecordsOfMessages, db, true)


        rvlog.adapter = myAdapter
    }
    fun initMissed(){
        Log.i("JOSHlog", "missedlicked")
        var allRecordsOfMessages = db.multiDOA().allLogTabMissed
        if(allRecordsOfMessages.size  == 0){
            supportActionBar?.title = "No Unsent Texts"
        }else{
            supportActionBar?.title = "Unsent Log"
        }

        var myAdapter = LogAdapter(this, allRecordsOfMessages, db, true)


        rvlog.adapter = myAdapter
    }
}
