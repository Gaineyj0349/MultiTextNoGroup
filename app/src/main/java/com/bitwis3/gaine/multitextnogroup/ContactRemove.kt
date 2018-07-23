package com.bitwis3.gaine.multitextnogroup

import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_contact_remove.*
import spencerstudios.com.bungeelib.Bungee

class ContactRemove : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_remove)
        val db = Room.databaseBuilder(applicationContext,
                DBRoom::class.java, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val layoutManager =  LinearLayoutManager(this);
        rvcont.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(rvcont.getContext(),
                layoutManager.getOrientation())
        rvcont.addItemDecoration(dividerItemDecoration)

        val group = intent.getStringExtra("group")
        supportActionBar?.title = "Group: $group"
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.gradient4))
        val groupList = db.multiDOA().getAllInGroupList(group)
        val adapter = AdapterRemove(groupList, ContactRemove@this, db)
        rvcont.adapter = adapter


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
}
