package com.bitwis3.gaine.multitextnogroup;

/**
 * Created by gaine on 11/25/2017.
 */

public class Contact {
    public String name = "";
    public String number = "";
    public String id = "";


    public Contact(String name, String phoneNumber){
        this.name = name;
        this.number = phoneNumber;
    }




    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }






}
