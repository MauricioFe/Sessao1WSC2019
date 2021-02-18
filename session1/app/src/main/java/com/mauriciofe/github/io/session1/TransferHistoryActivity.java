package com.mauriciofe.github.io.session1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mauriciofe.github.io.session1.models.AssetTransferLogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.mauriciofe.github.io.session1.Constantes.BASE_URL;

public class TransferHistoryActivity extends AppCompatActivity {

    RecyclerView listHistoryAsset;
    Button btnBack;
    TextView txtMensagem;
    List<AssetTransferLogs> logsList = new ArrayList<>();
    TransferHistoryAdapter adapter;
    long assetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("History of transfer");
        setContentView(R.layout.activity_transfer_history);
        btnBack = findViewById(R.id.btnBack);
        txtMensagem = findViewById(R.id.txtMensagem);
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String transferDate = jsonObject.getString("transferDate").substring(0, 10);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate dateTime = LocalDate.parse(transferDate, formatter);
                        LocalDate dataTeste2 = LocalDate.now();
                        long meses = dateTime.until(dataTeste2, ChronoUnit.MONTHS);
                        if (meses > 12) {
                            logsList.clear();
                        } else {
                            AssetTransferLogs log = new AssetTransferLogs(jsonObject.getInt("id"),
                                    jsonObject.getInt("assetId"), jsonObject.getString("fromAssetSN"),
                                    jsonObject.getString("fromDepartment"), jsonObject.getString("toAssetSN"),
                                    jsonObject.getString("toDepartment"), jsonObject.getInt("fromDepartmentLocationId"),
                                    jsonObject.getInt("toDepartmentLocationId"),
                                    jsonObject.getString("transferDate"));
                            logsList.add(log);
                        }
                    }
                    adapter.atualizaList(logsList);
                    if (logsList.size() <= 0){
                        listHistoryAsset.setVisibility(View.INVISIBLE);
                        txtMensagem.setVisibility(View.VISIBLE);
                    }else{
                        listHistoryAsset.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}