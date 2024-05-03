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

import java.util.ArrayList;

import project.skripsi.kateringin.Model.UserBankAccount;
import project.skripsi.kateringin.Model.WalletHistory;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.IdrFormat;

public class BankAccountsAdapter extends RecyclerView.Adapter<BankAccountsAdapter.ViewHolder> {

    private ArrayList<UserBankAccount> bankAccounts;
    private OnClickListener onClickListener;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    public BankAccountsAdapter(ArrayList<UserBankAccount> bankAccounts, Context context) {
        this.bankAccounts = bankAccounts;
        this.context = context;
    }

    @NonNull
    @Override
    public BankAccountsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_bank_account_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull BankAccountsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        final UserBankAccount bankAccount = bankAccounts.get(position);
        holder.bankName.setText(bankAccount.getBankName());
        holder.bankNumber.setText(bankAccount.getBankNumber());
        holder.username.setText(bankAccount.getAccountHolderName());

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, bankAccount);
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, UserBankAccount model);
    }

    @Override
    public int getItemCount() {
        return bankAccounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView bankName, bankNumber, username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bankName = itemView.findViewById(R.id.bank_account_item_bank_name);
            bankNumber = itemView.findViewById(R.id.bank_account_item_bank_number);
            username = itemView.findViewById(R.id.bank_account_item_user_name);
        }
    }
}
