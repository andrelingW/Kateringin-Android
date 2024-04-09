package project.skripsi.kateringin.Controller.Checkout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.Gson;

import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class CheckOutContactEditController extends AppCompatActivity {

    AppCompatButton save;
    EditText nameET, phoneET;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_contact_edit_view);
        toolbar = findViewById(R.id.checkout_contact_edit_toolbar);
        bindView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        buttonAction();
    }

    public void bindView(){
        nameET = findViewById(R.id.checkout_contact_edit_name_et);
        phoneET = findViewById(R.id.checkout_contact_edit_phone_et);
        save = findViewById(R.id.checkout_contact_edit_save_button);
    }

    public void buttonAction(){
        save.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), CheckOutController.class);
            intent.putExtra("CONTACT_EDIT_NAME", nameET.getText().toString());
            intent.putExtra("CONTACT_EDIT_PHONE", phoneET.getText().toString());
            setResult(1111, intent);
            finish();
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