package com.mauriciofe.github.io.session1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

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
    List<String> locationListString = new ArrayList<>();
    List<Integer> locationListInt = new ArrayList<>();
    long departmentId;
    long locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_transfer);
        assetId = getIntent().getLongExtra("assetId", 0);
        inicializaCampos();
        preencheSpinnerDepartment();
        preencheSpinnerLocalizacao();
        cancelarAcao();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAsyncTask.requestApi(BASE_URL + "departmentLocation/" + departmentId + "/" + locationId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
                    @Override
                    public void onComplete(String result) {
                        MyAsyncTask.requestApi(BASE_URL + "departmentLocation/" + departmentId + "/" + locationId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
                            @Override
                            public void onComplete(String result) {
                                try {
                                    transfer(result);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void transfer(String result) throws JSONException {
        JSONStringer js = new JSONStringer();
        js.object();
        js.key("assetSn").value(edtNewAssetSN.getText().toString());
        js.key("departmentLocationId").value(new JSONObject(result).getInt("id"));
        js.endObject();
        MyAsyncTask.requestApi(BASE_URL + "assets/transferAsset/" + assetId, MyAsyncTask.METHOD_PUT, js.toString(), new Callback<String>() {
            @Override
            public void onComplete(String result) {
                startActivity(new Intent(AssetTransferActivity.this, MainActivity.class));
            }
        });
    }

    private void cancelarAcao() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssetTransferActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_back) {
            startActivity(new Intent(AssetTransferActivity.this, MainActivity.class));
            finish();
        }
        return true;
    }

    private void preencheSpinnerLocalizacao() {
        MyAsyncTask.requestApi(BASE_URL + "location/" + departmentId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
            @Override
            public void onComplete(String result) {
                try {
                    locationListInt.clear();
                    locationListString.clear();
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        locationListInt.add(jsonObject.getInt("id"));
                        locationListString.add(jsonObject.getString("name"));
                    }
                    ArrayAdapter adapter = new ArrayAdapter(AssetTransferActivity.this, android.R.layout.simple_spinner_item, locationListString);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnLocationDepartment.setAdapter(adapter);
                    spnLocationDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            locationId = locationListInt.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void preencheSpinnerDepartment() {
        MyAsyncTask.requestApi(BASE_URL + "assets/" + assetId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
            @Override
            public void onComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    departmentId = jsonObject.getLong("departmentId");
                    edtAssetName.setText(jsonObject.getString("assetName"));
                    edtAssetSN.setText(jsonObject.getString("assetSn"));
                    edtCurrentDepartment.setText(jsonObject.getString("nameDescription"));
                    MyAsyncTask.requestApi(BASE_URL + "departments/destino/" + departmentId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
                        @Override
                        public void onComplete(String result) {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    departmentListInt.add(jsonObject.getInt("id"));
                                    departmentListString.add(jsonObject.getString("name"));
                                }
                                ArrayAdapter adapter = new ArrayAdapter(AssetTransferActivity.this, android.R.layout.simple_spinner_item, departmentListString);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnDestinationDepartment.setAdapter(adapter);
                                spnDestinationDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        departmentId = departmentListInt.get(position);
                                        preencheSpinnerLocalizacao();
                                        geraNewAssetSn(departmentId);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
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

    private void geraNewAssetSn(long departmentId) {
        String assetGroupId = edtAssetSN.getText().toString().substring(3, 5);
        String departmentIdStr;
        if (String.valueOf(departmentId).length() > 1)
            departmentIdStr = String.valueOf(departmentId);
        else
            departmentIdStr = "0" + departmentId;
        MyAsyncTask.requestApi(BASE_URL + "assets/search?assetSn=" + departmentIdStr + "/" + assetGroupId + "/", MyAsyncTask.METHOD_GET, null, new Callback<String>() {
            @Override
            public void onComplete(String result) {
                List<String> assetsListString = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        assetsListString.add(jsonObject.getString("assetSn"));
                    }
                    String assetSn = null;
                    String lastAssetSn = null;
                    for (String item : assetsListString) {
                        lastAssetSn = item;
                    }
                    StringBuilder bacon = new StringBuilder();
                    if (lastAssetSn != null) {
                        int n = Integer.parseInt(lastAssetSn.substring(6));
                        for (int i = 0; i < 4 - String.valueOf(n).length(); i++) {
                            bacon.append(0);
                        }
                        bacon.append(n + 1);
                        assetSn = departmentIdStr + "/" + assetGroupId + "/" + bacon;
                    } else {
                        assetSn = departmentIdStr + "/" + assetGroupId + "/0001";
                    }
                    edtNewAssetSN.setText(assetSn);
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