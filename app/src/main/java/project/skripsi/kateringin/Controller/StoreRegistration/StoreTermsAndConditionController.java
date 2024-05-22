package project.skripsi.kateringin.Controller.StoreRegistration;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogStoreTermAndCondition;

public class StoreTermsAndConditionController extends AppCompatActivity {

    CheckBox tnc;
    AppCompatButton cancel, accept;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_terms_and_conditions_view);

        binding();
        button();

    }
    private void binding(){
        tnc = findViewById(R.id.store_regis_form_checkBox);
        cancel = findViewById(R.id.store_regis_form_cancel_button);
        accept = findViewById(R.id.store_regis_form_accept_button);
        toolbar();
    }

    private void button() {
        cancel.setOnClickListener(v ->{
//            Intent intent = new Intent(this, MainScreenController.class);
//            intent.putExtra("fragmentId", R.layout.fragment_user);
//            intent.putExtra("menuItemId", R.id.menu_profile);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            finish();
        });

        accept.setOnClickListener(v ->{
            if(tnc.isChecked()){
                Intent intent = new Intent(this, StoreRegisterFormController.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast toast=Toast. makeText(getApplicationContext(),"Please accept the terms and conditions", Toast. LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void toolbar(){
        Toolbar toolbar = findViewById(R.id.storeTermAndConditionToolbar);
        toolbar.setTitle("Terms and Conditions");

    }

}