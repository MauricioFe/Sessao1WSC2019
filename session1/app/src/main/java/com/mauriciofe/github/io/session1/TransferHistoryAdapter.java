package com.mauriciofe.github.io.session1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mauriciofe.github.io.session1.models.AssetTranferLogs;

import java.util.List;

public class TransferHistoryAdapter extends RecyclerView.Adapter<TransferHistoryAdapter.TransferHistoryViewHolder> {
    private Context context;
    private List<AssetTranferLogs> assetTranferLogsList;

    public TransferHistoryAdapter(Context context, List<AssetTranferLogs> assetTranferLogsList) {
        this.context = context;
        this.assetTranferLogsList = assetTranferLogsList;
    }

    @NonNull
    @Override
    public TransferHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
        return new TransferHistoryViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransferHistoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class TransferHistoryViewHolder extends RecyclerView.ViewHolder {
        public TransferHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
