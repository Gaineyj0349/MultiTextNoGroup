package com.bitwis3.gaine.multitextnogroup;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by gaine on 11/25/2017.
 */

public class CustomListAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Contact> items; //data source of the list adapter
    public static ArrayList<String> stringAL = new ArrayList<>();
    public static ArrayList<Integer> positionAL = new ArrayList<>();
    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>(); // array list for store state of each checkbox;

    //public constructor
    public CustomListAdapter(Context context, ArrayList<Contact> items) {
        this.context = context;
        this.items = items;
        for (int i = 0; i < items.size(); i++) { // c.getCount() return total number of your Cursor
            itemChecked.add(i, false); // initializes all items value with false
        }
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int position2 = position;
        // inflate the layout for each list row

        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.listview_item, parent, false);
        }

        // get current item to be displayed
        Contact currentContact = (Contact) getItem(position);

        // get the TextView for item name and item description
        TextView tv1Name = (TextView)
                convertView.findViewById(R.id.lv_tv1);
        TextView tv2Number = (TextView)
                convertView.findViewById(R.id.lv_tv2);
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.lv_checkbox);

        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (itemChecked.get(position2) == true) { // if current checkbox is checked, when you click -> change it to false
                    itemChecked.set(position2, false);
                    positionAL.removeAll(Arrays.asList(position2));
                    Log.i("JOSH CursorAdapter","position: "+position2+" - checkbox state: "+itemChecked.get(position2));
                    Log.i("JOSH CursorAdapter","SIZE: " + getArrayList().size());
                } else {
                    itemChecked.set(position2, true);
                    positionAL.add(position2);
                    Log.i("JOSH CursorAdapter","position: "+position2+" - checkbox state: "+itemChecked.get(position2));
                    Log.i("JOSH CursorAdapter","SIZE: " + getArrayList().size());
                }
            }
        });


        checkBox.setChecked(itemChecked.get(position)); // set the checkbox state base on arraylist object state
        Log.i("JOSH CursorAdapter","position: "+position+" - checkbox state: "+itemChecked.get(position));
        StringBuilder sb = new StringBuilder();
        sb.append(currentContact.getNumber());
        sb.append("-");
        switch (currentContact.getType()){
            case 0: sb.append("");
                break;
            case 1: sb.append("home");
                break;
            case 2: sb.append(" mobile");
                break;
            case 3: sb.append("work");
                break;
            case 17: sb.append("work mobile");
                break;
            default: break;

        }
        Typeface font = Typeface.createFromAsset(context.getAssets(), "Acme-Regular.ttf");
        //sets the text for item name and item description from the current item object
        tv1Name.setTypeface(font);
        tv2Number.setTypeface(font);
//        tv1Name.setTextColor(Color.WHITE);
//        tv2Number.setTextColor(Color.WHITE);
        tv1Name.setText(currentContact.getName());
        tv2Number.setText(sb.toString());

        // returns the view for the current row
        return convertView;
    }

    public static ArrayList getArrayList(){

        return positionAL;
    }
    public static void setArrayList(){

        positionAL = new ArrayList<>();
    }

}
