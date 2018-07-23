package com.bitwis3.gaine.multitextnogroup;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by gaine on 11/25/2017.
 */
@Entity(tableName = "_table_one_multi")
public class Contact implements Serializable{

    public Contact(String name, String number, int type) {
        this.name = name;
        this.number = number;
        this.type = type;
    }

    public Contact(String name, String number, String group) {
        this.group = group;
        this.name = name;
        this.number = number;
    }

    public Contact(){

    }

    @ColumnInfo (name = "_group")
    public String group;

    @ColumnInfo (name = "_name")
    public String name;

    @ColumnInfo (name = "_number")
    public String number;

    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo (name = "_type")
    public int type;

    @ColumnInfo (name = "_type_entry")
    public String typeEntry;

    @ColumnInfo (name = "_timeInMillis")
    public long timeInMillis;

    @ColumnInfo (name = "_message")
    public String message;

    @ColumnInfo (name = "_code1")
    public int code1;

    @ColumnInfo (name = "_code2")
    public int code2;

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode1() {
        return code1;
    }

    public void setCode1(int code1) {
        this.code1 = code1;
    }

    public int getCode2() {
        return code2;
    }

    public void setCode2(int code2) {
        this.code2 = code2;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public int getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTypeEntry() {
        return typeEntry;
    }

    public void setTypeEntry(String typeEntry) {
        this.typeEntry = typeEntry;
    }


    public String getAllInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append("\n\n");
        builder.append("id " + getId());
        builder.append("\n\n");
        builder.append("name " +getName());
        builder.append("\n\n");
        builder.append("group " +getGroup());
        builder.append("\n\n");
        builder.append("message " +getMessage());
        builder.append("\n\n");
        builder.append("code1 " +getCode1());
        builder.append("\n\n");
        builder.append("timeinmillis " +getTimeInMillis());
        builder.append("\n\n");
        builder.append("type_entry " +getTypeEntry());
        builder.append("\n\n");
        builder.append("type " +getType());
        builder.append("\n\n");

        return builder.toString();
    }


    public boolean isSatisfied(){
        if(getName().length()>0 && group.length()>0 && getMessage().length() > 0){
            return true;
        }else{
            return false;
        }
    }
}
