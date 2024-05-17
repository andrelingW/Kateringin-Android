package project.skripsi.kateringin.Controller.StoreRegistration;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import project.skripsi.kateringin.R;

public class StoreRegisterStartController extends AppCompatActivity {
    Button startRegister;
    FirebaseFirestore database;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_register_start_view);
        binding();
        button();
    }

    private void binding(){
        toolbar();
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        startRegister = findViewById(R.id.startStoreRegistration);
    }
    private void toolbar(){
        Toolbar toolbar = findViewById(R.id.startStoreRegistrationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(" ");
    }
    private void button() {
        startRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StoreTermsAndConditionController.class);
            startActivity(intent);
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
}
