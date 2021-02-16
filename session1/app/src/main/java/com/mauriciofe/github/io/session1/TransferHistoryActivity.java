package com.mauriciofe.github.io.session1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

public class TransferHistoryActivity extends AppCompatActivity {

    RecyclerView listHistoryAsset;
    Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_history);
        btnBack = findViewById(R.id.btnBack);
        listHistoryAsset = findViewById(R.id.list_history_transfer);
    }
}