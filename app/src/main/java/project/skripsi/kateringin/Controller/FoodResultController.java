package project.skripsi.kateringin.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Checkout.CheckOutController;
import project.skripsi.kateringin.Fragment.CartFragment;
import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.Model.newMenu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.CartRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.MenuRecycleviewAdapter;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;
import project.skripsi.kateringin.Recycler.ExploreRecycleviewAdapter;

public class FoodResultController extends AppCompatActivity {

    ArrayList<newMenu> menuItems = new ArrayList<>();
    RecyclerView recyclerView;
    MenuRecycleviewAdapter menuRecycleviewAdapter;

    FirebaseAuth mAuth;
    FirebaseFirestore database;

    String search;
    public static final String NEXT_SCREEN = "details_screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_result_view);

        search = (String) getIntent().getSerializableExtra("SEARCH");

        Toolbar toolbar = findViewById(R.id.search_result_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.search_result_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        readMenuData(this::menuAdapter);
    }

    public void menuAdapter(ArrayList<newMenu> menuItems){
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

    //TESTING PURPOSE
    private void readMenuData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("menu");


//        Query query = collectionRef
//                .whereNotEqualTo("userId", mAuth.getCurrentUser().getUid());

        Log.d("TAG", "readMenuData: " + search);

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

                    menuItems.add(new newMenu(
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
        void onCallback(ArrayList<newMenu> list);
    }
}