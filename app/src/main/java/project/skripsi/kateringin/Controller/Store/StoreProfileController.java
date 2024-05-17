package project.skripsi.kateringin.Controller.Store;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;

public class StoreProfileController extends AppCompatActivity {
    //XML
    TextView storeName, storeDesc, storeSubdistrict, storePhoneNumber, ownerId;
    ImageView storeProfileImage;
    Toolbar toolbar;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;


    @Override
    protected void onResume() {
        super.onResume();
        setField();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile_view);
        binding();
        setField();
    }

    private void binding(){
        toolbar = findViewById(R.id.store_profile_toolbar);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storeName = findViewById(R.id.store_profile_info_name);
        storeDesc= findViewById(R.id.store_profile_info_desc);
        storeSubdistrict = findViewById(R.id.store_profile_info_subdistrict);
        storePhoneNumber = findViewById(R.id.store_profile_info_phone);
        ownerId = findViewById(R.id.store_profile_info_owner_id);
        storeProfileImage = findViewById(R.id.store_profile_info_imagePicture);
    }

    private void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Store Profile Information");

        Store store = getStoreDataFromStorage();
        storeName.setText(store.getStoreName());
        storeDesc.setText(store.getStoreDesc());
        storeSubdistrict.setText(store.getStoreSubDistrict());
        storePhoneNumber.setText(store.getStorePhone());
        ownerId.setText(mAuth.getCurrentUser().getUid());

        //set image
        if(store.getStoreUrlPhoto() == null){
            Glide.with(this)
                    .load(R.drawable.default_image_profile)
                    .into(storeProfileImage);
        }else{
            RequestBuilder<Drawable> requestBuilder= Glide.with(storeProfileImage.getContext())
                    .asDrawable().sizeMultiplier(0.1f);

            Glide.with(this)
                    .load(store.getStoreUrlPhoto())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .thumbnail(requestBuilder)
                    .placeholder(R.drawable.default_image_profile)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(storeProfileImage);
        }
    }

    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_profile_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.menu_edit_store_profile) {
            Intent intent = new Intent(getApplicationContext(), EditStoreController.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
