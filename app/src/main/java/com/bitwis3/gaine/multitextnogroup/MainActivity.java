package com.bitwis3.gaine.multitextnogroup;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;
import spencerstudios.com.fab_toast.FabToast;


public class MainActivity extends AppCompatActivity {


    ArrayList<String> namesToSend;
    ArrayAdapter<String> adapterForSpinner;
    Cursor cursor;
    static final Integer SMS = 0x5;
    static final Integer ACCOUNTS = 0x6;
    ArrayList<String> numbersToSend;
//    DBHelper helper = null;
    FloatingActionButton fab;
//    SQLiteDatabase db = null;
    public static boolean returning = false;
    private Uri uriContact;
    private String contactID;
    public static final int MAX_PICK_CONTACT = 10;
    ListView lv;
    Spinner spinner;
    TabLayout tablayout;
    LinearLayout LL1;
    private int selectedTabint = 1;
    String answer = "";
    DBRoom db;
    private int resumeCount = 0;
    private int havePermission = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       ;
        if(getIntent().hasExtra("timed")){
            getSupportActionBar().setTitle("Timed Text Message");
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient3));
        }else{
            getSupportActionBar().setTitle("Text Many No Group");
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient2));        }
        SharedPreferences prefs = getSharedPreferences("TIP", MODE_PRIVATE);
        answer = prefs.getString("tip", "yes");
        SharedPreferences prefs2 = getSharedPreferences("PERMISSION", MODE_PRIVATE);
        havePermission = prefs2.getInt("permission", 0);
//        helper = new DBHelper(this, "_contactDB", null, 1);
//        db = helper.getWritableDatabase();
        db = Room.databaseBuilder(getApplicationContext(),
                DBRoom.class, "_database_multi_master")
                 .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        spinner = (Spinner) findViewById(R.id.spinnerinMain);
        lv = (ListView) findViewById(R.id.listview);
        tablayout = (TabLayout) findViewById(R.id.tabLayout);
        tablayout.addTab(tablayout.newTab().setText("Select Recipients"));
        tablayout.addTab(tablayout.newTab().setText("Select Group"));

        LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL1.setVisibility(View.GONE);
        changeTabsFont();

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

                        if (answer.equals("yes")) {
                            final Dialog dialog = new Dialog(MainActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.dialogfirstrun, null);
                            Button button = (Button) mView.findViewById(R.id.buttondialog);
                            final CheckBox cb = (CheckBox) mView.findViewById(R.id.checkbox);
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
                            dialog.show();
                        }

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


        // initList();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildArrayListFromPositions();
                switch (selectedTabint) {

                    case 1:
                        if (numbersToSend.size() > 0) {
                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            if(getIntent().hasExtra("timed")){
                                intent.putExtra("timed", "timed");
                            }
                            intent.putExtra("array", numbersToSend);
                            intent.putExtra("names", namesToSend);
                            startActivity(intent);
                            Bungee.slideLeft(MainActivity.this);


                        } else {
                            FabToast.makeText(MainActivity.this,
                                    "No Recipients selected, check recipients or choose group!",
                                    Toast.LENGTH_LONG, FabToast.WARNING, FabToast.POSITION_DEFAULT).show();
                        }
                        break;

                    case 2:
                        String inSpinnerNow = getCurrentInSpinner();
                        if (inSpinnerNow.length() > 0) {
                            numbersToSend = buildArrayListFromSpinner();
                            Intent intent2 = new Intent(MainActivity.this, Main2Activity.class);
                            if(getIntent().hasExtra("timed")){
                                intent2.putExtra("timed", "timed");
                            }
                            intent2.putExtra("array", numbersToSend);
                            intent2.putExtra("names", namesToSend);
                            startActivity(intent2);
                            Bungee.slideLeft(MainActivity.this);
                            MainActivity.this.finish();
                        } else {
                            FabToast.makeText(MainActivity.this, "No groups selected, you must create group first!",
                                    Toast.LENGTH_LONG, FabToast.WARNING, FabToast.POSITION_DEFAULT).show();

                        }
                        break;
                }
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillListViewFromGroup();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadFolderSpinner();
        askForPermission(Manifest.permission.READ_CONTACTS, ACCOUNTS);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//
//        if (id == R.id.other_apps) {
//            Task.MoreApps(this, "GainWise");
//            return true;
//        }
//        if (id == R.id.ra) {
//            Intent intent = new Intent(MainActivity.this, CreateContactList.class);
//            startActivity(intent);
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


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

                    break;
                //Accounts
                case 6:
                    extraMethod();
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                Bungee.slideRight(this);
                MainActivity.this.finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {


                //Accounts
                case 6:
                    extraMethod();

            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor3 = getSharedPreferences("PERMISSION", MODE_PRIVATE).edit();
            editor3.putInt("permission", 1);
            editor3.apply();
            havePermission = 1;
        } else {
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


    public void fillListView() {
        lv.setAdapter(null);
        CustomListAdapter.setArrayList();
        CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, getContactsList(MainActivity.this));
        lv.setAdapter(adapter);
    }

    public static ArrayList<Contact> getContactsList(Context context) {
        ArrayList<String> numbers = new ArrayList<>();
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            Log.i("JOSHtest", "" + type);
           if(!numbers.contains(phoneNumber)){
               contacts.add(new Contact(name, phoneNumber, type));
           }
            numbers.add(phoneNumber);
        }
        phones.close();
        Collections.sort(contacts, new Comparator<Contact>() {
            public int compare(Contact v1, Contact v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });



        return contacts;
    }

    public void buildArrayListFromPositions() {
        namesToSend = new ArrayList<>();
        numbersToSend = new ArrayList<>();
        ArrayList<Integer> positions = CustomListAdapter.getArrayList();

        int size = positions.size();

        for (int i = 0; i < size; i++) {
            Log.i("JOSH", "hello");
            Contact c = (Contact) lv.getItemAtPosition(positions.get(i));
            numbersToSend.add(c.getNumber());
            namesToSend.add(c.getName());
        }

    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    public void fillListViewFromGroup() {

        lv.setAdapter(null);
        if (getCurrentInSpinner().length() > 0) {
            Cursor c = db.multiDOA().getAllInGroup(getCurrentInSpinner());
            MyCursorAdapter myCursorAdapter = new MyCursorAdapter(MainActivity.this, c);
            lv.setAdapter(myCursorAdapter);

        }


    }


    @Override
    protected void onResume() {
        super.onResume();



    }

    public void loadFolderSpinner() {
                List<String> list = db.multiDOA().getDistinctGroups();
                Log.i("JOSHnew", "size: " + list.size());
        if(list.size() > 0){
                adapterForSpinner = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.spinnerz, list);

                adapterForSpinner.setDropDownViewResource(R.layout.spinnerzdrop);

                    spinner.setAdapter(adapterForSpinner);
                }else{
                    spinner.setAdapter(null);
                }

            }




    public String getCurrentInSpinner() {
        String now = "";
        if (spinner.getAdapter() != null ) {
            now = spinner.getSelectedItem().toString();
        }
        if (now.length() > 0) {
            return now;
        } else {
            return "";
        }
    }




    public ArrayList<String> buildArrayListFromSpinner() {
        namesToSend = new ArrayList<>();
        String spin = getCurrentInSpinner();
        ArrayList<String> temp = new ArrayList<>();
        Cursor c = db.multiDOA().getAllInGroup(spin);
        if (c != null && c.moveToFirst()) {
            do {
                temp.add(c.getString(2));
                namesToSend.add(c.getString(1));
            } while (c.moveToNext());
        }

        return temp;
    }

    private void extraMethod() {
        // buildArrayListFromPositions();
        CustomListAdapter.setArrayList();

        Log.d("JOSH", "RESUMECOUNT: " + resumeCount);

        loadFolderSpinner();
        Log.d("JOSH", "RESUMECOUNT: " + resumeCount);
        fillListView();
    }


//    private void checkForConsent() {
//        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
    //
    //        ConsentInformation.getInstance(MainActivity.this).addTestDevice("E8F9908FCA6E01002BCD08F42B00E801");
    //        ConsentInformation.getInstance(MainActivity.this).
    //                setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
//        String[] publisherIds = {"pub-6280186717837639"};
//        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
//            @Override
//            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
//                // User's consent status successfully updated.
//                switch (consentStatus) {
//                    case PERSONALIZED:
//                        Log.d("JOSHad", "Showing Personalized ads");
//                        showPersonalizedAds();
//                        break;
//                    case NON_PERSONALIZED:
//                        Log.d("JOSHad", "Showing Non-Personalized ads");
//                        showNonPersonalizedAds();
//                        break;
//                    case UNKNOWN:
//                        Log.d("JOSHad", "Requesting Consent");
//                        if (ConsentInformation.getInstance(getBaseContext())
//                                .isRequestLocationInEeaOrUnknown()) {
//                            Log.d("JOSHad", "helper2");
//
//                            requestConsent();
//
//                        } else {
//                            Log.d("JOSHad", "helper3");
//                            showPersonalizedAds();
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailedToUpdateConsentInfo(String errorDescription) {
//                // User's consent status failed to update.
//            }
//        });
//    }
//
//    private void requestConsent() {
//        Log.d("JOSHad", "helper1");
//
//        URL privacyUrl = null;
//        try {
//            // TODO: Replace with your app's privacy policy URL.
//            privacyUrl = new URL("https://docs.google.com/document/d/e/2PACX-1vS0f1URBeRQ6Lrhi1W5KxC6eDjxB46OwZOLv8VKoE6DmN5kpESA7EqHNB0qbt08amyr5Iv-Yx_HXubK/pub");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            // Handle error.
//        }
//        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
//                .withListener(new ConsentFormListener() {
//                    @Override
//                    public void onConsentFormLoaded() {
//                        // Consent form loaded successfully.
//                        Log.d("JOSHad", "Requesting Consent: onConsentFormLoaded");
//                        showForm();
//                    }
//
//                    @Override
//                    public void onConsentFormOpened() {
//                        // Consent form was displayed.
//                        Log.d("JOSHad", "Requesting Consent: onConsentFormOpened");
//                    }
//
//                    @Override
//                    public void onConsentFormClosed(
//                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
//                        Log.d("JOSHad", "Requesting Consent: onConsentFormClosed");
//                        if (userPrefersAdFree) {
//                            // Buy or Subscribe
//                            Log.d("JOSHad", "Requesting Consent: User prefers AdFree");
//                        } else {
//                            Log.d("JOSHad", "Requesting Consent: Requesting consent again");
//                            switch (consentStatus) {
//                                case PERSONALIZED:
//                                    showPersonalizedAds();break;
//                                case NON_PERSONALIZED:
//                                    showNonPersonalizedAds();break;
//                                case UNKNOWN:
//                                    showNonPersonalizedAds();break;
//                            }
//
//                        }
//                        // Consent form was closed.
//                    }
//
//                    @Override
//                    public void onConsentFormError(String errorDescription) {
//                        Log.d("JOSHad", "Requesting Consent: onConsentFormError. Error - " + errorDescription);
//                        // Consent form error.
//                    }
//                })
//                .withPersonalizedAdsOption()
//                .withNonPersonalizedAdsOption()
//                .withAdFreeOption()
//                .build();
//        form.load();
//    }
//
//    private void showPersonalizedAds() {
//
//        interstitialAd = new InterstitialAd(MainActivity.this);
//        interstitialAd.setAdUnitId("ca-app-pub-6280186717837639/5716084168");
//
//        interstitialAd.loadAd(new AdRequest.Builder().build());
//        interstitialAd.setAdListener(new AdListener()
//        {
//            @Override
//            public void onAdClosed(){
//
//                interstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//        });
//
//    }
//
//    private void showNonPersonalizedAds() {
//
//        interstitialAd = new InterstitialAd(MainActivity.this);
//        interstitialAd.setAdUnitId("ca-app-pub-6280186717837639/5716084168");
//
//        interstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle()).build());
//        interstitialAd.setAdListener(new AdListener()
//        {
//            @Override
//            public void onAdClosed(){
//
//                interstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle()).build());
//            }
//        });
//
//
//
//    }
//    public Bundle getNonPersonalizedAdsBundle() {
//        Bundle extras = new Bundle();
//        extras.putString("npa", "1");
//
//        return extras;
//    }
//    private void showForm() {
//        if (form == null) {
//            Log.d("JOSHad", "Consent form is null");
//        }
//        if (form != null) {
//            Log.d("JOSHad", "Showing consent form");
//            form.show();
//        } else {
//            Log.d("JOSHad", "Not Showing consent form");
//        }
//    }
//
private void changeTabsFont() {
    Typeface font = Typeface.createFromAsset(MainActivity.this.getAssets(), "Acme-Regular.ttf");
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
    public void onBackPressed() {
        super.onBackPressed();

        Bungee.slideRight(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.this.finish();
    }
}
