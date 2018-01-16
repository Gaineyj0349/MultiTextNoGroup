package com.bitwis3.gaine.multitextnogroup;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by gaine on 11/26/2017.
 */

public class MyCursorAdapter extends CursorAdapter {
    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.listview_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv1Name = (TextView)
                view.findViewById(R.id.lv_tv1);
        TextView tv2Number = (TextView)
                view.findViewById(R.id.lv_tv2);
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.lv_checkbox);

        checkBox.setVisibility(View.GONE);

        tv1Name.setText(cursor.getString(1));
        tv2Number.setText(cursor.getString(2));

    }
}
