package com.mauriciofe.github.io.session1;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mauriciofe.github.io.session1.models.AssetGroup;
import com.mauriciofe.github.io.session1.models.Assets;
import com.mauriciofe.github.io.session1.models.Department;
import com.mauriciofe.github.io.session1.models.Employee;
import com.mauriciofe.github.io.session1.models.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AssetInformationActivity extends AppCompatActivity {
    Spinner spnDepartment;
    Spinner spnLocation;
    Spinner spnAssetGroup;
    Spinner spnAccountable;
    EditText edtAssetName;
    EditText edtDescription;
    EditText edtWarrantyDate;
    TextView txtAssetSN;
    Button btnSubmit;
    private long departmentId;
    private long locationId;
    private long assetGroupId;
    private long accounableId;
    List<Assets> assetSnList = new ArrayList<>();
    private static final String BASE_URL = "http://192.168.0.105:5000/api/";
    String departmentIdStr;
    String assetGroupIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_information);
        inicializaCampos();
        preencheSpinnerAssetGroups();
        preencheSpinnerDepartment();
        preencheSpinnerLocation();
        preencheSpinnerAccountable();
    }

    private void preencheSpinnerDepartment() {
        List<String> departmentNameList = new ArrayList<>();
        List<Integer> departmentIdList = new ArrayList<>();
        ConfigConsumeApi.requestApi(BASE_URL + "departments", new Callback<String>() {

            @Override
            public void onComplete(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    departmentIdList.add(0);
                    departmentNameList.add("Department");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        departmentIdList.add(jsonObject.getInt("id"));
                        departmentNameList.add(jsonObject.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(AssetInformationActivity.this,
                                android.R.layout.simple_spinner_item, departmentNameList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                if (spnDepartment != null) {
                    spnDepartment.setAdapter(adapter);

                    spnDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            departmentId = departmentIdList.get(position);

                            if (assetGroupId > 0 && departmentId > 0) {
                                departmentIdStr = String.valueOf(departmentId);
                                assetGroupIdStr = String.valueOf(assetGroupId);
                                departmentIdStr = departmentIdStr.length() < 2 ? "0" + departmentIdStr : departmentIdStr;
                                assetGroupIdStr = assetGroupIdStr.length() < 2 ? "0" + assetGroupIdStr : assetGroupIdStr;
                                preencheAssetSn();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            departmentId = 0;
                        }
                    });
                }
            }
        });
    }

    private void preencheAssetSn() {
        ConfigConsumeApi.requestApi(BASE_URL + "assets/search?assetSn=" + departmentIdStr + "/" + assetGroupIdStr,
                new Callback<String>() {
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
                                assetSn = departmentIdStr + "/" + assetGroupIdStr + "/" + bacon;
                            } else {
                                assetSn = departmentIdStr + "/" + assetGroupIdStr + "/0001";
                            }
                            txtAssetSN.setText(assetSn);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void preencheSpinnerLocation() {
        List<Integer> locationListId = new ArrayList<>();
        List<String> locationListName = new ArrayList<>();
        ConfigConsumeApi.requestApi(BASE_URL + "location", new Callback<String>() {
            @Override
            public void onComplete(String result) {
                locationListId.add(0);
                locationListName.add("Locations");
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        locationListId.add(jsonObject.getInt("id"));
                        locationListName.add(jsonObject.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayAdapter adapter = new ArrayAdapter(AssetInformationActivity.this,
                        android.R.layout.simple_spinner_item, locationListName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLocation.setAdapter(adapter);
                spnLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        locationId = locationListId.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        locationId = 0;
                    }
                });
            }
        });
    }

    private void preencheSpinnerAccountable() {
        List<Integer> employeeListId = new ArrayList<>();
        List<String> employeeListName = new ArrayList<>();
        employeeListId.add(0);
        employeeListName.add("Accontable Party");
        ConfigConsumeApi.requestApi(BASE_URL + "Employees", new Callback<String>() {
            @Override
            public void onComplete(String result) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        employeeListId.add(jsonObject.getInt("id"));
                        employeeListName.add(jsonObject.getString("firstName") + jsonObject.getString("lastName"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayAdapter adapter = new ArrayAdapter(AssetInformationActivity.this,
                        android.R.layout.simple_spinner_item, employeeListName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnAccountable.setAdapter(adapter);
                spnAccountable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accounableId = employeeListId.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        accounableId = 0;
                    }
                });
            }
        });
    }

    private void preencheSpinnerAssetGroups() {
        List<String> assetGroupsNameList = new ArrayList<>();
        List<Integer> assetGroupsIdList = new ArrayList<>();
        assetGroupsIdList.add(0);
        assetGroupsNameList.add("Asset Group");
        ConfigConsumeApi.requestApi(BASE_URL + "assetGroups", new Callback<String>() {
            @Override
            public void onComplete(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        assetGroupsIdList.add(jsonObject.getInt("id"));
                        assetGroupsNameList.add(jsonObject.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(AssetInformationActivity.this,
                                android.R.layout.simple_spinner_item, assetGroupsNameList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                if (spnAssetGroup != null) {
                    spnAssetGroup.setAdapter(adapter);
                    spnAssetGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            assetGroupId = assetGroupsIdList.get(position);
                            if (assetGroupId > 0 && departmentId > 0) {
                                departmentIdStr = String.valueOf(departmentId);
                                assetGroupIdStr = String.valueOf(assetGroupId);
                                departmentIdStr = departmentIdStr.length() < 2 ? "0" + departmentIdStr : departmentIdStr;
                                assetGroupIdStr = assetGroupIdStr.length() < 2 ? "0" + assetGroupIdStr : assetGroupIdStr;
                                preencheAssetSn();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            assetGroupId = 0;
                        }
                    });
                }
            }
        });
    }

    private void inicializaCampos() {
        spnDepartment = findViewById(R.id.spn_department);
        spnLocation = findViewById(R.id.spnLocation);
        spnAssetGroup = findViewById(R.id.spnAsset_group);
        spnAccountable = findViewById(R.id.spnAccountable);
        edtAssetName = findViewById(R.id.edtAssetName);
        edtDescription = findViewById(R.id.edtAssetDescriptionMult);
        edtWarrantyDate = findViewById(R.id.edtWarrantyDate);
        txtAssetSN = findViewById(R.id.txtAssetSn);
    }

}

