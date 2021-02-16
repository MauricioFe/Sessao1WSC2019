package com.mauriciofe.github.io.session1.models;

public class AssetTranferLogs {
    private int Id;
    private int AssetId;
    private String FromAssetSN;
    private String FromDepartment;
    private String ToAssetSN;
    private String ToDepartment;
    private int FromDepartmentLocationId;
    private int ToDepartmentLocationId;
    private String TranseferDate;

    public AssetTranferLogs(int id, int assetId, String fromAssetSN, String fromDepartment, String toAssetSN, String toDepartment, int fromDepartmentLocationId, int toDepartmentLocationId, String transeferDate) {
        Id = id;
        AssetId = assetId;
        FromAssetSN = fromAssetSN;
        FromDepartment = fromDepartment;
        ToAssetSN = toAssetSN;
        ToDepartment = toDepartment;
        FromDepartmentLocationId = fromDepartmentLocationId;
        ToDepartmentLocationId = toDepartmentLocationId;
        TranseferDate = transeferDate;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getAssetId() {
        return AssetId;
    }

    public void setAssetId(int assetId) {
        AssetId = assetId;
    }

    public String getFromAssetSN() {
        return FromAssetSN;
    }

    public void setFromAssetSN(String fromAssetSN) {
        FromAssetSN = fromAssetSN;
    }

    public String getFromDepartment() {
        return FromDepartment;
    }

    public void setFromDepartment(String fromDepartment) {
        FromDepartment = fromDepartment;
    }

    public String getToAssetSN() {
        return ToAssetSN;
    }

    public void setToAssetSN(String toAssetSN) {
        ToAssetSN = toAssetSN;
    }

    public String getToDepartment() {
        return ToDepartment;
    }

    public void setToDepartment(String toDepartment) {
        ToDepartment = toDepartment;
    }

    public int getFromDepartmentLocationId() {
        return FromDepartmentLocationId;
    }

    public void setFromDepartmentLocationId(int fromDepartmentLocationId) {
        FromDepartmentLocationId = fromDepartmentLocationId;
    }

    public int getToDepartmentLocationId() {
        return ToDepartmentLocationId;
    }

    public void setToDepartmentLocationId(int toDepartmentLocationId) {
        ToDepartmentLocationId = toDepartmentLocationId;
    }

    public String getTranseferDate() {
        return TranseferDate;
    }

    public void setTranseferDate(String transeferDate) {
        TranseferDate = transeferDate;
    }
}
