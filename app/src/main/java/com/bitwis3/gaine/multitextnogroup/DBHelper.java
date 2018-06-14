package com.bitwis3.gaine.multitextnogroup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
  Context context;

  public DBHelper(Context context, String dbName,
                  SQLiteDatabase.CursorFactory factory, int version) {
    super(context, dbName, factory, version);
    this.context = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String createString =
        "CREATE TABLE IF NOT EXISTS _contacts "
            + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "_name STRING, "
            + "_number STRING," +
                "_group STRING);";

    db.execSQL(createString);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db,
      int oldVersion, int newVersion) {
    String dropString =
        "DROP TABLE IF EXISTS _contacts;";
    db.execSQL(dropString);
    onCreate(db);
  }

}
