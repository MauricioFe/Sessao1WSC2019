package com.mauriciofe.github.io.session1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    @Override//layout
    public TransferHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
        return new TransferHistoryViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransferHistoryViewHolder holder, int position) {
        //vinculo os dados do dataset para a view
        AssetTranferLogs assetTranferLogs = assetTranferLogsList.get(position);
        holder.vincula(assetTranferLogs);
    }

    @Override
    public int getItemCount() {
        return assetTranferLogsList.size();
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

        public void vincula(AssetTranferLogs assetTranferLogs) {
            txtRelocationDate.setText(assetTranferLogs.getTranseferDate());
            txtToDepartment.setText(assetTranferLogs.getToDepartment());
            txtToAssetSn.setText(assetTranferLogs.getToAssetSN());
            txtFromAssetSn.setText(assetTranferLogs.getFromAssetSN());
            txtFromDepartment.setText(assetTranferLogs.getFromDepartment());
        }
    }
}
