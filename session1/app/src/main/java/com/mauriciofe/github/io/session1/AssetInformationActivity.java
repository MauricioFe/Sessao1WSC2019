package com.mauriciofe.github.io.session1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mauriciofe.github.io.session1.models.Assets;
import com.mauriciofe.github.io.session1.models.Employee;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    Button btnCancel;
    Button btnCaptureImage;
    Button btnBrowse;
    private long departmentId;
    private long locationId;
    private long assetGroupId;
    private long accounableId;
    List<Assets> assetSnList = new ArrayList<>();
    List<Bitmap> bitmapList = new ArrayList<>();
    ImagesRecyclerViewAdapter mAdapter;
    RecyclerView recyclerListImages;
    private static final String BASE_URL = "http://192.168.0.105:5000/api/";
    String departmentIdStr;
    String assetGroupIdStr;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_BROWSE_IMAGE = 2;
    List<String> assetGroupsNameList = new ArrayList<>();
    List<Integer> assetGroupsIdList = new ArrayList<>();
    List<String> departmentNameList = new ArrayList<>();
    List<Integer> departmentIdList = new ArrayList<>();
    List<Integer> locationListId = new ArrayList<>();
    List<String> locationListName = new ArrayList<>();
    List<Integer> employeeListId = new ArrayList<>();
    List<String> employeeListName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_information);
        inicializaCampos();
        configuraRecyclerView();
        preencheSpinnerAssetGroups();
        preencheSpinnerDepartment();
        preencheSpinnerLocation();
        preencheSpinnerAccountable();
        abreCamera();
        abreGaleria();
        Intent receiverAssetIntent = getIntent();
        long assetID = receiverAssetIntent.getLongExtra("assetId", 0);
        preencheCamposParaEdicao(assetID);
        cancelarAcao();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAssetName.getText().length() > 0 && locationId > 0 && departmentId > 0 && assetGroupId > 0
                        && accounableId > 0)
                    if (assetID <= 0)
                        verificaAssetSn(BASE_URL + "assets/assetName?assetName=" + edtAssetName.getText() + "&locationID=" + locationId);
                    else
                        editaAsset(assetID);
                else
                    new AlertDialog.Builder(AssetInformationActivity.this)
                            .setTitle("Erro ao tentar inserir")
                            .setMessage("Não é possível inserir um ativo de mesmo nome no mesmo local")
                            .setNeutralButton("OK", null)
                            .show();
            }
        });
    }

    private void preencheCamposParaEdicao(long assetID) {
        if (assetID > 0) {
            MyAsyncTask.requestApi(BASE_URL + "assets/" + assetID, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
                @Override
                public void onComplete(String result) {
                    spnAssetGroup.setEnabled(false);
                    spnDepartment.setEnabled(false);
                    spnLocation.setEnabled(false);
                    try {
                        int positionAssetGroup = assetGroupsIdList.indexOf(new JSONObject(result).getInt("assetGroupId"));
                        int positionDepartment = departmentIdList.indexOf(new JSONObject(result).getInt("departmentId"));
                        int positionLocation = locationListId.indexOf(new JSONObject(result).getInt("locationId"));
                        int positionEmployee = employeeListId.indexOf(new JSONObject(result).getInt("employeeId"));
                        spnDepartment.setSelection(positionDepartment);
                        spnAssetGroup.setSelection(positionAssetGroup);
                        spnLocation.setSelection(positionLocation);
                        spnAccountable.setSelection(positionEmployee);
                        edtAssetName.setText(new JSONObject(result).getString("assetName"));
                        txtAssetSN.setText(new JSONObject(result).getString("assetSn"));
                        edtDescription.setText(new JSONObject(result).getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void cancelarAcao() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssetInformationActivity.this, MainActivity.class));
            }
        });
    }

    private void verificaAssetSn(String uri) {
        MyAsyncTask.requestApi(uri, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
            @Override
            public void onComplete(String result) {
                try {
                    if (new JSONObject(result).getInt("quantidadeRegistros") == 0)
                        insereNewAsset();
                    else
                        new AlertDialog.Builder(AssetInformationActivity.this)
                                .setTitle("Erro ao tentar inserir")
                                .setMessage("Não é possível inserir um ativo de mesmo nome no mesmo local")
                                .setNeutralButton("OK", null)
                                .show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void editaAsset(long assetID) {
        JSONStringer js = new JSONStringer();
        try {
            js.object();
            js.key("assetName").value(edtAssetName.getText());
            js.key("employeeId").value(accounableId);
            js.key("description").value(edtDescription.getText());
            if (edtWarrantyDate.getText().length() == 0) {
                js.key("warrantyDate").value(null);
            } else {
                js.key("warrantyDate").value(edtWarrantyDate.getText());
            }
            js.endObject();

            MyAsyncTask.requestApi(BASE_URL + "assets/" + assetID, MyAsyncTask.METHOD_PUT, js.toString(), new Callback<String>() {
                @Override
                public void onComplete(String result) {
                    if (result != null) {
                        startActivity(new Intent(AssetInformationActivity.this, MainActivity.class));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insereNewAsset() {
        MyAsyncTask.requestApi(BASE_URL + "departmentLocation/" + departmentId + "/" + locationId, MyAsyncTask.METHOD_GET, null, new Callback<String>() {
            @Override
            public void onComplete(String result) {
                if (result != null) {
                    JSONStringer js = new JSONStringer();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        js.object();
                        js.key("assetSn").value(txtAssetSN.getText());
                        js.key("assetName").value(edtAssetName.getText());
                        js.key("departmentLocationId").value(jsonObject.getInt("id"));
                        js.key("employeeId").value(accounableId);
                        js.key("assetGroupId").value(assetGroupId);
                        js.key("description").value(edtDescription.getText());
                        if (edtWarrantyDate.getText().length() == 0) {
                            js.key("warrantyDate").value(null);
                        } else {
                            js.key("warrantyDate").value(edtWarrantyDate.getText());
                        }
                        js.endObject();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MyAsyncTask.requestApi(BASE_URL + "assets", MyAsyncTask.METHOD_POST, js.toString(), new Callback<String>() {
                        @Override
                        public void onComplete(String result) {
                            if (bitmapList.size() > 0) {
                                MyAsyncTask.requestApi(BASE_URL + "assets/last", MyAsyncTask.METHOD_GET, null, new Callback<String>() {
                                    @Override
                                    public void onComplete(String result) {
                                        if (result != null) {
                                            try {
                                                insereAssetsPhotos(BASE_URL + "assetPhoto", new JSONObject(result).getInt("id"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            new AlertDialog.Builder(AssetInformationActivity.this).setTitle("Erro ao enviar cadastro")
                                                    .setMessage("Erro ao realizar cadastro tente novamente mais tarde")
                                                    .setNeutralButton("OK", null).show();
                                        }
                                    }
                                });
                            } else {
                                startActivity(new Intent(AssetInformationActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void insereAssetsPhotos(String uri, int assetId) {
        for (Bitmap item : bitmapList) {
            JSONStringer js = new JSONStringer();
            try {
                js.object();
                js.key("assetId").value(assetId);
                js.key("assetPhoto").value(FileUtil.convertBitmapToArrayByte(item));
                js.endObject();
                MyAsyncTask.requestApi(uri, MyAsyncTask.METHOD_POST, js.toString(), new Callback<String>() {
                    @Override
                    public void onComplete(String result) {
                        startActivity(new Intent(AssetInformationActivity.this, MainActivity.class));
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void insereNewDepartmentLocation(String uri) {

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
            startActivity(new Intent(AssetInformationActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
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
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBrowse = findViewById(R.id.btnBrowse);
        btnCancel = findViewById(R.id.btnCancel);
        recyclerListImages = findViewById(R.id.recyclerViewPhoto);
    }

    private void abreGaleria() {
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AssetInformationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    carregarFotoGaleriaIntent();
                else
                    ActivityCompat.requestPermissions(AssetInformationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_BROWSE_IMAGE);
            }
        });
    }

    private void abreCamera() {
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AssetInformationActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    tirarFotoIntent();
                else {
                    ActivityCompat.requestPermissions(AssetInformationActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    private void configuraRecyclerView() {
        mAdapter = new ImagesRecyclerViewAdapter(bitmapList, this);
        recyclerListImages.setLayoutManager(new LinearLayoutManager(AssetInformationActivity.this));
        recyclerListImages.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            preencheListaBitmap(data);
        }
        if (requestCode == REQUEST_BROWSE_IMAGE && resultCode == RESULT_OK) {
            MyAsyncTask.openNewThreadSync(preencheListaBitmapFromGallery(data.getData()), new Callback<Bitmap>() {
                @Override
                public void onComplete(Bitmap result) {
                    bitmapList.add(result);
                    mAdapter.atualizaLista(bitmapList);
                }
            });
        }
    }

    private Bitmap preencheListaBitmapFromGallery(Uri data) {
        try {
            return BitmapFactory.decodeStream(this.getContentResolver().openInputStream(data));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void preencheListaBitmap(@Nullable Intent data) {
        assert data != null;
        Bundle extras = data.getExtras();
        Bitmap bitmap = getPictureImage(extras);
        bitmapList.add(bitmap);
        mAdapter.atualizaLista(bitmapList);
    }

    private Bitmap getPictureImage(Bundle extras) {
        return (Bitmap) extras.get("data");
    }

    private void tirarFotoIntent() {
        Intent tirarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tirarFotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(tirarFotoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void carregarFotoGaleriaIntent() {
        Intent carregarFortoGaleriaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        if (carregarFortoGaleriaIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(carregarFortoGaleriaIntent, "Selecione uma imagem"), REQUEST_BROWSE_IMAGE);
        }
    }

    private void preencheSpinnerDepartment() {

        MyAsyncTask.requestApi(BASE_URL + "departments", MyAsyncTask.METHOD_GET, null, new Callback<String>() {

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
        MyAsyncTask.requestApi(BASE_URL + "assets/search?assetSn=" + departmentIdStr + "/" + assetGroupIdStr,
                MyAsyncTask.METHOD_GET, null, new Callback<String>() {
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
        MyAsyncTask.requestApi(BASE_URL + "location", MyAsyncTask.METHOD_GET, null, new Callback<String>() {
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
        employeeListId.add(0);
        employeeListName.add("Accontable Party");
        MyAsyncTask.requestApi(BASE_URL + "Employees", MyAsyncTask.METHOD_GET, null, new Callback<String>() {
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

        assetGroupsIdList.add(0);
        assetGroupsNameList.add("Asset Group");
        MyAsyncTask.requestApi(BASE_URL + "assetGroups", MyAsyncTask.METHOD_GET, null, new Callback<String>() {
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


}

