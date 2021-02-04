package com.mauriciofe.github.io.session1;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    Spinner spnDepartment;
    Spinner spnAssetGroup;
    EditText edtStartDate;
    EditText edtEndDate;
    EditText edtSearch;
    ListView listViewAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        spnDepartment = findViewById(R.id.main_spn_department);
        spnAssetGroup = findViewById(R.id.main_spn_asset_group);
        edtStartDate = findViewById(R.id.editEndDate);
        edtEndDate = findViewById(R.id.editStartDate);
        edtSearch = findViewById(R.id.editTextSearch);
        listViewAssets = findViewById(R.id.listViewAssets);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}