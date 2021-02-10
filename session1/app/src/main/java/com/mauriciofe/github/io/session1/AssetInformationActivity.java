package com.mauriciofe.github.io.session1;

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
    private static final String BASE_URL = "http://192.168.0.104:5000";
    String departmentIdStr;
    String assetGroupIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_information);
        inicializaCampos();
        LocationGetTask locationGetTask = new LocationGetTask();
        locationGetTask.execute(BASE_URL + "/api/location");
        AssetGroupTask assetGroupGetTask = new AssetGroupTask();
        assetGroupGetTask.execute(BASE_URL + "/api/AssetGroups");
        AccountableTask accountableGetTask = new AccountableTask();
        accountableGetTask.execute(BASE_URL + "/api/Employees");
        preencheSpinnerDepartment();
    }

    private void preencheSpinnerDepartment() {
        List<String> departmentNameList = new ArrayList<>();
        List<Integer> departmentIdList = new ArrayList<>();
        ConfigConsumeApi.requestApi(BASE_URL + "/api/departments", new Callback<String>() {

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
                            AssetsTask task = new AssetsTask();
                            if (assetGroupId > 0 && departmentId > 0) {
                                departmentIdStr = String.valueOf(departmentId);
                                assetGroupIdStr = String.valueOf(assetGroupId);
                                departmentIdStr = departmentIdStr.length() < 2 ? "0" + departmentIdStr : departmentIdStr;
                                assetGroupIdStr = assetGroupIdStr.length() < 2 ? "0" + assetGroupIdStr : assetGroupIdStr;
                                task.execute(BASE_URL + "/api/assets/search?assetSn=" + departmentIdStr + "/" + assetGroupIdStr);
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


    public class LocationGetTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "Application/json");
                conn.setRequestMethod("GET");
                conn.setDoOutput(false);

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
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
            List<Location> locationList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Location location = new Location();
                    location.setId(jsonObject.getInt("id"));
                    location.setName(jsonObject.getString("name"));
                    locationList.add(location);
                }
                List<Integer> locationListId = new ArrayList<>();
                List<String> locationListName = new ArrayList<>();
                locationListId.add(0);
                locationListName.add("Locations");
                for (Location item : locationList) {
                    locationListId.add(item.getId());
                    locationListName.add(item.getName());
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class AccountableTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "Application/json");
                conn.setRequestMethod("GET");
                conn.setDoOutput(false);

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
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
            List<Employee> employeeList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Employee employee = new Employee(jsonObject.getInt("id"), jsonObject.getString("firstName"), jsonObject.getString("lastName"), jsonObject.getString("phone"));
                    employeeList.add(employee);
                }
                List<Integer> employeeListId = new ArrayList<>();
                List<String> employeeListName = new ArrayList<>();
                employeeListId.add(0);
                employeeListName.add("Accontable Party");
                for (Employee item : employeeList) {
                    employeeListId.add(item.getId());
                    employeeListName.add(item.getFirstName() + " " + item.getLastName());
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
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class AssetGroupTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return getDataGeneric(params[0]);
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
                    new ArrayAdapter<String>(AssetInformationActivity.this,
                            android.R.layout.simple_spinner_item, assetGroupsNameList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spnAssetGroup != null) {
                spnAssetGroup.setAdapter(adapter);
                spnAssetGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        assetGroupId = assetGroupsIdList.get(position);
                        AssetsTask task = new AssetsTask();

                        if (assetGroupId > 0 && departmentId > 0) {
                            departmentIdStr = String.valueOf(departmentId);
                            assetGroupIdStr = String.valueOf(assetGroupId);
                            departmentIdStr = departmentIdStr.length() < 2 ? "0" + departmentIdStr : departmentIdStr;
                            assetGroupIdStr = assetGroupIdStr.length() < 2 ? "0" + assetGroupIdStr : assetGroupIdStr;
                            task.execute(BASE_URL + "/api/assets/search?assetSn=" + departmentIdStr + "/" + assetGroupIdStr);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        assetGroupId = 0;
                    }
                });
            }
        }

    }

    public class AssetsTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return getDataGeneric(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            List<Assets> assetsList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Assets assets = new Assets();
                    assets.setAssetSn(jsonObject.getString("assetSn"));
                    assetsList.add(assets);
                }
                String assetSn = null;
                String lastAssetSn = null;
                for (Assets item : assetsList) {
                    lastAssetSn = item.AssetSn;
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
    }

    private String getDataGeneric(String uri) {
        BufferedReader reader;
        try {
            URL url = new URL(uri);
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

}

