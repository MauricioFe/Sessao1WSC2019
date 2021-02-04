package com.mauriciofe.github.io.session1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mauriciofe.github.io.session1.models.Department;

import java.util.List;

public class DepartmentSpinnerAdapter extends ArrayAdapter<Department> {
    private  List<Department> departmentList;
    public DepartmentSpinnerAdapter(@NonNull Context context, List<Department> departmentList, int resource) {
        super(context, resource, departmentList);
        this.departmentList = departmentList;
    }

    @Override
    public long getItemId(int position) {
        return departmentList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }
        TextView txtSpinner = convertView.findViewById(R.id.txtSpinnerItem);
        Department department = (Department) getItem(position);
        txtSpinner.setText(department.getName());
        return convertView;
    }
}
