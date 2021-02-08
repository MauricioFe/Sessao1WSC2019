package com.mauriciofe.github.io.session1;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mauriciofe.github.io.session1.models.Assets;

import java.util.ArrayList;
import java.util.List;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.AssetsViewHolder> {
    private List<Assets> assetsList = new ArrayList<>();
    private Context context;

    public AssetsAdapter(List<Assets> assetsList, Context context) {
        this.assetsList = assetsList;
        this.context = context;
    }


    @NonNull
    @Override
    public AssetsAdapter.AssetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.assets_list_item, parent, false);
        return new AssetsViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetsAdapter.AssetsViewHolder holder, int position) {
        Assets assets = assetsList.get(position);
        holder.vincula(assets);
    }

    @Override
    public long getItemId(int position) {
        return assetsList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return assetsList.size();
    }

    public void atualizaList(List<Assets> assetsList) {
        this.assetsList = assetsList;
        notifyDataSetChanged();
    }

    public class AssetsViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtAssetName;
        private final TextView txtAssetSN;
        private final TextView txtDepartment;
        private final ImageView imgAssetImage;
        private final ImageButton imgEdit;
        private final ImageButton imgTrasnfer;
        private final ImageButton imgHistory;
        //private Assets assets;

        public AssetsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAssetName = itemView.findViewById(R.id.txtAssetName);
            txtAssetSN = itemView.findViewById(R.id.txtAssetSN);
            txtDepartment = itemView.findViewById(R.id.txtDepartment);
            imgAssetImage = itemView.findViewById(R.id.imgAssetImage);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgTrasnfer = itemView.findViewById(R.id.imgTransfer);
            imgHistory = itemView.findViewById(R.id.imgHistory);
            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == imgEdit.getId()) {
                        Toast.makeText(v.getContext(), "Editar", Toast.LENGTH_LONG).show();
                    }
                }
            });
            if (imgTrasnfer != null && imgHistory != null) {
                imgTrasnfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == imgTrasnfer.getId()) {
                            Toast.makeText(v.getContext(), "Transferindo ativo", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                imgHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == imgHistory.getId()) {
                            Toast.makeText(v.getContext(), "histórico", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

        private void vincula(Assets assets) {
            txtAssetName.setText(assets.getAssetName());
            txtAssetSN.setText(assets.getAssetSn());
            if (txtDepartment != null)
                txtDepartment.setText(assets.getNameDescription());
        }
    }

}
