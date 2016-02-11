 package com.informix.goverbook;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;


import java.util.ArrayList;

 public class MainActivity extends TabActivity {
     static EditText etSearch;
     Spinner spinner;
     Intent intent;
     DBHelper dbHelper;
     SQLiteDatabase database;
     ArrayList<UserContact> userContact;
     ListView searchResult;
     String[][] areas;



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
                ListView searchResult = new ListView(MainActivity.this);
                searchResult.setId(R.id.searchFioResult);
                return searchResult;
            }
 };

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator("Сотрудники");
        tabHost.addTab(tabSpec);


        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Организации");
        tabSpec.setContent(new Intent(this, OrgActivity.class));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator("Избранные");
        tabSpec.setContent(new Intent(this, FavoritsAcitivity.class));
        tabHost.addTab(tabSpec);

        areas = dbHelper.areaGetter(database);
        for (int i = 0; i < (areas[0].length); i++) {
            areaName.add(areas[0][i]);
            areaIdS.add(Integer.parseInt(areas[1][i]));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(),
                android.R.layout.simple_list_item_1, areaName);
        spinner.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>2) {
                    startSearch();
                }
            }
        });

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

     public void startSearch(){
         userContact = dbHelper.searchByFio(etSearch.getText().toString(), database);
         ItemMenuUsers itemMenuUsers = new ItemMenuUsers(userContact);
         searchResult = (ListView) findViewById(R.id.searchFioResult);
         itemMenuUsers.DrawMenu(searchResult);
         searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                 intent.putExtra("userid", userContact.get(position).getId());
                 startActivity(intent);
             }
         });

     }




 }
