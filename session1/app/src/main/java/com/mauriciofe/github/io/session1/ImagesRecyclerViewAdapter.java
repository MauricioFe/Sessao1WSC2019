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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesRecyclerViewAdapter.ImagesRecyclerViewHolder> {
    private List<Bitmap> bitmapList;
    private Context context;

    public ImagesRecyclerViewAdapter(List<Bitmap> bitmapList, Context context) {
        this.bitmapList = bitmapList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImagesRecyclerViewAdapter.ImagesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_photo_assets, parent, false);
        return new ImagesRecyclerViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesRecyclerViewHolder holder, int position) {
        Bitmap bitmap = bitmapList.get(position);
        holder.vincula(bitmap);
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public void atualizaLista(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
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

        public void vincula(Bitmap assetPhoto) {
            txtNomeImage.setText("asset_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            imgAsset.setImageBitmap(assetPhoto);
        }
    }
}
