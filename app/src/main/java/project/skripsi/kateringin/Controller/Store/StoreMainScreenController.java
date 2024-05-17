package project.skripsi.kateringin.Controller.Store;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.Locale;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.Controller.Product.ProductController;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;

public class StoreMainScreenController extends AppCompatActivity {
    TextView totalEarningText;
    ConstraintLayout totalEarning, storeProduct, storeOrder, storeHistory, storeProfile, storeLogout;

    FirebaseFirestore database;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_mainscreen_view);
        binding();
        getStoreBalance();
        button();
    }

    private void binding(){
        totalEarningText = findViewById(R.id.totalEarningWithCurrency);
        totalEarning = findViewById(R.id.totalEarning);
        storeProduct = findViewById(R.id.storeProduct);
        storeOrder = findViewById(R.id.storeOrder);
        storeHistory = findViewById(R.id.storeHistory);
        storeProfile = findViewById(R.id.storeProfile);
        storeLogout = findViewById(R.id.storeLogout);
    }
    private Store getUserSharedPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("storeObject", "");
        Store store = gson.fromJson(json, Store.class);
        return store;
    }
    private void getStoreBalance(){
        Store store = getUserSharedPreference();
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        totalEarningText.setText(formatRupiah.format(Double.parseDouble(store.getBalance().toString())));
    }
    private void button(){
//        totalEarning.setOnClickListener(v->{
//            Intent intent = new Intent(getApplicationContext(), StoreTermsAndConditionController.class);
//            startActivity(intent);
//            finish();
//        });
        storeProduct.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), ProductController.class);
            startActivity(intent);
            finish();
        });

//        storeOrder.setOnClickListener(v->{
//            Intent intent = new Intent(getApplicationContext(), StoreTermsAndConditionController.class);
//            startActivity(intent);
//            finish();
//        });
//        storeHistory.setOnClickListener(v->{
//            Intent intent = new Intent(getApplicationContext(), StoreTermsAndConditionController.class);
//            startActivity(intent);
//            finish();
//        });
        storeProfile.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), StoreProfileController.class);
            startActivity(intent);
            finish();
        });
        storeLogout.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), MainScreenController.class);
            startActivity(intent);
            finish();
        });
    }
}
