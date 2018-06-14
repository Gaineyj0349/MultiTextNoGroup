package com.bitwis3.gaine.multitextnogroup;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

@Dao
public interface MultiDOA {

    @Query("SELECT * FROM _table_one_multi")
    List<Contact> getAllDataInList();

    @Query("SELECT * FROM _table_one_multi WHERE _type_entry NOT LIKE 'group'  AND _type_entry NOT LIKE 'timed_text_not_sent' ORDER BY _timeInMillis DESC")
    List<Contact> getAllRecords();

    @Query("SELECT * FROM _table_one_multi WHERE _type_entry NOT LIKE 'group' AND _timeInMillis > :timeInMillis ORDER BY _id DESC")
    List<Contact> allFutureRecords(long timeInMillis);

    @Query("SELECT * FROM _table_one_multi WHERE _type_entry LIKE 'timed_text_not_sent' AND _timeInMillis<> :timeInMillis ORDER BY _id DESC")
    List<Contact> getAllMissed(long timeInMillis);

    @Query("SELECT * FROM _table_one_multi WHERE _type_entry LIKE 'timed_text_not_sent'")
    List<Contact> allPending();

    @Query("SELECT * FROM _table_one_multi WHERE _type_entry LIKE 'timed_text_not_sent' AND  _timeInMillis = :millis")
    List<Contact> allPendingWithMillis(long millis);



    @Query("UPDATE _table_one_multi \n" +
            "SET _type_entry = :typeEntry " +
            "WHERE _id = :ID;")
    public void updateTypeEntryWithID(int ID, String typeEntry);


    @Query("UPDATE _table_one_multi \n" +
            "SET _message = :message " +
            "WHERE _id = :ID;")
    public void updateMessageWithID(int ID, String message);

    @Query("UPDATE _table_one_multi \n" +
            "SET _timeInMillis = :timeInMillis " +
            "WHERE _id = :ID;")
    public void updateDateWithID(int ID, long timeInMillis);

    @Insert
    public void insertAll(Contact... contacts);

    @Delete
    public void delete(Contact contact);

    @Query("SELECT * FROM _table_one_multi WHERE _group = :string AND _group IS NOT NULL")
    public Cursor getAllInGroup(String string);

    @Query("SELECT distinct _group FROM  _table_one_multi WHERE _group IS NOT NULL")
    public List<String> getDistinctGroups();

    @Query("DELETE FROM  _table_one_multi WHERE _id = :id")
    public void deleteWithId(int id);

    @Query("DELETE FROM  _table_one_multi WHERE _group = :group")
    public void deleteGroup(String group);

    @Query("SELECT _code1 FROM _table_one_multi WHERE _code1 NOT NULL ORDER BY _code1 DESC LIMIT 1")
    public int getLatestCode();

    @Query("DELETE FROM _table_one_multi WHERE _id = :id")
    public void deleteTransactionWithID(int id);


    @Query("SELECT * FROM _table_one_multi WHERE _type_entry LIKE 'timed_text_not_sent' AND _timeInMillis > :timeInMillis GROUP BY _timeInMillis ORDER BY _timeInMillis DESC")
    List<Contact> getAllPendingAfterBoot(long timeInMillis);

}
