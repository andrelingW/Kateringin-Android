package project.skripsi.kateringin.Controller.Helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.MenuRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.StoreRecycleviewAdapter;
import project.skripsi.kateringin.TESTING.FoodItem;
import project.skripsi.kateringin.Util.LoadingUtil;

public class StoreController extends AppCompatActivity {

    //KEY
    public static final String NEXT_SCREEN = "details_screen";

    //XML
    Toolbar toolbar;
    TextView storeName, storeDesc, storePhone;
    ImageView storeImage;
    RecyclerView recyclerView;

    //FIELD
    StoreRecycleviewAdapter storeRecycleviewAdapter;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    String storeId;
    ArrayList<Menu> menuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_view);
        binding();
        setField();
        readMenuData(this::storeMenuAdapter);
    }

    public void storeMenuAdapter(ArrayList<Menu> menuItems){
        storeRecycleviewAdapter = new StoreRecycleviewAdapter(menuItems,this);
        recyclerView.setAdapter(storeRecycleviewAdapter);
        storeRecycleviewAdapter.setOnClickListener((position, model) -> {
            Intent intent = new Intent(this, MenuDetailController.class);
            intent.putExtra(NEXT_SCREEN, model);
            startActivity(intent);
        });
    }

    private void setField() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DocumentReference docRef = database.collection("store").document(storeId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Glide.with(this)
                            .load(document.getString("storePhotoUrl"))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .placeholder(R.drawable.default_image_profile)
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(storeImage);
                    storeName.setText(document.getString("storeName"));
                    storeDesc.setText(document.getString("storeDescription"));
                    storePhone.setText(document.getString("storePhoneNumber"));
                } else {
                    Log.i("TAG", "No such document");
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void binding() {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storeId = (String) getIntent().getSerializableExtra("STORE_ID");
        toolbar = findViewById(R.id.store_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.store_recyclerview);
        storeImage = findViewById(R.id.store_profile_image);
        storeName = findViewById(R.id.store_name_tv);
        storeDesc = findViewById(R.id.store_description_tv);
        storePhone = findViewById(R.id.store_phone_tv);
    }


    private void readMenuData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("menu");

        collectionRef
                .whereEqualTo("storeId", storeId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String menuId = document.getId();
                            String storeId = document.getString("storeId");
                            String menuName = document.getString("menuName");
                            String menuDescription = document.getString("menuDescription");
                            String menuPhotoUrl = document.getString("menuPhotoUrl");
                            int menuCalorie = document.getLong("menuCalorie").intValue();
                            int menuPrice = document.getLong("menuPrice").intValue();

                            Double menuRating = document.getLong("menuRating").doubleValue();

                            Boolean isDiet = document.getBoolean("isDiet");
                            Boolean isNoodle = document.getBoolean("isNoodle");
                            Boolean isPork = document.getBoolean("isPork");
                            Boolean isRice = document.getBoolean("isRice");
                            Boolean isSoup = document.getBoolean("isSoup");
                            Boolean isVegan = document.getBoolean("isVegan");

                            menuItems.add(new Menu(
                                    menuId,
                                    storeId,
                                    menuName,
                                    menuDescription,
                                    menuPhotoUrl,
                                    menuCalorie,
                                    menuPrice,
                                    menuRating,
                                    isDiet,
                                    isNoodle,
                                    isPork,
                                    isRice,
                                    isSoup,
                                    isVegan
                            ));
                        }
                        firestoreCallback.onCallback(menuItems);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private interface FirestoreCallback{
        void onCallback(ArrayList<Menu> list);
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