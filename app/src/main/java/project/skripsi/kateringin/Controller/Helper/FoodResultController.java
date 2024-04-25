package project.skripsi.kateringin.Controller.Helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.MenuRecycleviewAdapter;

public class FoodResultController extends AppCompatActivity {

    //KEY
    public static final String NEXT_SCREEN = "details_screen";

    //XML
    RecyclerView recyclerView;
    Toolbar toolbar;

    //FIELD
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    ArrayList<Menu> menuItems = new ArrayList<>();
    MenuRecycleviewAdapter menuRecycleviewAdapter;
    String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_result_view);
        binding();
        setField();
        readMenuData(this::menuAdapter);
    }

    private void binding(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.search_result_recyclerview);
        search = (String) getIntent().getSerializableExtra("SEARCH");
        toolbar = findViewById(R.id.search_result_toolbar);
    }

    public void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
    }

    public void menuAdapter(ArrayList<Menu> menuItems){
        menuRecycleviewAdapter = new MenuRecycleviewAdapter(menuItems,this);
        recyclerView.setAdapter(menuRecycleviewAdapter);

        menuRecycleviewAdapter.setOnClickListener((position, model) -> {
            Intent intent = new Intent(FoodResultController.this, MenuDetailController.class);
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

        collectionRef
                .whereGreaterThanOrEqualTo("menuSearch",search)
                .whereLessThanOrEqualTo("menuSearch", search + "\uf8ff")
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
}