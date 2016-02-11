 package com.informix.goverbook;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;


import java.util.ArrayList;

 public class MainActivity extends TabActivity {
     static EditText etSearch;
     Spinner spinner;
     Intent intent;
     DBHelper dbHelper;
     SQLiteDatabase database;
     ArrayList<UserContact> userContact;
     ArrayList<String> orgNames;
     ArrayList<String> orgTypes = new ArrayList<String>();
     ArrayList<Integer> orgTypesId = new ArrayList<Integer>();
     ListView searchResultFio;
     ExpandableListView searchResultOrg;
     String[][] areas;
     OrgContact org;
     String[][] list;
     ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
     ExpListAdapter adapterForTypes;
     ExpListAdapter adapterForOrgs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<String> areaName = new ArrayList<String>();
        ArrayList<Integer> areaIdS= new ArrayList<Integer>();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        etSearch = (EditText) findViewById(R.id.searchString);
        spinner = (Spinner) findViewById(R.id.spinner);

        TabHost tabHost = getTabHost();
        TabHost.TabSpec tabSpec;

        TabHost.TabContentFactory TabFactory = new TabHost.TabContentFactory(){
            @Override
            public View createTabContent(String tag) {
                searchResultFio = new ListView(MainActivity.this);
                searchResultFio.setId(R.id.searchFioResult);
                return searchResultFio;
            }
 };

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator("Сотрудники");
        tabHost.addTab(tabSpec);

         TabFactory = new TabHost.TabContentFactory(){
            @Override
            public View createTabContent(String tag) {
                searchResultOrg = new ExpandableListView(MainActivity.this);
                searchResultOrg.setId(R.id.searchOrgResult);
                return searchResultOrg;
            }
        };

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Организации");
        tabSpec.setContent(TabFactory);
        tabHost.addTab(tabSpec);

        TabFactory = new TabHost.TabContentFactory(){
            @Override
            public View createTabContent(String tag) {
                ListView searchResult = new ListView(MainActivity.this);
                searchResult.setId(R.id.searchFaveResult);
                return searchResult;
            }
        };

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator("Избранные");
        tabSpec.setContent(TabFactory);
        tabHost.addTab(tabSpec);

        tab1Actions();


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (getTabHost().getCurrentTabTag().equals("tag1")) {
                    tab1Actions();
                }

                    if (getTabHost().getCurrentTabTag().equals("tag2")){
                    tab2Actions();
                    }


            }
        });


        areas = dbHelper.areaGetter(database);
        for (int i = 0; i < (areas[0].length); i++) {
            areaName.add(areas[0][i]);
            areaIdS.add(Integer.parseInt(areas[1][i]));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(),
                android.R.layout.simple_list_item_1, areaName);
        spinner.setAdapter(adapter);



    }



     // Метод сворачиваня клавиатуры
     public static void hideSoftKeyboard(Activity activity) {
         InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
         inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
     }


     public void onClickSettings(View view) {
         intent = new Intent(this, SettingsActivity.class);
         startActivity(intent);
     }

     public void startSearchFio(){
         userContact = dbHelper.searchByFio(etSearch.getText().toString(), database);
         ItemMenuUsers itemMenuUsers = new ItemMenuUsers(userContact);
         searchResultFio = (ListView) findViewById(R.id.searchFioResult);
         itemMenuUsers.DrawMenu(searchResultFio);
         searchResultFio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                 intent.putExtra("userid", userContact.get(position).getId());
                 startActivity(intent);
             }
         });

     }

     private void ShowOrgUsers(String clickedOrgName) {
         SQLiteDatabase database = dbHelper.getReadableDatabase();
         ExpandableListView searchResult = (ExpandableListView) findViewById(R.id.searchOrgResult);
         org=dbHelper.searchOrgByName(clickedOrgName, database);
         org.DrawOrgContact(searchResult, getApplicationContext());

     }


     private void ListOrg(SQLiteDatabase database) {

         String[][] orgListByType;
         ArrayList<Integer> orgId = new ArrayList<Integer>();


//         try {
             list = dbHelper.ListOrg(database);

             for (int i = 0; i < (list[0].length); i++) {
                 orgTypes.add(list[0][i]);
                 orgTypesId.add(Integer.parseInt(list[1][i]));
             }


             for (int i=0;i< (orgTypesId.size());i++) {
                 orgListByType = dbHelper.ListOrgOnId(String.valueOf(orgTypesId.get(i)),database);
                 orgNames= new ArrayList<String>();

                 for (int k = 0; k < (orgListByType[0].length); k++) {
                     orgNames.add(orgListByType[0][k]);
                     orgId.add(Integer.parseInt(orgListByType[1][k]));
                 }
                 groups.add(orgNames);
             }

             adapterForTypes = new ExpListAdapter(getApplicationContext(), groups,orgTypes,true);
             searchResultOrg.setAdapter(adapterForTypes);


             etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {

                 @Override
                 public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                     startSearchOrg();
                     hideSoftKeyboard(MainActivity.this);
                     return actionId == EditorInfo.IME_ACTION_DONE;
                 }
             });
//         } catch (Exception e) {}
         getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


     }



     public void startSearchOrg() {

         ExpandableListView listView = (ExpandableListView) findViewById(R.id.searchOrgResult);
         list = dbHelper.SearchOrg(etSearch.getText().toString(),database);

         orgTypes.clear();
         for (int i = 0; i < (list[0].length); i++) {
             orgTypes.add(list[0][i]);
             orgTypesId.add(Integer.parseInt(list[1][i]));
         }

         adapterForOrgs = new ExpListAdapter(getApplicationContext(),orgTypes,false);
         listView.setAdapter(adapterForOrgs);

         searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
             @Override
             public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                 org = dbHelper.searchOrgByName(adapterForOrgs.getGroup(groupPosition).toString(), database);
                 org.DrawOrgContact(searchResultOrg, getApplicationContext());
                 return false;
             }
         });
         searchResultOrg.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
             @Override
             public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                 Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                 intent.putExtra("userid", org.GetUserIdOnOrg(groupPosition, childPosition));
                 startActivity(intent);
                 return false;
             }
         });

     }


     private void tab1Actions() {
         etSearch.setText("");

         etSearch.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

             }

             @Override
             public void afterTextChanged(Editable s) {
                 if (s.length() > 2) {
                     startSearchFio();
                 }
             }
         });


     }


     private void tab2Actions() {
         groups.clear();
         orgTypes.clear();
         ListOrg(database);
         etSearch.setText("");

         etSearch.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

             }

             @Override
             public void afterTextChanged(Editable s) {
                 if (s.length() > 2) {
                     startSearchOrg();
                 }
             }
         });

         searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
             @Override
             public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                 return false;
             }
         });

         searchResultOrg.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
             @Override
             public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                 String clickedOrgName = adapterForTypes.getChildById(groupPosition, childPosition);
                 ShowOrgUsers(clickedOrgName);

                 searchResultOrg.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                     @Override
                     public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                         Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                         intent.putExtra("userid", org.GetUserIdOnOrg(groupPosition, childPosition));
                         startActivity(intent);
                         return false;
                     }
                 });

                 return false;
             }
         });



     }


 }
