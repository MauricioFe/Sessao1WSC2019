package com.mauriciofe.github.io.session1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mauriciofe.github.io.session1.models.AssetTransferLogs;

import java.util.List;

public class TransferHistoryAdapter extends RecyclerView.Adapter<TransferHistoryAdapter.TransferHistoryViewHolder> {
    private Context context;
    private List<AssetTransferLogs> assetTransferLogsList;

    public TransferHistoryAdapter(Context context, List<AssetTransferLogs> assetTransferLogsList) {
        this.context = context;
        this.assetTransferLogsList = assetTransferLogsList;
    }

    @NonNull
    @Override//layout
    public TransferHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
        return new TransferHistoryViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransferHistoryViewHolder holder, int position) {
        //vinculo os dados do dataset para a view
        AssetTransferLogs assetTransferLogs = assetTransferLogsList.get(position);
        holder.vincula(assetTransferLogs);
    }

    @Override
    public int getItemCount() {
        return assetTransferLogsList.size();
    }

    public void atualizaList(List<AssetTransferLogs> logsList) {
        this.assetTransferLogsList = logsList;
        notifyDataSetChanged();
    }

    public class TransferHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtRelocationDate;
        TextView txtToDepartment;
        TextView txtToAssetSn;
        TextView txtFromDepartment;
        TextView txtFromAssetSn;

        public TransferHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRelocationDate = itemView.findViewById(R.id.item_txtRelocationDate_result);
            txtToDepartment = itemView.findViewById(R.id.item_txtOldDepartment);
            txtToAssetSn = itemView.findViewById(R.id.item_old_assetSn);
            txtFromAssetSn = itemView.findViewById(R.id.item_txt_newAssetSn);
            txtFromDepartment = itemView.findViewById(R.id.item_txtNewDepartment);
        }

        public void vincula(AssetTransferLogs assetTransferLogs) {
            txtRelocationDate.setText(assetTransferLogs.getTranseferDate());
            txtToDepartment.setText(assetTransferLogs.getToDepartment());
            txtToAssetSn.setText(assetTransferLogs.getToAssetSN());
            txtFromAssetSn.setText(assetTransferLogs.getFromAssetSN());
            txtFromDepartment.setText(assetTransferLogs.getFromDepartment());
        }
    }
}
