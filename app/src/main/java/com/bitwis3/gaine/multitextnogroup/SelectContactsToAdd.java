package com.bitwis3.gaine.multitextnogroup;

import android.arch.persistence.room.Room;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectContactsToAdd extends AppCompatActivity {
ListView lv;
//SQLiteDatabase db;
//DBHelper helper;
ContentValues values;
DBRoom db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts_to_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient4));
        lv = (ListView)findViewById(R.id.lvforedit);
        values = new ContentValues();
        final String group = getIntent().getStringExtra("key");

        db = Room.databaseBuilder(getApplicationContext(),
                DBRoom.class, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

//        helper = new DBHelper(this, "_contactDB", null, 1);
//        db = helper.getWritableDatabase();

        fillListView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputAllSelectedIntoDB(group);
                Snackbar.make(view, "Done!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                SelectContactsToAdd.this.finish();
            }
        });
    }

    public void fillListView(){
        CustomListAdapter adapter = new CustomListAdapter(SelectContactsToAdd.this,
                MainActivity.getContactsList(SelectContactsToAdd.this) );
        lv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs


        SelectContactsToAdd.this.finish();
    }

    public void inputAllSelectedIntoDB(String groupName){

        ArrayList<Integer> positions = CustomListAdapter.getArrayList();

        int size = positions.size();

        for(int i = 0; i < size; i++){
            Contact c = (Contact) lv.getItemAtPosition(positions.get(i));
//            String name = c.getName();
//            String number = c.getNumber();
//            values.put("_name", name);
//            values.put("_number", number);
//            values.put("_group", groupName);
            c.setGroup(groupName);
            c.setTypeEntry("group");
//            Log.i("JOSH", "HERE " + name + " " + number + " "+ groupName);
            db.multiDOA().insertAll(c);

            values.clear();

        }
    }
}
