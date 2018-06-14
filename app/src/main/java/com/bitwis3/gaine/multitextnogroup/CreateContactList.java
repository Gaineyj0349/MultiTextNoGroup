package com.bitwis3.gaine.multitextnogroup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;
import spencerstudios.com.fab_toast.FabToast;

public class CreateContactList extends AppCompatActivity {

    ListView lv;
    ListView lv2;
    TabLayout tablayout;
    LinearLayout LL1;
//    DBHelper helper = null;
//    SQLiteDatabase db = null;
    ContentValues values;
    Cursor cursor;
    ArrayAdapter<String> adapterForSpinner;
    Spinner spinner;
    String inSpinnerNow;
    private int selectedTabint = 1;

    DBRoom db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setTitle("Group Management");
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient4));


        values = new ContentValues();
        MainActivity.returning = true;
//        helper = new DBHelper(this, "_contactDB", null, 1);
//        db = helper.getWritableDatabase();
        db =  Room.databaseBuilder(getApplicationContext(),
                DBRoom.class , "_database_multi_master")
                 .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        spinner = (Spinner) findViewById(R.id.spinnerincreate);

        lv = (ListView) findViewById(R.id.listviewincreate);
        lv2 = (ListView) findViewById(R.id.listviewincreate2);
        lv2.setVisibility(View.GONE);
        registerForContextMenu(lv2);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabincreate);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2increate);
        fab2.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view9) {

                if (CustomListAdapter.getArrayList().size() > 0) {

                    SharedPreferences.Editor prefs = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit();
                    prefs.putBoolean("showAd", true);
                    prefs.apply();

                    final Dialog dialog = new Dialog(CreateContactList.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_for_saving_group, null);

                    final EditText et = (EditText) mView.findViewById(R.id.etindialogcreategroup);
                    final Button SaN = (Button) mView.findViewById(R.id.saveandcreatenewButton);
                    final Button SaE = (Button) mView.findViewById(R.id.saveandexitButton);

                    SaN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String groupName = et.getText().toString().trim();
                            if (groupName.length() > 0 && !groupName.contains("'")) {
                                inputAllSelectedIntoDB(groupName);


                                dialog.dismiss();

                                Snackbar.make(view9, "Group " + groupName + " created!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                loadFolderSpinner();
                                fillListView();
                                CustomListAdapter.setArrayList();
                            } else {
                                FabToast.makeText(CreateContactList.this, "Group name can not be blank, or contain " +
                                        "an apostrophe!", Toast.LENGTH_LONG, FabToast.WARNING, FabToast.POSITION_DEFAULT).show();
                            }


                        }
                    });
                    SaE.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String groupName = et.getText().toString().trim();
                            if (groupName.length() > 0 && !groupName.contains("'")) {
                                inputAllSelectedIntoDB(groupName);


                                dialog.dismiss();

                                CreateContactList.this.finish();
                            } else {
                               FabToast.makeText(CreateContactList.this, "Group name can not be blank."
                                       , Toast.LENGTH_LONG, FabToast.WARNING, FabToast.POSITION_DEFAULT).show();
                            }


                        }
                    });


                    dialog.setContentView(mView);
                    dialog.show();

                } else {
                    FabToast.makeText(CreateContactList.this, "No Contacts have been selected!", Toast.LENGTH_LONG
                    , FabToast.WARNING, FabToast.POSITION_DEFAULT).show();
                }
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view9) {
                SharedPreferences.Editor prefs = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit();
                prefs.putBoolean("showAd", true);
                prefs.apply();

                inSpinnerNow = getCurrentInSpinner();
                if (inSpinnerNow.length()>0) {
                    final Dialog dialog99 = new Dialog(CreateContactList.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_for_create2, null);

                    final TextView tv = (TextView) mView.findViewById(R.id.tvinDialog);
                    final Button DeleteGroup = (Button) mView.findViewById(R.id.buttonindialogtodeleteentiregroup);
                    final Button AddtoGroup = (Button) mView.findViewById(R.id.buttonindialogtoaddpeopletogroup);
                    tv.setText("Group: " + getCurrentInSpinner());
                    DeleteGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:


                                            FabToast.makeText(getApplicationContext(), "Removed!",
                                                    Toast.LENGTH_SHORT, FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show();
                                            dialog99.dismiss();

                                            Snackbar.make(view9, "Group " + getCurrentInSpinner() + " Deleted!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            deleteEntireFolder(inSpinnerNow);
                                            inSpinnerNow = "";
                                            loadFolderSpinner();
                                            fillListView2();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            dialog.dismiss();
                                            break;
                                    }

                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateContactList.this);
                            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();


                        }


                    });
                    AddtoGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            dialog99.dismiss();
                            Intent intent = new Intent(CreateContactList.this, SelectContactsToAdd.class);
                            intent.putExtra("key", inSpinnerNow);
                            startActivity(intent);

                        }
                    });


                    dialog99.setContentView(mView);
                    dialog99.show();
                }else{
                    FabToast.makeText(CreateContactList.this, "Nothing to edit, you must create group first!", Toast.LENGTH_LONG
                    , FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show();
                }
            }
        });


        tablayout = (TabLayout) findViewById(R.id.tabLayoutincreate);
        tablayout.addTab(tablayout.newTab().setText("Create Group"));
        tablayout.addTab(tablayout.newTab().setText("Edit Group"));
        changeTabsFont();
        LL1 = (LinearLayout) findViewById(R.id.LL1increate);
        LL1.setVisibility(View.GONE);

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedTab = (String) tab.getText();

                switch (selectedTab) {
                    case "Create Group":
                        selectedTabint = 1;
                        CustomListAdapter.setArrayList();
                        //  Toast.makeText(MainActivity.this, "cont", Toast.LENGTH_LONG).show();
                        LL1.setVisibility(View.GONE);
                        lv.setVisibility(View.VISIBLE);
                        lv2.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
                        fab2.setVisibility(View.GONE);
                        fillListView();

                        break;
                    case "Edit Group":
                        CustomListAdapter.setArrayList();
                        selectedTabint = 2;
                        loadFolderSpinner();
                        //  Toast.makeText(MainActivity.this, "group", Toast.LENGTH_LONG).show();
                        LL1.setVisibility(View.VISIBLE);
                        CustomListAdapter.setArrayList();
                        lv.setAdapter(null);
                        lv.setVisibility(View.GONE);
                        lv2.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.GONE);
                        fab2.setVisibility(View.VISIBLE);
                        fillListView2();
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        loadFolderSpinner();
        fillListView();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillListView2();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadFolderSpinner();

    }
    public void fillListView(){
        CustomListAdapter adapter = new CustomListAdapter(CreateContactList.this,
                MainActivity.getContactsList(CreateContactList.this) );
        lv.setAdapter(adapter);
    }
    public void fillListView2(){
        lv2.setAdapter(null);
        if(getCurrentInSpinner().length()>0){
        Cursor c = db.multiDOA().getAllInGroup(getCurrentInSpinner());
        MyCursorAdapter adapter2 = new MyCursorAdapter(CreateContactList.this, c );
        lv2.setAdapter(adapter2);
       }
    }

    public void inputAllSelectedIntoDB(String groupName){

        ArrayList<Integer> positions = CustomListAdapter.getArrayList();

        int size = positions.size();

        for(int i = 0; i < size; i++){
            Contact c = (Contact) lv.getItemAtPosition(positions.get(i));
            c.setGroup(groupName);
            c.setTypeEntry("group");
//            String name = c.getName();
//            String number = c.getNumber();
//            values.put("_name", name);
//            values.put("_number", number);
//            values.put("_group", groupName);
//            Log.i("JOSH", "HERE " + name + " " + number + " "+ groupName);
            db.multiDOA().insertAll(c);

            values.clear();

        }
}

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs

        Bungee.slideRight(this);
        this.finish();
    }

    public void loadFolderSpinner() {


            List<String> list = db.multiDOA().getDistinctGroups();
            Log.i("JOSHnew", "size: " + list.size());
            if(list.size() > 0){
                adapterForSpinner = new ArrayAdapter<String>(CreateContactList.this,
                        R.layout.spinnerz, list);

                adapterForSpinner.setDropDownViewResource(R.layout.spinnerzdrop);

                spinner.setAdapter(adapterForSpinner);
            }else{
                spinner.setAdapter(null);
            }

        }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();



        inflater.inflate(R.menu.context,
                menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Cursor c = (Cursor) lv2.getItemAtPosition(info.position);
        final Integer itemEntryID = c.getInt(0);


        switch (item.getItemId()) {

            case R.id.remove:

                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:


                                FabToast.makeText(getApplicationContext(), "Removed!",
                                        Toast.LENGTH_SHORT, FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show();
                                db.multiDOA().deleteWithId(itemEntryID);
                                loadFolderSpinner();
                                dialog.dismiss();
                                fillListView2();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }

                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateContactList.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


                return true;


        }
        return super.onContextItemSelected(item);
    }
    public String getCurrentInSpinner(){
        String now = "";
        if(spinner.getAdapter() != null){
            now =  spinner.getSelectedItem().toString();}
        if (now.length()>0){
            return now;
        }else {
            return "";
        }
    }
public void deleteEntireFolder(String inSpinnerNow2){
       db.multiDOA().deleteGroup(inSpinnerNow2);
}

    @Override
    protected void onResume() {
        super.onResume();
        CustomListAdapter.setArrayList();
        switch (selectedTabint){
            case 1:

                loadFolderSpinner();
                fillListView();
                break;
            case 2:
                loadFolderSpinner();
                fillListView2();
                break;
        }
    }
    private void changeTabsFont() {
        Typeface font = Typeface.createFromAsset(CreateContactList.this.getAssets(), "Acme-Regular.ttf");
        ViewGroup vg = (ViewGroup) tablayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(font);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Bungee.slideRight(this);
                this.finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
