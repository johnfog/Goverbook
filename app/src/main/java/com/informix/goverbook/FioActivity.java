package com.informix.goverbook;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class FioActivity extends Activity{
    DBHelper dbHelper;
    SQLiteDatabase database;

    ListView searchResult;
    Intent intent;
    EditText etSearch;
    ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fio);

    }


}
