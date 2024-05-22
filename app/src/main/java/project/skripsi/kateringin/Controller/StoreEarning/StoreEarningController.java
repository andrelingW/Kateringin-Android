package project.skripsi.kateringin.Controller.StoreEarning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.Model.WalletHistory;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.StoreWalletRecyclerviewAdapter;
import project.skripsi.kateringin.Recycler.WalletRecyclerviewAdapter;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class StoreEarningController extends AppCompatActivity {

    //XML
    TextView earningTextView, viewAllWithdrawHistory;
    AppCompatButton withdrawButton;
    FirebaseFirestore database;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    ConstraintLayout walletWarning;

    //FIELD
    int balance;
    String storeId;
    ArrayList<WalletHistory> walletHistories = new ArrayList<>();
    StoreWalletRecyclerviewAdapter walletRecyclerviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_earning_view);
        binding();
        setField();
        button();
        readWalletHistoryData(this::walletHistoryAdapter);

    }

    private void button() {
        viewAllWithdrawHistory.setOnClickListener(v ->{
            Intent intent = new Intent(this, StoreWithdrawHistoryController.class);
            startActivity(intent);
        });

        withdrawButton.setOnClickListener(v ->{
            Intent intent = new Intent(this, ListOfStoreBankAccountController.class);
            startActivity(intent);
        });
    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Store store = getStoreDataFromStorage();
        storeId = store.getStoreId();
        //GET Earning BALANCE
        CollectionReference collectionReference = database.collection("wallet");
        Query query = collectionReference.whereEqualTo("userId", storeId);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    balance = document.getLong("balance").intValue();
                                    earningTextView.setText(IdrFormat.format(balance));
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
        recyclerView = findViewById(R.id.store_withdraw_history_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        earningTextView = findViewById(R.id.store_earning_balance_text);
        withdrawButton = findViewById(R.id.store_earning_withdraw_button);
        viewAllWithdrawHistory = findViewById(R.id.store_withdraw_history_view_all_button);
        toolbar = findViewById(R.id.store_earning_toolbar);
        walletWarning = findViewById(R.id.earning_warning);
    }

    public void walletHistoryAdapter(ArrayList<WalletHistory> list){
        Log.d("TAG", "walletHistoryAdapter: asd" + list.size());

        if(list.isEmpty()){
            Log.d("TAG", "walletHistoryAdapter: asd1");
            walletWarning.setVisibility(View.VISIBLE);
        }else{
            Log.d("TAG", "walletHistoryAdapter: asd2");
            walletWarning.setVisibility(View.GONE);
        }

        walletRecyclerviewAdapter = new StoreWalletRecyclerviewAdapter(list,this);
        recyclerView.setAdapter(walletRecyclerviewAdapter);
    }
    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }
    private void readWalletHistoryData(FirestoreCallback firestoreCallback){

        CollectionReference collectionRef = database.collection("transaction");
        Query query = collectionRef
                .whereEqualTo("userId", storeId)
                .orderBy("timestamp", Direction.DESCENDING)
                .limit(4);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String userId = document.getString("userId");
                    String date = document.getString("date");
                    String transactionType = document.getString("transactionType");
                    int amount = document.getLong("amount").intValue();
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