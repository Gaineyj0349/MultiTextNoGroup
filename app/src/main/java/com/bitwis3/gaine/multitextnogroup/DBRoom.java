package com.bitwis3.gaine.multitextnogroup;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 1)
public abstract class DBRoom extends RoomDatabase{
    public abstract MultiDOA multiDOA();
}
