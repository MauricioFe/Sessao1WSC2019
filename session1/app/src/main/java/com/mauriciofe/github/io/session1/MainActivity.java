package com.mauriciofe.github.io.session1;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.0.104:5000";
    Spinner spnDepartment;
    Spinner spnAssetGroup;
    EditText edtStartDate;
    EditText edtEndDate;
    EditText edtSearch;
    TextView txtCountAssets;
    RecyclerView recyclerViewAssets;
    public long assetGroupId;
    public long departmentId;
    public int numTotalAssets;
    public int numFilteredAssets;
    int i = 0;
    List<Assets> assetsListTotal = new ArrayList<>();
    String assetsListString;
    AssetsAdapter adapter;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("teste", assetsListString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializandoViews();
        adapter = new AssetsAdapter(assetsListTotal, MainActivity.this);
        recyclerViewAssets.setAdapter(adapter);
        recyclerViewAssets.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        AssetsTask task = new AssetsTask();
        task.execute(BASE_URL + "/api/assets");
        AssetGroupTask taskGroup = new AssetGroupTask();
        DepartmentTask taskDepartment = new DepartmentTask();
        taskGroup.execute(BASE_URL + "/api/AssetGroups");
        taskDepartment.execute(BASE_URL + "/api/Departments");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (savedInstanceState != null) {
                    assetsListString = savedInstanceState.getString("teste");
                    if (assetsListString != null) {
                        adapter.atualizaList(convertJsonToAssets(assetsListString));
                        numFilteredAssets = adapter.getItemCount();
                        txtCountAssets.setText(numFilteredAssets + " assets from " + numTotalAssets);
                    }
                }
            }
        }, 1000);

        if (edtSearch != null && edtEndDate != null && spnAssetGroup != null && edtStartDate != null && spnDepartment != null) {
            edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        buscaPorFiltros();
                    }
                }
            });

            edtEndDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus && edtStartDate.getText().length() > 0) {
                        buscaPorFiltros();
                    }
                    if (hasFocus) {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(edtEndDate.getWindowToken(), 0);
                        openDatePickerDialog(edtEndDate);
                    }
                }
            });
            edtEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDatePickerDialog(edtEndDate);
                }
            });
            edtStartDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && edtEndDate.getText().length() > 0) {
                        buscaPorFiltros();
                    }
                    if (hasFocus)
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(edtStartDate.getWindowToken(), 0);
                    openDatePickerDialog(edtStartDate);
                }
            });
            edtStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDatePickerDialog(edtStartDate);
                }
            });
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (edtSearch.getText().length() > 2) {
                        buscaPorFiltros();
                    } else if (edtSearch.getText().length() == 0) {
                        AssetsTask taskFilter = new AssetsTask();
                        taskFilter.execute(BASE_URL + "/api/assets");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (edtSearch.getText().length() > 2) {
                        buscaPorFiltros();
                    } else {
                        AssetsTask taskFilter = new AssetsTask();
                        taskFilter.execute(BASE_URL + "/api/assets");
                    }
                    return true;
                }
            });
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AssetInformationActivity.class));
            }
        });
    }

    private void openDatePickerDialog(EditText editText) {

        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editText.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void buscaPorFiltros() {
        String urlFilter = BASE_URL + "/api/assets/filter?search=" + edtSearch.getText().toString() +
                "&assetGroupId=" + assetGroupId + "&departmentId=" + departmentId +
                "&startDate=" + edtStartDate.getText().toString() + "&endDate=" + edtEndDate.getText().toString();
        AssetsTask taskFilter = new AssetsTask();
        taskFilter.execute(urlFilter);
    }


    private void inicializandoViews() {
        spnDepartment = findViewById(R.id.main_spn_department);
        spnAssetGroup = findViewById(R.id.main_spn_asset_group);
        edtStartDate = findViewById(R.id.editStartDate);
        edtEndDate = findViewById(R.id.editEndDate);
        edtSearch = findViewById(R.id.editTextSearch);
        recyclerViewAssets = findViewById(R.id.recycleViewAssets);
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
            List<Assets> assetsList = convertJsonToAssets(content);
            assetsListString = content;
            if (i == 0) {
                numTotalAssets = assetsList.size();
                i++;
            }
            adapter.atualizaList(assetsList);
            numFilteredAssets = adapter.getItemCount();
            txtCountAssets.setText(numFilteredAssets + " assets from " + numTotalAssets);
        }
    }

    private List<Assets> convertJsonToAssets(String content) {
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
            return assetsList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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

            List<String> assetGroupsNameList = new ArrayList<>();
            List<Integer> assetGroupsIdList = new ArrayList<>();
            assetGroupsIdList.add(0);
            assetGroupsNameList.add("Asset Group");
            for (AssetGroup item : assetGroupsList) {
                assetGroupsNameList.add(item.getName());
                assetGroupsIdList.add(item.getId());
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item, assetGroupsNameList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spnAssetGroup != null) {
                spnAssetGroup.setAdapter(adapter);
                spnAssetGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        assetGroupId = assetGroupsIdList.get(position);
                        if (assetGroupId > 0)
                            buscaPorFiltros();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        assetGroupId = 0;
                    }
                });
            }
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
            List<String> departmentNameList = new ArrayList<>();
            List<Integer> departmentIdList = new ArrayList<>();
            departmentIdList.add(0);
            departmentNameList.add("Department");
            for (Department item : departmentList) {
                departmentNameList.add(item.getName());
                departmentIdList.add(item.getId());
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item, departmentNameList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spnDepartment != null) {
                spnDepartment.setAdapter(adapter);

                spnDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        departmentId = departmentIdList.get(position);
                        if (departmentId > 0)
                            buscaPorFiltros();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        departmentId = 0;
                    }
                });
            }
        }
    }
}