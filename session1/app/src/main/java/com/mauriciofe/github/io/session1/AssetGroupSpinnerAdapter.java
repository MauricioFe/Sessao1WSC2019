package com.mauriciofe.github.io.session1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mauriciofe.github.io.session1.models.AssetGroup;
import com.mauriciofe.github.io.session1.models.Department;

import java.util.List;

public class AssetGroupSpinnerAdapter extends ArrayAdapter<AssetGroup> {
    private  List<AssetGroup> assetGroupList;
    public AssetGroupSpinnerAdapter(@NonNull Context context, List<AssetGroup> assetGroupList, int resource) {
        super(context, resource, assetGroupList);
        this.assetGroupList = assetGroupList;
    }

    @Override
    public long getItemId(int position) {
        return assetGroupList.get(position).getId();
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
        AssetGroup assetGroup = (AssetGroup) getItem(position);
        txtSpinner.setText(assetGroup.getName());
        return convertView;
    }
}
