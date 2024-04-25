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

public class CheckOutLocationEditController extends AppCompatActivity {

    //XML
    AppCompatButton save;
    EditText addressET;
    Toolbar toolbar;

    //STRING
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_location_edit_controller);
        bindView();
        setField();
        buttonAction();
    }

    public void setField(){
        addressET.setText(address);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void bindView(){
        address = getIntent().getStringExtra("LOCATION_EDIT");
        addressET = findViewById(R.id.checkout_location_address_et);
        toolbar = findViewById(R.id.checkout_location_edit_toolbar);
        save = findViewById(R.id.checkout_location_save_button);
    }

    public void buttonAction(){
        save.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), CheckOutController.class);
            intent.putExtra("LOCATION_EDIT_ADDRESS", addressET.getText().toString());
            setResult(2222, intent);
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


