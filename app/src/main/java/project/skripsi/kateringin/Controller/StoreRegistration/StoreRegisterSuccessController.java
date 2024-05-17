package project.skripsi.kateringin.Controller.StoreRegistration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import project.skripsi.kateringin.Controller.Store.StoreMainScreenController;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;

public class StoreRegisterSuccessController extends AppCompatActivity {

    //XML
    Button goToCateringMode;

    FirebaseFirestore database;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_register_success_view);
        binding();
        button();
    }

    private void binding(){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        goToCateringMode = findViewById(R.id.goToCateringModeButton);

    }

    private void button(){
        goToCateringMode.setOnClickListener(v->{
            CollectionReference collectionRef = database.collection("store");
            collectionRef
                    .whereEqualTo("ownerId", mAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);

                                    Store store = new Store();
                                    store.setStoreId(document.getId());
                                    store.setStoreName(document.get("storeName").toString());
                                    store.setStoreDesc(document.get("storeDescription").toString());
                                    store.setStorePhone(document.get("storePhoneNumber").toString());
                                    if(document.get("storePhotoUrl") != null){
                                        store.setStoreUrlPhoto(document.get("storePhotoUrl").toString());
                                    }
                                    store.setStoreSubDistrict(document.get("storeSubDistrict").toString());

                                    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(store);
                                    prefsEditor.putString("storeObject", json);
                                    prefsEditor.commit();

                                    Intent intent = new Intent(getApplicationContext(), StoreMainScreenController.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.i("TAG", "No such document");
                                }
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
            });
        });
    }
}