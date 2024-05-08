package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import project.skripsi.kateringin.Model.WalletHistory;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class WalletRecyclerviewAdapter extends RecyclerView.Adapter<WalletRecyclerviewAdapter.ViewHolder> {

    private ArrayList<WalletHistory> walletHistories;
    private OnClickListener onClickListener;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    public WalletRecyclerviewAdapter(ArrayList<WalletHistory> walletHistories, Context context) {
        this.walletHistories = walletHistories;
        this.context = context;
    }

    @NonNull
    @Override
    public WalletRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_wallet_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull WalletRecyclerviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        final WalletHistory walletHistory = walletHistories.get(position);
        String inputDate = walletHistory.getDate();

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);

        try {
            Date date = inputFormat.parse(inputDate);
            holder.date.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (walletHistory.getTransactionType()){
            case "top-up":
                holder.amount.setText("+ " + IdrFormat.format(walletHistory.getAmount()));
                break;
            case "settlement":
                holder.amount.setText("- " + IdrFormat.format(walletHistory.getAmount()));
                break;
            case "withdraw":
                holder.amount.setText("- " + IdrFormat.format(walletHistory.getAmount()));
                break;
            case "refund":
                holder.amount.setText("+ " + IdrFormat.format(walletHistory.getAmount()));
                break;
        }
        holder.transactionType.setText("Transaction Type : " + walletHistory.getTransactionType());
        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, walletHistory);
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, WalletHistory model);
    }

    @Override
    public int getItemCount() {
        return walletHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, amount, transactionType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.wallet_item_date_text);
            amount = itemView.findViewById(R.id.wallet_item_value_text);
            transactionType = itemView.findViewById(R.id.wallet_item_type_text);
        }
    }
}
