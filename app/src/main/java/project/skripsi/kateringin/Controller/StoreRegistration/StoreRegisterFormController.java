package project.skripsi.kateringin.Controller.StoreRegistration;

import static android.content.ContentValues.TAG;

import static project.skripsi.kateringin.Util.UtilClass.LoadingUtil.animateView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.R;

public class StoreRegisterFormController extends AppCompatActivity {

    EditText storeNameTxt, storePhoneNumberTxt, storeDescTxt, storeSubDistrictEditTxt;
    Button storeRegisterButton;
    TextView storeNameAlert, storePhoneAlert, storeSubDistrictAlert, storeDescAlert, storeSubDistrictTxtView;
    ListView listSubdistrictView;
    String storeName,storePhoneNumber, storeSubDistrict, storeDesc;
    View progressOverlay;
    ArrayList<String> listSubdistrict;
    Dialog dialog;


    FirebaseAuth mAuth;
    FirebaseFirestore database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_register_form_view);
        binding();
        subdistrictDropdown();
        button();
    }
    private void binding(){
        toolbar();
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressOverlay = findViewById(R.id.progress_overlay);

        storeNameTxt = findViewById(R.id.storeNameEditText);
        storePhoneNumberTxt = findViewById(R.id.storePhoneNumberEditText);
        storeSubDistrictTxtView = findViewById(R.id.storeSubDistrictText);
        storeDescTxt = findViewById(R.id.storeDescriptionEditText);

        storeNameAlert = findViewById(R.id.storeNameAlert);
        storePhoneAlert = findViewById(R.id.storephoneNumberAlert);
        storeSubDistrictAlert = findViewById(R.id.storeSubDistrictAlert);
        storeDescAlert = findViewById(R.id.storeDescriptionAlert);

        storeRegisterButton = findViewById(R.id.storeRegisterButton);

    }
    private void toolbar(){
        progressOverlay.bringToFront();
        Toolbar toolbar = findViewById(R.id.storeRegisterToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Catering Registration");

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

    private void button() {
        storeRegisterButton.setOnClickListener(v -> {
            animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            storeName = storeNameTxt.getText().toString();
            storePhoneNumber = storePhoneNumberTxt.getText().toString();
            storeSubDistrict = storeSubDistrictTxtView.getText().toString();
            storeDesc = storeDescTxt.getText().toString();

            if(checkStoreData()){
                insertStoreData();
            }
        });
    }

    private void subdistrictDropdown(){
        listSubdistrict = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.array_subdistricts)));
        Collections.sort(listSubdistrict);
        storeSubDistrictTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(StoreRegisterFormController.this);
                dialog.setContentView((R.layout.dialog_searchable_spinner));
                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);

                dialog.getWindow().setLayout(width, height);
//                dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                storeSubDistrictEditTxt = dialog.findViewById(R.id.storeSubDistrictEditText);
                listSubdistrictView = dialog.findViewById(R.id.listSubdistrictView);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(StoreRegisterFormController.this, android.R.layout.simple_list_item_1, listSubdistrict);

                listSubdistrictView.setAdapter(adapter);

                storeSubDistrictEditTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listSubdistrictView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        storeSubDistrictTxtView.setText(adapter.getItem(position));
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void insertStoreData(){
        Map<String, Object> newStore = new HashMap<>();
        newStore.put("ownerId", mAuth.getCurrentUser().getUid());
        newStore.put("storeName", storeName);
        newStore.put("storeDescription", storeDesc);
        newStore.put("storePhoneNumber", storePhoneNumber);
        newStore.put("storePhotoUrl", null);
        newStore.put("storeSubDistrict", storeSubDistrict);
        newStore.put("storeBalance", 0);

        database.collection("store").document()
                .set(newStore)
                .addOnCompleteListener(innerTaskAddUser -> {

                    updateIsOwner();

                    Intent intent = new Intent(getApplicationContext(), StoreRegisterSuccessController.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));


    }
    private void updateIsOwner(){
        DocumentReference docRef = database.collection("user").document(mAuth.getCurrentUser().getUid().toString());
        Map<String, Object> updates = new HashMap<>();
        updates.put("isOwner", true);

        docRef.update(updates);
    }
    private Boolean checkStoreData() {
        Boolean status = true;
        if(storeName.isEmpty()){
            storeNameAlert.setVisibility(View.VISIBLE);
            storeNameTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            storeNameAlert.setVisibility(View.GONE);
            storeNameTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(storePhoneNumber.isEmpty()){
            storePhoneAlert.setVisibility(View.VISIBLE);
            storePhoneNumberTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            storePhoneAlert.setVisibility(View.GONE);
            storePhoneNumberTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(storeSubDistrict.isEmpty()){
            storeSubDistrictAlert.setVisibility(View.VISIBLE);
            storeSubDistrictTxtView.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            storeSubDistrictAlert.setVisibility(View.GONE);
            storeSubDistrictTxtView.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(storeDesc.isEmpty()){
            storeDescAlert.setVisibility(View.VISIBLE);
            storeDescTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            storeDescAlert.setVisibility(View.GONE);
            storeDescTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }

        return status;
    }


}