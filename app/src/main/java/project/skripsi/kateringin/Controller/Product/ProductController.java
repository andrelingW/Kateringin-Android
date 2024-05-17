package project.skripsi.kateringin.Controller.Product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.ProductRecyclerViewAdapter;

public class ProductController extends AppCompatActivity {

    //KEY
    public static final String NEXT_SCREEN = "details_screen";
    //XML
    ImageButton addProductButton;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ConstraintLayout storeProductWarning;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    String storeId;
    ProductRecyclerViewAdapter productRecyclerViewAdapter;
    ArrayList<Menu> menuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product_view);
        binding();
        setField();
        readMenuData(this::storeProductAdapter);
    }

    private void binding(){
        toolbar = findViewById(R.id.store_product_toolbar);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.storeProductRecyclerView);
        storeProductWarning = findViewById(R.id.store_product_warning);
        addProductButton = findViewById(R.id.store_add_product_button);
    }
    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }
    public void storeProductAdapter(ArrayList<Menu> menuItems){
        if(menuItems.isEmpty()){
            storeProductWarning.setVisibility(View.VISIBLE);
        }else{
            storeProductWarning.setVisibility(View.GONE);
        }

        productRecyclerViewAdapter = new ProductRecyclerViewAdapter(menuItems,this);
        recyclerView.setAdapter(productRecyclerViewAdapter);

        productRecyclerViewAdapter.setOnDeleteItemClickListener(position -> {
            productRecyclerViewAdapter.removeAt(position);

            if(menuItems.isEmpty()){
                storeProductWarning.setVisibility(View.VISIBLE);
            }else{
                storeProductWarning.setVisibility(View.GONE);
            }
        });
        productRecyclerViewAdapter.setOnEditItemClickListener((position, model) -> {
            Intent intent = new Intent(this, EditProductController.class);
            intent.putExtra(NEXT_SCREEN, model);
            startActivity(intent);
        });
        addProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProductController.class);
            startActivity(intent);
        });

    }


    private void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Product");

//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
//        int space = getResources().getDimensionPixelSize(R.dimen.recyclerview_item_2_column_space_other);
//        recyclerView.addItemDecoration(new RecyclerItemSpacer(this, space));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));


    }
    private void readMenuData(ProductController.FirestoreCallback firestoreCallback){

        Store store = getStoreDataFromStorage();
        storeId = store.getStoreId();

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

                            Double menuRating = document.getDouble("menuRating").doubleValue();

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
