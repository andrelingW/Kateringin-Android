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
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.Authentication.EmailVerificationController;
import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.Controller.Wallet.WalletController;
import project.skripsi.kateringin.R;

public class StoreRegisterFormController extends AppCompatActivity {

    EditText storeNameTxt, storePhoneNumberTxt, storeDescTxt, storeSubDistrictEditTxt;
    AppCompatButton storeRegisCancel, storeRegisterButton;
    TextView storeNameAlert, storePhoneAlert, storeSubDistrictAlert, storeDescAlert, storeSubDistrictTxtView;
    ListView listSubdistrictView;
    String storeName,storePhoneNumber, storeSubDistrict, storeDesc, storeId;
    View progressOverlay;
    ArrayList<String> listSubdistrict;
    Dialog dialog;


    FirebaseAuth mAuth;
    FirebaseFirestore database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
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
        progressOverlay.bringToFront();
        storeNameTxt = findViewById(R.id.storeNameEditText);
        storePhoneNumberTxt = findViewById(R.id.storePhoneNumberEditText);
        storeSubDistrictTxtView = findViewById(R.id.storeSubDistrictText);
        storeDescTxt = findViewById(R.id.storeDescriptionEditText);

        storeNameAlert = findViewById(R.id.storeNameAlert);
        storePhoneAlert = findViewById(R.id.storephoneNumberAlert);
        storeSubDistrictAlert = findViewById(R.id.storeSubDistrictAlert);
        storeDescAlert = findViewById(R.id.storeDescriptionAlert);

        storeRegisterButton = findViewById(R.id.storeRegisterButton);
        storeRegisCancel = findViewById(R.id.storeRegisterButtonCancel);

    }
    private void toolbar(){
        Toolbar toolbar = findViewById(R.id.storeRegisterToolbar);
        toolbar.setTitle("Catering Registration");
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
            } else{
                animateView(progressOverlay, View.GONE, 0, 200);
            }
        });

        storeRegisCancel.setOnClickListener(v ->{
//            Intent intent = new Intent(this, MainScreenController.class);
//            intent.putExtra("fragmentId", R.layout.fragment_user);
//            intent.putExtra("menuItemId", R.id.menu_profile);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            finish();
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(StoreRegisterFormController.this, R.layout.custom_list, listSubdistrict);

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

        storeId =  database.collection("store").document().getId().toString();

        database.collection("store").document(storeId)
                .set(newStore)
                .addOnCompleteListener(innerTaskAddUser -> {

                    updateIsOwner();

                    Map<String, Object> newWallet = new HashMap<>();
                    newWallet.put("userId", storeId);
                    newWallet.put("balance", 0);

                    database.collection("wallet").document()
                            .set(newWallet)
                            .addOnCompleteListener(innerTaskAddWallet -> {
                                animateView(progressOverlay, View.GONE, 0, 200);
                                Intent intent = new Intent(this, StoreRegisterSuccessController.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

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