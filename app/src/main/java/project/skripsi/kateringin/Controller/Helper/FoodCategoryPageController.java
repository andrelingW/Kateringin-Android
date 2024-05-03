package project.skripsi.kateringin.Controller.Helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.MenuRecycleviewAdapter;

public class FoodCategoryPageController extends AppCompatActivity {

    //KEY
    public static final String NEXT_SCREEN = "details_screen";

    //XML
    RecyclerView recyclerView;
    Toolbar toolbar;
    ConstraintLayout searchNotFound;

    //FIELD
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    ArrayList<Menu> menuItems = new ArrayList<>();
    MenuRecycleviewAdapter menuRecycleviewAdapter;
    String foodCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_category_page_view);
        binding();
        setField();
        readMenuData(this::menuAdapter);
    }

    private void binding(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.food_category_page_recyclerview);
        foodCategory = (String) getIntent().getSerializableExtra("SUB_ORDER_DECIDE");
        toolbar = findViewById(R.id.food_category_page_toolbar);
        searchNotFound = findViewById(R.id.food_category_page_warning);
        switch (foodCategory){
            case "VEGAN":
                toolbar.setTitle("Vegan");
                break;
            case "NASI":
                toolbar.setTitle("Nasi");
                break;
            case "MIE":
                toolbar.setTitle("Mie");
                break;
            case "KUAH":
                toolbar.setTitle("Kuah");
                break;
            case "DIET":
                toolbar.setTitle("Diet");
                break;
            case "NONHALAL":
                toolbar.setTitle("Non Halal");
                break;
        }
    }

    public void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
    }

    public void menuAdapter(ArrayList<Menu> menuItems){
        if (menuItems.isEmpty()){
            searchNotFound.setVisibility(View.VISIBLE);
        } else{
            searchNotFound.setVisibility(View.GONE);
            for(int i = 0; i < 30; i++){
                menuItems.add(new Menu());
            }
        }
        menuRecycleviewAdapter = new MenuRecycleviewAdapter(menuItems,this);
        recyclerView.setAdapter(menuRecycleviewAdapter);

        menuRecycleviewAdapter.setOnClickListener((position, model) -> {
            Intent intent = new Intent(this, MenuDetailController.class);
            intent.putExtra(NEXT_SCREEN, model);
            startActivity(intent);
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

    private void readMenuData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("menu");
        Query query = null;
        switch (foodCategory){
            case "VEGAN":
                query = collectionRef.whereEqualTo("isVegan", true);
                break;
            case "NASI":
                query = collectionRef.whereEqualTo("isRice", true);
                break;
            case "MIE":
                query = collectionRef.whereEqualTo("isNoodle", true);
                break;
            case "KUAH":
                query = collectionRef.whereEqualTo("isSoup", true);
                break;
            case "DIET":
                query = collectionRef.whereEqualTo("isDiet", true);
                break;
            case "NONHALAL":
                query = collectionRef.whereEqualTo("isPork", true);
                break;
        }

        query.get().addOnCompleteListener(task -> {
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
}