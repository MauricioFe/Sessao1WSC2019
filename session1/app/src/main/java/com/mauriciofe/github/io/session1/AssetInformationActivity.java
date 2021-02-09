package com.mauriciofe.github.io.session1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mauriciofe.github.io.session1.models.AssetGroup;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AssetInformationActivity extends AppCompatActivity {
    Spinner spnDepartment;
    Spinner spnLocation;
    Spinner spnAssetGroup;
    Spinner spnAccountable;
    private long departmentId;
    private long locationId;
    private long AssetGroupId;
    private long accounableId;
    private static final String BASE_URL = "http://192.168.0.104:5000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_information);
        spnDepartment = findViewById(R.id.spn_department);
        spnLocation = findViewById(R.id.spnLocation);
        spnAssetGroup = findViewById(R.id.spnAsset_group);
        spnAccountable = findViewById(R.id.spnAccountable);

        DepartmentGetTask departmentGetTask = new DepartmentGetTask();
        departmentGetTask.execute(BASE_URL+"/api/departments");
        LocationGetTask locationGetTask = new LocationGetTask();
        locationGetTask.execute(BASE_URL+"/api/location");
        AssetGroupTask assetGroupGetTask = new AssetGroupTask();
        assetGroupGetTask.execute(BASE_URL+"/api/AssetGroups");
        AccountableTask accountableGetTask = new AccountableTask();
        accountableGetTask.execute(BASE_URL+"/api/Employees");

    }

    public class DepartmentGetTask extends AsyncTask<String, String, String> {

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
                    new ArrayAdapter<String>(AssetInformationActivity.this,
                            android.R.layout.simple_spinner_item, departmentNameList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spnDepartment != null) {
                spnDepartment.setAdapter(adapter);

                spnDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        departmentId = departmentIdList.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        departmentId = 0;
                    }
                });
            }
        }
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
                    new ArrayAdapter<String>(AssetInformationActivity.this,
                            android.R.layout.simple_spinner_item, assetGroupsNameList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spnAssetGroup != null) {
                spnAssetGroup.setAdapter(adapter);
                spnAssetGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        AssetGroupId = assetGroupsIdList.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        AssetGroupId = 0;
                    }
                });
            }
        }

    }
}

