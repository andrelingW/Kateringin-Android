package project.skripsi.kateringin.Controller.Wallet.Withdraw;

import static android.content.ContentValues.TAG;
import static project.skripsi.kateringin.Util.UtilClass.LoadingUtil.animateView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.LoadingUtil;

public class AddUserBankAccountController extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //XML
    EditText bankNumber, username;
    Toolbar toolbar;
    AppCompatButton save, cancel;
    View progressOverlay;
    MaterialSpinner spinner;


    //FIELD
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    String[] data = {"BCA", "Mandiri", "Permata", "Cimb Niaga"};
    String bankNameFld, bankNumberFld, usernameFld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_bank_account_view);
        binding();
        setField();
        button();
    }

    private void binding() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.add_bank_account_toolbar);
        progressOverlay = findViewById(R.id.progress_overlay);
        bankNumber = findViewById(R.id.add_bank_account_number_et);
        username = findViewById(R.id.add_bank_account_username_et);
        save = findViewById(R.id.add_bank_account_bank_save_btn);
        cancel = findViewById(R.id.add_bank_account_bank_cancel_btn);
        spinner = findViewById(R.id.spinner);

    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressOverlay.bringToFront();
        spinner.setItems("BCA", "Mandiri", "Permata", "Cimb Niaga");
        bankNameFld = "BCA";
        spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> bankNameFld = item);
    }

    private void button() {
        cancel.setOnClickListener(v ->{
            finish();
        });

        save.setOnClickListener(v ->{
            LoadingUtil.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            bankNumberFld = bankNumber.getText().toString();
            usernameFld = username.getText().toString();



            if(!bankNameFld.isEmpty() && !bankNumberFld.isEmpty() && !usernameFld.isEmpty()){
                Map<String, Object> newBankAccount = new HashMap<>();
                newBankAccount.put("bankName", bankNameFld);
                newBankAccount.put("bankNumber", bankNumberFld);
                newBankAccount.put("accountHolderName", usernameFld);
                newBankAccount.put("userId", mAuth.getCurrentUser().getUid());

                database.collection("userBankAccount").document()
                        .set(newBankAccount)
                        .addOnCompleteListener(task -> {
                            Intent intent = new Intent(getApplicationContext(), ListOfBankAccountController.class);
                            setResult(3333, intent);
                            finish();
                            animateView(progressOverlay, View.GONE, 0, 200);
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error writing document", e);
                            LoadingUtil.animateView(progressOverlay, View.GONE, 0, 200);
                        });

            } else{
                LoadingUtil.animateView(progressOverlay, View.GONE, 0, 200);
                showSnackbar(v);
            }
        });


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

    private void showSnackbar(View v){
        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar_failed, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(customSnackView, 0);
        snackbar.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        bankNameFld = data[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}