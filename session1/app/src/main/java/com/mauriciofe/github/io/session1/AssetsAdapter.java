package com.mauriciofe.github.io.session1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mauriciofe.github.io.session1.models.Assets;

import java.util.ArrayList;
import java.util.List;

public class AssetsAdapter extends BaseAdapter {
    private List<Assets> assetsList = new ArrayList<>();
    private Context context;

    public AssetsAdapter(List<Assets> assetsList, Context context) {
        this.assetsList = assetsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return assetsList.size();
    }

    @Override
    public Object getItem(int position) {
        return assetsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return assetsList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.assets_list_item, parent, false);
        }
        TextView txtAssetName = convertView.findViewById(R.id.txtAssetName);
        TextView txtAssetSN = convertView.findViewById(R.id.txtAssetSN);
        TextView txtDepartment = convertView.findViewById(R.id.txtDepartment);
        ImageView imgAssetImage = convertView.findViewById(R.id.imgAssetImage);
        ImageView imgEdit = convertView.findViewById(R.id.imgEdit);
        ImageView imgTransfer = convertView.findViewById(R.id.imgTransfer);
        ImageView imgHistory = convertView.findViewById(R.id.imgHistory);
        Assets assets = (Assets) getItem(position);
        txtAssetName.setText(assets.getAssetName());
        txtAssetSN.setText(assets.getAssetSn());
        txtDepartment.setText(assets.getNameDescription());
        return convertView;
    }
}
