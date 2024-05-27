package project.skripsi.kateringin.Controller.StoreEarning;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.Model.WalletHistory;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.StoreWalletRecyclerviewAdapter;
import project.skripsi.kateringin.Recycler.WalletHistoryRecyclerviewAdapter;

public class StoreTransactionHistoryController extends AppCompatActivity {

    //XML
    FirebaseFirestore database;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    ConstraintLayout walletHistoryWarning;

    //FIELD
    ArrayList<WalletHistory> walletHistories = new ArrayList<>();
    StoreWalletRecyclerviewAdapter walletHistoryRecyclerviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history_view);
        binding();
        setField();
        readWalletHistoryData(this::walletHistoryAdapter);
    }

    private void binding() {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.wallet_history_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        toolbar = findViewById(R.id.wallet_history_toolbar);
        walletHistoryWarning = findViewById(R.id.wallet_history_warning);
    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Transaction History");
    }

    public void walletHistoryAdapter(ArrayList<WalletHistory> list){
        if(list.isEmpty()){
            walletHistoryWarning.setVisibility(View.VISIBLE);
        }else{
            walletHistoryWarning.setVisibility(View.GONE);
        }

        walletHistoryRecyclerviewAdapter = new StoreWalletRecyclerviewAdapter(list,this);
        recyclerView.setAdapter(walletHistoryRecyclerviewAdapter);
    }
    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }
    private void readWalletHistoryData(FirestoreCallback firestoreCallback){

        Store store = getStoreDataFromStorage();
        CollectionReference collectionRef = database.collection("transaction");
        Query query = collectionRef
                .whereEqualTo("userId", store.getStoreId())
                .orderBy("timestamp", Query.Direction.DESCENDING);


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