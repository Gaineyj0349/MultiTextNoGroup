package com.bitwis3.gaine.multitextnogroup;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> namesToSend;
    ArrayAdapter<String> adapterForSpinner;
    Cursor cursor;
    static final Integer SMS = 0x5;
    static final Integer ACCOUNTS = 0x6;
    ArrayList<String> numbersToSend;
    DBHelper helper = null;
    FloatingActionButton fab;
    SQLiteDatabase db = null;
    private Uri uriContact;
    private String contactID;
    public static final int  MAX_PICK_CONTACT= 10;
    ListView lv;
    Spinner spinner;
    TabLayout tablayout;
    LinearLayout LL1;
    private int selectedTabint = 1;
String answer = "";
    public static InterstitialAd interstitialAd;
    private int resumeCount = 0;
    private int havePermission = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      //  askForPermission(Manifest.permission.READ_CONTACTS,ACCOUNTS);
        SharedPreferences prefs = getSharedPreferences("TIP", MODE_PRIVATE);
        answer = prefs.getString("tip", "yes");
        SharedPreferences prefs2 = getSharedPreferences("PERMISSION", MODE_PRIVATE);
        havePermission = prefs2.getInt("permission", 0);
        helper = new DBHelper(this, "_contactDB", null, 1);
        db = helper.getWritableDatabase();
        spinner = (Spinner) findViewById(R.id.spinnerinMain);
        lv = (ListView) findViewById(R.id.listview);
        tablayout = (TabLayout) findViewById(R.id.tabLayout);
        tablayout.addTab(tablayout.newTab().setText("Select Recipients"));
        tablayout.addTab(tablayout.newTab().setText("Select Group"));

        LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL1.setVisibility(View.GONE);
        MobileAds.initialize(this, "ca-app-pub-6280186717837639~2020381737");
        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId("ca-app-pub-6280186717837639/5716084168");

        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed(){

                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });




        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedTab = (String) tab.getText();

                switch (selectedTab) {
                    case "Select Recipients":
                        CustomListAdapter.setArrayList();
                        selectedTabint = 1;
                      //  Toast.makeText(MainActivity.this, "cont", Toast.LENGTH_LONG).show();
                        LL1.setVisibility(View.GONE);
                        fillListView();

                        break;
                    case "Select Group":

                        if(answer.equals("yes")){
                            final Dialog dialog = new Dialog(MainActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.dialogfirstrun, null);
                            Button button = (Button)mView.findViewById(R.id.buttondialog);
                            final CheckBox cb = (CheckBox)mView.findViewById(R.id.checkbox);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (cb.isChecked()) {
                                        SharedPreferences.Editor editor3 = getSharedPreferences("TIP", MODE_PRIVATE).edit();
                                        editor3.putString("tip", "no");
                                        editor3.apply();
                                        answer = "no";
                                    }
                                    dialog.dismiss();
                                }
                            });
                            dialog.setContentView(mView);
                            dialog.show();}

                        selectedTabint = 2;
                        loadFolderSpinner();
                      //  Toast.makeText(MainActivity.this, "group", Toast.LENGTH_LONG).show();
                        LL1.setVisibility(View.VISIBLE);
                        CustomListAdapter.setArrayList();
                        fillListViewFromGroup();
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

        numbersToSend = new ArrayList<String>();
        // initList();

         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    buildArrayListFromPositions();
                switch (selectedTabint) {

                    case 1:
                    if(numbersToSend.size()>0){
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    intent.putExtra("array", numbersToSend);
                        intent.putExtra("names", namesToSend);
                    startActivity(intent);}
                    else
                    {Toast.makeText(MainActivity.this, "No Recipients selected, check recipients or choose group!", Toast.LENGTH_LONG).show();
                    }
                    break;

                    case 2:
                       String inSpinnerNow = getCurrentInSpinner();
                        if (inSpinnerNow.length()>0) {
                        numbersToSend = buildArrayListFromSpinner();
                        Intent intent2 = new Intent(MainActivity.this, Main2Activity.class);
                        intent2.putExtra("array", numbersToSend);
                            intent2.putExtra("names", namesToSend);
                        startActivity(intent2);}else{
                            Toast.makeText(MainActivity.this, "No groups selected, you must create group first!", Toast.LENGTH_LONG).show();

                        }
            break;
                }}
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillListViewFromGroup();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }});
        loadFolderSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.other_apps) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=GainWise")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=GainWise")));
            }
            return true;
        }
        if (id == R.id.manage_groups) {
            Intent intent = new Intent(MainActivity.this, CreateContactList.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            switch (requestCode) {
            case 5:
                // SMS
              //  sendTheMessage(numbersToSend);

                buildArrayListFromPositions();
                break;
            //Accounts
            case 6:

        fillListView();
        }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {


                //Accounts
                case 6:

            fillListView();
            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor3 = getSharedPreferences("PERMISSION", MODE_PRIVATE).edit();
            editor3.putInt("permission", 1);
            editor3.apply();
            havePermission = 1;
        }else{
            Toast.makeText(this, "Permission denied, reset permissions for this app in your System -> App settings", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Permission denied, reset permissions for this app in your System -> App settings", Toast.LENGTH_SHORT).show();

            MainActivity.this.finish();
        }
    }


/*
    private static View.OnClickListener ask = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

            case R.id.sendSMS:
                askForPermission(Manifest.permission.SEND_SMS,SMS);
                break;
            case R.id.pickCONTACTS:
                askForPermission(Manifest.permission.READ_CONTACTS,ACCOUNTS);
                break;
           default:
                break;
        }
    }

    };

*/


    public void fillListView(){
        lv.setAdapter(null);
        CustomListAdapter.setArrayList();
        CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, getContactsList(MainActivity.this) );
        lv.setAdapter(adapter);
    }
    public static ArrayList<Contact> getContactsList(Context context) {
        ArrayList<Contact> contacts=new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new Contact(name,phoneNumber));
        }
        phones.close();
        Collections.sort(contacts, new Comparator<Contact>() {
            public int compare(Contact v1, Contact v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });
        return contacts;
    }

    public void buildArrayListFromPositions(){
        namesToSend = new ArrayList<>();
        numbersToSend = new ArrayList<>();
        ArrayList<Integer> positions = CustomListAdapter.getArrayList();

        int size = positions.size();

        for(int i = 0; i < size; i++){
            Contact c = (Contact) lv.getItemAtPosition(positions.get(i));
            numbersToSend.add(c.getNumber());
            namesToSend.add(c.getName());
        }
/*
        StringBuilder test = new StringBuilder();
        for(int i = 0; i< size; i++){
            test.append(numbersToSend.get(i) + " ");
        }
        Log.i("JOSH", test.toString());
*/
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


public void fillListViewFromGroup(){

        lv.setAdapter(null);
        if (getCurrentInSpinner().length()>0){
        Cursor c = db.rawQuery("Select * FROM _contacts WHERE _group = '"+ getCurrentInSpinner() + "'", null);
        MyCursorAdapter myCursorAdapter = new MyCursorAdapter(MainActivity.this, c);
        lv.setAdapter(myCursorAdapter);}

}



    @Override
    protected void onResume() {
        super.onResume();
        resumeCount++;

        CustomListAdapter.setArrayList();
        if(havePermission>0){
Log.d("JOSH", "RESUMECOUNT: "+ resumeCount);

            loadFolderSpinner();
            Log.d("JOSH", "RESUMECOUNT: "+ resumeCount);
            if (resumeCount>1){
                loadAd();
                Log.d("JOSH", "ad called");
            }

            switch (selectedTabint){
                case 1:
                    fillListView();
                    break;
                case 2:
                    fillListViewFromGroup();
                    break;

            }
        }else{
            askForPermission(Manifest.permission.READ_CONTACTS,ACCOUNTS);
        }



    }

    public void loadFolderSpinner() {
        if (getStringArrayListForFolders() != null) {
            if (getStringArrayListForFolders().size() > 0) {

                adapterForSpinner = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.spinnerz, getStringArrayListForFolders());

                adapterForSpinner.setDropDownViewResource(R.layout.spinnerzdrop);
                spinner.setAdapter(adapterForSpinner);
            }
        }else {
            spinner.setAdapter(null);


        }

    }
    public ArrayList<String> getStringArrayListForFolders(){
        ArrayList<String> foldersArrayList = new ArrayList<>();
        String query = "SELECT distinct _group FROM _contacts;";
        cursor = null;
        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor != null && cursor.getCount() > 0) {

            do {
                foldersArrayList.add(new String(cursor.getString(0
                )));
            } while (cursor.moveToNext());
        }


        if(foldersArrayList.size()>0){

            return foldersArrayList;} else{
            return null;
        }
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
    public static void loadAd(){
        try{

            if(interstitialAd.isLoaded()){
                interstitialAd.show();
                Log.d("JOSH", "ad tried to show");
            }else{
                Log.d("JOSH", "ad not loaded");}

        }catch (Exception e){e.printStackTrace();}
        Log.d("JOSH", "caught");
    }
   public ArrayList<String> buildArrayListFromSpinner(){
       namesToSend = new ArrayList<>();
       String spin =  getCurrentInSpinner();
       ArrayList<String> temp = new ArrayList<>();
       Cursor c = db.rawQuery("SELECT * FROM _contacts WHERE _group = '" + spin + "'", null);
       if(c != null && c.moveToFirst()){
           do{
               temp.add(c.getString(2));
               namesToSend.add(c.getString(1));
           }while(c.moveToNext());
       }
       return temp;
    }
}

