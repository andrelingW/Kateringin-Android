package project.skripsi.kateringin.Controller.Wallet.Withdraw;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Helper.FoodResultController;
import project.skripsi.kateringin.Controller.Helper.MenuDetailController;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.Model.UserBankAccount;
import project.skripsi.kateringin.Model.WalletHistory;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.BankAccountsAdapter;
import project.skripsi.kateringin.Recycler.WalletRecyclerviewAdapter;

public class ListOfBankAccountController extends AppCompatActivity {

    //XML
    AppCompatButton addBankAccountBtn;
    RecyclerView recyclerView;
    Toolbar toolbar;

    //FIELD
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    ArrayList<UserBankAccount> userBankAccounts = new ArrayList<>();
    BankAccountsAdapter bankAccountsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_bank_account_view);
        binding();
        setField();
        button();
        readListOfBankAccount(this::BankAccountsAdapter);

    }

    private void binding() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        addBankAccountBtn = findViewById(R.id.list_of_bank_account_add_button);
        recyclerView = findViewById(R.id.list_of_bank_account_recyclerview);
        toolbar = findViewById(R.id.list_of_bank_account_toolbar);

    }

    private void setField() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void button() {
        addBankAccountBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, AddUserBankAccountController.class);
            someActivityResultLauncher.launch(intent);
        });
    }

    public void BankAccountsAdapter(ArrayList<UserBankAccount> list){
//        if(list.isEmpty()){
//            orderWarning.setVisibility(View.VISIBLE);
//        }else{
//            orderWarning.setVisibility(View.GONE);
//        }
        Log.d("TAG", "BankAccountsAdapter: " + list.size());
        bankAccountsAdapter = new BankAccountsAdapter(list,this);
        recyclerView.setAdapter(bankAccountsAdapter);

        bankAccountsAdapter.setOnClickListener((position, model) -> {
            Intent intent = new Intent(this, WithdrawController.class);
            intent.putExtra("NEXT_SCREEN", model);
            startActivity(intent);
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 3333) {
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            });

    private void readListOfBankAccount(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("userBankAccount");
        Query query = collectionRef
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();
                    String accountHolderName = document.getString("accountHolderName");
                    String bankName = document.getString("bankName");
                    String bankNumber = document.getString("bankNumber");

                    userBankAccounts.add(new UserBankAccount(
                            documentId,
                            accountHolderName,
                            bankName,
                            bankNumber
                    ));
                }
                firestoreCallback.onCallback(userBankAccounts);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private interface FirestoreCallback{
        void onCallback(ArrayList<UserBankAccount> list);
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