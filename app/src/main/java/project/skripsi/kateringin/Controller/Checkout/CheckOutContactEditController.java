package project.skripsi.kateringin.Controller.Checkout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import project.skripsi.kateringin.R;

public class CheckOutContactEditController extends AppCompatActivity {

    //XML
    AppCompatButton save;
    EditText nameET, phoneET;
    Toolbar toolbar;

    //FIELD
    String name,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_contact_edit_view);
        bindView();
        setField();
        buttonAction();
    }

    public void setField(){
        nameET.setText(name);
        phoneET.setText(phone);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void bindView(){
        toolbar = findViewById(R.id.checkout_contact_edit_toolbar);
        name = getIntent().getStringExtra("CONTACT_EDIT_NAME");
        phone = getIntent().getStringExtra("CONTACT_EDIT_PHONE");
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