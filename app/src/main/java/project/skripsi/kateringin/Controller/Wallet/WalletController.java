package project.skripsi.kateringin.Controller.Wallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Wallet.TopUp.TopUpController;
import project.skripsi.kateringin.Controller.Wallet.Withdraw.ListOfBankAccountController;
import project.skripsi.kateringin.Model.WalletHistory;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.WalletRecyclerviewAdapter;
import project.skripsi.kateringin.Util.IdrFormat;

public class WalletController extends AppCompatActivity {

    //XML
    TextView balanceTV, viewAllWalletHistory;
    AppCompatButton topUpButton, withdrawButton;
    FirebaseFirestore database;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    Toolbar toolbar;

    //FIELD
    int balance;
    ArrayList<WalletHistory> walletHistories = new ArrayList<>();
    WalletRecyclerviewAdapter walletRecyclerviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_view);
        binding();
        setField();
        button();
        readWalletHistoryData(this::walletHistoryAdapter);

    }

    private void button() {
        topUpButton.setOnClickListener(v ->{
            Intent intent = new Intent(this, TopUpController.class);
            intent.putExtra("CURRENT_BALANCE", balance);
            startActivity(intent);
        });

        viewAllWalletHistory.setOnClickListener(v ->{
            Intent intent = new Intent(this, WalletHistoryController.class);
            startActivity(intent);
        });

        withdrawButton.setOnClickListener(v ->{
            Intent intent = new Intent(this, ListOfBankAccountController.class);
            startActivity(intent);
        });
    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //GET WALLET BALANCE
        CollectionReference collectionReference = database.collection("wallet");
        Query query = collectionReference.whereEqualTo("userId", mAuth.getCurrentUser().getUid());

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    balance = document.getLong("balance").intValue();
                                    balanceTV.setText(IdrFormat.format(balance));
                                }else {
                                    System.out.println("No such document");
                                }
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void binding() {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.wallet_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        balanceTV = findViewById(R.id.wallet_balance_text);
        topUpButton = findViewById(R.id.wallet_top_up_button);
        withdrawButton = findViewById(R.id.wallet_withdraw_button);
        viewAllWalletHistory = findViewById(R.id.wallet_view_all_button);
        toolbar = findViewById(R.id.wallet_toolbar);
    }

    public void walletHistoryAdapter(ArrayList<WalletHistory> list){
//        if(list.isEmpty()){
//            orderWarning.setVisibility(View.VISIBLE);
//        }else{
//            orderWarning.setVisibility(View.GONE);
//        }

        walletRecyclerviewAdapter = new WalletRecyclerviewAdapter(list,this);
        recyclerView.setAdapter(walletRecyclerviewAdapter);
    }

    private void readWalletHistoryData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("walletHistory");
        Query query = collectionRef
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Direction.DESCENDING)
                .limit(3);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String userId = document.getString("userId");
                    String date = document.getString("date");
                    String transactionType = document.getString("transactionType");
                    int amount = document.getLong("amount").intValue();
                    Log.d("TAG", "readWalletHistoryData: " + userId + date + transactionType + amount);
                    walletHistories.add(new WalletHistory(
                            userId,
                            transactionType,
                            amount,
                            date
                    ));
                }
                firestoreCallback.onCallback(walletHistories);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private interface FirestoreCallback{
        void onCallback(ArrayList<WalletHistory> list);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}