package com.bitwis3.gaine.multitextnogroup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SQLiteToRoomConverter {

    public Context context;


    public SQLiteToRoomConverter(Context context){
        this.context = context;
    }

    public boolean dbExists(String dbName){

        boolean exists = false;

        //this creates the default path to the internally created SQLite database
        String DB_PATH = "/data/data/"
                + context.getApplicationContext().getPackageName()
                + "/databases/";

        //creates the entire path to the database thats used to check if it exists
       String ABSOLUTE_DB_PATH = DB_PATH + dbName;

       //calls the method to check if the database exists
       exists = checkdatabase(ABSOLUTE_DB_PATH);

       return exists;
    }

    public boolean checkdatabase(String ABS_DB_PATH) {
        boolean checkdb = false;
        try {
            File dbfile = new File(ABS_DB_PATH);
            checkdb = dbfile.exists();

        } catch (SQLiteException e) {
        }
        return checkdb;
    }



    public void transferData(String dbNameDataFrom, DBRoom dbRoomDataTo){

        //this will be unique to each application because it will be specific to the POJO
        List<Contact> contacts = new ArrayList<>();
        DBHelper helper = new DBHelper(context, dbNameDataFrom, null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor phones = db.rawQuery("SELECT * FROM _contacts", null);
        int i = 0;
        if(phones != null && phones.moveToFirst())
            Log.i("JOSHtest1", "phones size " + phones.getCount());
           do {
               ++i;
            String name = phones.getString(1);
               Log.i("JOSHtest1name"+ i, name);
            String phoneNumber = phones.getString(2);
               Log.i("JOSHtest1number"+ i, phoneNumber);
            String group = phones.getString(3);
               Log.i("JOSHtest1group" + i, group);
            Contact contact = new Contact(name, phoneNumber, group);
            contact.setTypeEntry("group");
            contacts.add(contact);

        } while (phones.moveToNext());


        phones.close();
        db.close();


        Contact[] contactsArr = new Contact[contacts.size()];
        contactsArr = contacts.toArray(contactsArr);

        dbRoomDataTo.multiDOA().insertAll(contactsArr);

    }

    public void deleteFile(String dbName){
        Log.i("JOSHdb", "delete start");
        //this creates the default path to the internally created SQLite database
        String DB_PATH = "/data/data/"
                + context.getApplicationContext().getPackageName()
                + "/databases/";

        //creates the entire path to the database thats used to check if it exists
        String ABSOLUTE_DB_PATH = DB_PATH + dbName;

        File fdelete = new File(ABSOLUTE_DB_PATH);
        if (fdelete.exists())
        { fdelete.delete();
        }

        Log.i("JOSHdb", "delete end");
    }

}
