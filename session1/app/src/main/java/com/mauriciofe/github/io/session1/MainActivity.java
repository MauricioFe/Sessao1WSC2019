package com.mauriciofe.github.io.session1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mauriciofe.github.io.session1.models.AssetGroup;
import com.mauriciofe.github.io.session1.models.Assets;
import com.mauriciofe.github.io.session1.models.Department;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.0.104:5000";
    Spinner spnDepartment;
    Spinner spnAssetGroup;
    EditText edtStartDate;
    EditText edtEndDate;
    EditText edtSearch;
    TextView txtCountAssets;
    ListView listViewAssets;
    public long assetGroupId;
    public long departmentId;
    public int numTotalAssets;
    public int numFilteredAssets;
    List<Assets> assetsListTotal = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializandoViews();
        AssetsTask task = new AssetsTask();
        AssetGroupTask taskGroup = new AssetGroupTask();
        DepartmentTask taskDepartment = new DepartmentTask();
        taskGroup.execute(BASE_URL + "/api/AssetGroups");
        taskDepartment.execute(BASE_URL + "/api/Departments");
        task.execute(BASE_URL + "/api/assets");

        edtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (edtSearch.getText().length() > 2) {
                    String urlFilter = BASE_URL + "/api/assets/filter?search=" + edtSearch.getText().toString() +
                            "&assetGroupId=" + assetGroupId + "&departmentId=" + departmentId +
                            "&startDate=" + edtStartDate.getText().toString() + "&endDate=" + edtEndDate.getText().toString();
                    AssetsTask taskFilter = new AssetsTask();
                    taskFilter.execute(urlFilter);
                } else {
                    AssetsTask taskFilter = new AssetsTask();
                    taskFilter.execute(BASE_URL + "/api/assets");
                }
                return true;
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        recebendoIdSpinner();
    }
    int i = 0;

    private void recebendoIdSpinner() {
        spnDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i++;
                if (i == 1) {
                    departmentId = 0;
                } else
                    departmentId = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                departmentId = 0;
            }
        });
        spnAssetGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i++;
                if (i == 1) {
                    assetGroupId = 0;
                } else
                    assetGroupId = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                assetGroupId = 0;
            }
        });
    }

    private void inicializandoViews() {
        spnDepartment = findViewById(R.id.main_spn_department);
        spnAssetGroup = findViewById(R.id.main_spn_asset_group);
        edtStartDate = findViewById(R.id.editStartDate);
        edtEndDate = findViewById(R.id.editEndDate);
        edtSearch = findViewById(R.id.editTextSearch);
        listViewAssets = findViewById(R.id.listViewAssets);
        txtCountAssets = findViewById(R.id.txtCountAssets);
    }

    public class AssetsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-type", "application/json");
                conn.setDoOutput(false);

                StringBuilder stringBuilder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String content) {
            List<Assets> assetsList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Assets assets = new Assets();
                    assets.setId(jsonObject.getInt("id"));
                    assets.setAssetName(jsonObject.getString("assetName"));
                    assets.setAssetSn(jsonObject.getString("assetSn"));
                    assets.setNameDescription(jsonObject.getString("nameDescription"));
                    assetsList.add(assets);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (i==0){
                numTotalAssets = assetsList.size();
            }
            AssetsAdapter adapter = new AssetsAdapter(assetsList, MainActivity.this);
            numFilteredAssets = adapter.getCount();
            listViewAssets.setAdapter(adapter);

            txtCountAssets.setText(numFilteredAssets+" assets from "+ numTotalAssets);
        }
    }

    public class AssetGroupTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-type", "application/json");
                conn.setDoOutput(false);

                StringBuilder stringBuilder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String content) {
            List<AssetGroup> assetGroupsList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    AssetGroup assetGroup = new AssetGroup();
                    assetGroup.setId(jsonObject.getInt("id"));
                    assetGroup.setName(jsonObject.getString("name"));
                    assetGroupsList.add(assetGroup);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AssetGroupSpinnerAdapter adapter = new AssetGroupSpinnerAdapter(MainActivity.this, assetGroupsList, 0);
            spnAssetGroup.setAdapter(adapter);
        }
    }

    public class DepartmentTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-type", "application/json");
                conn.setDoOutput(false);

                StringBuilder stringBuilder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String content) {
            List<Department> departmentList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Department department = new Department();
                    department.setId(jsonObject.getInt("id"));
                    department.setName(jsonObject.getString("name"));
                    departmentList.add(department);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DepartmentSpinnerAdapter adapter = new DepartmentSpinnerAdapter(MainActivity.this, departmentList, 0);
            spnDepartment.setAdapter(adapter);
        }
    }
}