package com.mauriciofe.github.io.session1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mauriciofe.github.io.session1.models.AssetTransferLogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.security.DomainLoadStoreParameter;
import java.util.ArrayList;
import java.util.List;

import static com.mauriciofe.github.io.session1.Constantes.BASE_URL;

public class TransferHistoryActivity extends AppCompatActivity {

    RecyclerView listHistoryAsset;
    Button btnBack;
    List<AssetTransferLogs> logsList = new ArrayList<>();
    TransferHistoryAdapter adapter;
    long assetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_history);
        btnBack = findViewById(R.id.btnBack);
        listHistoryAsset = findViewById(R.id.list_history_transfer);
        adapter = new TransferHistoryAdapter(this, logsList);
        listHistoryAsset.setLayoutManager(new LinearLayoutManager(this));
        listHistoryAsset.setAdapter(adapter);
        Intent intent = getIntent();
        assetId = intent.getLongExtra("assetId", 0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransferHistoryActivity.this, MainActivity.class));
            }
        });
        MyAsyncTask.requestApi(BASE_URL + "assetTransferLogs/" + assetId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
            @Override
            public void onComplete(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        AssetTransferLogs log = new AssetTransferLogs(jsonObject.getInt("id"),
                                jsonObject.getInt("assetId"), jsonObject.getString("fromAssetSN"),
                                jsonObject.getString("fromDepartment"), jsonObject.getString("toAssetSN"),
                                jsonObject.getString("toDepartment"), jsonObject.getInt("fromDepartmentLocationId"),
                                jsonObject.getInt("toDepartmentLocationId"),
                                jsonObject.getString("transferDate"));
                        logsList.add(log);
                    }
                    adapter.atualizaList(logsList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}