package project.skripsi.kateringin.Controller.Wallet.Withdraw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import project.skripsi.kateringin.Model.UserBankAccount;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogWithdrawConfirmation;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class WithdrawController extends AppCompatActivity {

    //XML
    TextView bankNumberTV, bankNameTV, usernameTV, balanceTV;
    EditText withdrawAmount;
    Toolbar toolbar;
    AppCompatButton cancelBtn, submitBtn;

    //FIELD
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    UserBankAccount userBankAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_view);
        binding();
        setField();
        button();
    }

    private void binding() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.withdraw_toolbar);
        userBankAccount = (UserBankAccount) getIntent().getSerializableExtra("NEXT_SCREEN");
        bankNameTV = findViewById(R.id.withdraw_bankName_tv);
        bankNumberTV = findViewById(R.id.withdraw_bankNumber_tv);
        usernameTV = findViewById(R.id.withdraw_username_tv);
        balanceTV = findViewById(R.id.withdraw_user_balance_tv);
        cancelBtn = findViewById(R.id.withdraw_cancel_btn);
        submitBtn = findViewById(R.id.withdraw_submit_btn);
        withdrawAmount = findViewById(R.id.withdraw_amount_et);
    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bankNameTV.setText(userBankAccount.getBankName());
        bankNumberTV.setText(userBankAccount.getBankNumber());
        usernameTV.setText(userBankAccount.getAccountHolderName());

        CollectionReference collectionReference = database.collection("wallet");
        Query query = collectionReference.whereEqualTo("userId", mAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot document: task.getResult()){
                    String docId = document.getId();
                    DocumentReference documentReference = database.collection("wallet").document(docId);
                    documentReference.get().addOnCompleteListener(innerTask -> {
                        if(innerTask.isSuccessful()){
                            DocumentSnapshot documentSnapshot = innerTask.getResult();
                            if(documentSnapshot.exists()){
                                balanceTV.setText(IdrFormat.format(documentSnapshot.getLong("balance").intValue()));
                            }
                        }
                    });
                }
            }
        });

    }

    private void button() {
        cancelBtn.setOnClickListener(v ->{
            finish();
        });

        submitBtn.setOnClickListener(v ->{
            String guard = withdrawAmount.getText().toString();
            int value;
            if(!guard.equalsIgnoreCase("")){
                value = Integer.parseInt(guard);
            } else{
                value = 0;
            }

            if(value != 0){
                CollectionReference collectionReference = database.collection("wallet");
                Query query = collectionReference.whereEqualTo("userId", mAuth.getCurrentUser().getUid());
                query.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            String docId = document.getId();
                            DocumentReference documentReference = database.collection("wallet").document(docId);
                            documentReference.get().addOnCompleteListener(innerTask -> {
                                if(innerTask.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = innerTask.getResult();
                                    if(documentSnapshot.exists()){
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("withdrawAmount", value + 1000);
                                        bundle.putInt("currentBalance", documentSnapshot.getLong("balance").intValue());

                                        BottomSheetDialogWithdrawConfirmation bottomSheetDialogFragment = new BottomSheetDialogWithdrawConfirmation();
                                        bottomSheetDialogFragment.setArguments(bundle);
                                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                                    }
                                }
                            });
                        }
                    }
                });
            } else{
                Log.d("TAG", "button: TESTING NO WITHDRAW");
            }
        });
    }



}