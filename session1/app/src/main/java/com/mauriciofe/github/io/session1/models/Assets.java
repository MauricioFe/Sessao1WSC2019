package com.mauriciofe.github.io.session1.models;

import java.util.Date;

public class Assets {
    public int Id;
    public String AssetSn;
    public String AssetName;
    public int DepartmentLocationId;
    public int EmployeeId;
    public int AssetGroupId;
    public String Description;
    public Date WarrantyDate;
    public String NameDescription;
    public int DepartmentId;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAssetSn() {
        return AssetSn;
    }

    public void setAssetSn(String assetSn) {
        AssetSn = assetSn;
    }

    public String getAssetName() {
        return AssetName;
    }

    public void setAssetName(String assetName) {
        AssetName = assetName;
    }

    public int getDepartmentLocationId() {
        return DepartmentLocationId;
    }

    public void setDepartmentLocationId(int departmentLocationId) {
        DepartmentLocationId = departmentLocationId;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public int getAssetGroupId() {
        return AssetGroupId;
    }

    public void setAssetGroupId(int assetGroupId) {
        AssetGroupId = assetGroupId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Date getWarrantyDate() {
        return WarrantyDate;
    }

    public void setWarrantyDate(Date warrantyDate) {
        WarrantyDate = warrantyDate;
    }

    public String getNameDescription() {
        return NameDescription;
    }

    public void setNameDescription(String nameDescription) {
        NameDescription = nameDescription;
    }

    public int getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(int departmentId) {
        DepartmentId = departmentId;
    }
}
