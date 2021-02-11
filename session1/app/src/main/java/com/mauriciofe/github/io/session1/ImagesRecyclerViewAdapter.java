package com.mauriciofe.github.io.session1;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mauriciofe.github.io.session1.models.AssetPhotos;

import java.util.List;

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesRecyclerViewAdapter.ImagesRecyclerViewHolder> {
    private List<AssetPhotos> assetPhotosList;
    private Context context;
    private Bitmap bitmap;

    public ImagesRecyclerViewAdapter(List<AssetPhotos> assetPhotosList, Context context, Bitmap bitmap) {
        this.assetPhotosList = assetPhotosList;
        this.context = context;
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public ImagesRecyclerViewAdapter.ImagesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_photo_assets, parent, false);
        return new ImagesRecyclerViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesRecyclerViewHolder holder, int position) {
        AssetPhotos assetPhotos = assetPhotosList.get(position);
        holder.vincula(assetPhotos);
    }

    @Override
    public int getItemCount() {
        return assetPhotosList.size();
    }

    public void atualizaLista(List<AssetPhotos> assetPhotosList) {
        this.assetPhotosList = assetPhotosList;
        notifyDataSetChanged();
    }

    public class ImagesRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeImage;
        ImageView imgAsset;

        public ImagesRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeImage = itemView.findViewById(R.id.list_images_txtnome);
            imgAsset = itemView.findViewById(R.id.list_images_assetPhoto);
        }

        public void vincula(AssetPhotos assetPhotos) {
            txtNomeImage.setText(assetPhotos.getId() + "_asset_" + assetPhotos.getAssetId());
            imgAsset.setImageBitmap(bitmap);
        }
    }
}
