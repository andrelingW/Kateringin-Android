package project.skripsi.kateringin.Controller.StoreRegistration;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogStoreTermAndCondition;

public class StoreTermsAndConditionController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_terms_and_conditions_view);

        BottomSheetDialogStoreTermAndCondition bottomSheetDialog = new BottomSheetDialogStoreTermAndCondition();
        bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());

        binding();
    }
    private void binding(){
        toolbar();
    }
    private void toolbar(){
        Toolbar toolbar = findViewById(R.id.storeTermAndConditionToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Terms & Conditions");

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