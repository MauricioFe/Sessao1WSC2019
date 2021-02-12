package com.mauriciofe.github.io.session1;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mauriciofe.github.io.session1.Constantes.BASE_URL;

public class AssetTransferActivity extends AppCompatActivity {
    private long assetId;
    EditText edtAssetName;
    EditText edtAssetSN;
    EditText edtNewAssetSN;
    EditText edtCurrentDepartment;
    Spinner spnDestinationDepartment;
    Spinner spnLocationDepartment;
    Button btnSubmit;
    Button btnCancel;
    List<String> departmentListString = new ArrayList<>();
    List<Integer> departmentListInt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_transfer);
        assetId = getIntent().getLongExtra("assetId", 0);
        inicializaCampos();
        MyAsyncTask.requestApi(BASE_URL + "assets/" + assetId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
            @Override
            public void onComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    long departmentId = jsonObject.getLong("departmentId");
                    edtAssetName.setText(jsonObject.getString("assetName"));
                    edtAssetSN.setText(jsonObject.getString("assetSn"));
                    edtCurrentDepartment.setText(jsonObject.getString("nameDescription"));
                    MyAsyncTask.requestApi(BASE_URL + "departments/destino/" + departmentId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
                        @Override
                        public void onComplete(String result) {
                            departmentListString.add("Destination Department");
                            departmentListInt.add(0);
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    departmentListInt.add(jsonObject.getInt("id"));
                                    departmentListString.add(jsonObject.getString("name"));
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter(AssetTransferActivity.this, android.R.layout.simple_spinner_item, departmentListString);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnDestinationDepartment.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void inicializaCampos() {
        edtAssetName = findViewById(R.id.transfer_edtAssetName);
        edtAssetSN = findViewById(R.id.transfer_edtAssetSn);
        edtNewAssetSN = findViewById(R.id.transfer_edtNewAssetSn);
        edtCurrentDepartment = findViewById(R.id.transfer_edtCurrent_department);
        spnDestinationDepartment = findViewById(R.id.transfer_spn_department_destination);
        spnLocationDepartment = findViewById(R.id.transfer_spn_department_location);
        btnCancel = findViewById(R.id.transfer_btnCancel);
        btnSubmit = findViewById(R.id.transfer_btnSubmit);
    }
}