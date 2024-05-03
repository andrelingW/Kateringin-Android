package project.skripsi.kateringin.Controller.Helper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import project.skripsi.kateringin.Controller.User.EditUserController;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.MenuRecycleviewAdapter;
import project.skripsi.kateringin.Util.BottomSheetDialogFilter;
import project.skripsi.kateringin.Util.BottomSheetDialogTopUpConfirmation;

public class FoodResultFilterController extends AppCompatActivity {

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
    int priceFlag;
    String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_result_filter_view);
        binding();
        setField();
        readMenuData(this::menuAdapter);
    }

    private void binding(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.search_result_filter_recyclerview);
        priceFlag = (int) getIntent().getSerializableExtra("PRICE_FLAG");
        search = (String) getIntent().getSerializableExtra("SEARCH");
        toolbar = findViewById(R.id.search_result_filter_toolbar);
        searchNotFound = findViewById(R.id.search_result_filter_not_found_warning);
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
            Intent intent = new Intent(FoodResultFilterController.this, MenuDetailController.class);
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
        String menuPriceFlag = "menuPrice";
        String menuSearchFlag = "menuSearch";
        Query query = null;
        switch (priceFlag){
            case 1:
                query = collectionRef.whereLessThan(menuPriceFlag, 25000)
                        .whereLessThanOrEqualTo(menuSearchFlag, search + "\uf8ff")
                        .whereGreaterThanOrEqualTo(menuSearchFlag,search);
                break;
            case 2:
                query = collectionRef
                            .whereGreaterThanOrEqualTo(menuPriceFlag,25000)
                            .whereLessThanOrEqualTo(menuPriceFlag,50000)
                            .whereGreaterThanOrEqualTo(menuSearchFlag,search)
                            .whereLessThanOrEqualTo(menuSearchFlag, search + "\uf8ff");
                break;
            case 3:
                query = collectionRef
                        .whereGreaterThan(menuPriceFlag, 50000)
                        .whereLessThanOrEqualTo(menuPriceFlag, 100000)
                        .whereGreaterThanOrEqualTo(menuSearchFlag,search)
                        .whereLessThanOrEqualTo(menuSearchFlag, search + "\uf8ff");
                break;
            case 4:
                query = collectionRef
                        .whereGreaterThan(menuPriceFlag, 100000)
                        .whereGreaterThanOrEqualTo(menuSearchFlag,search)
                        .whereLessThanOrEqualTo(menuSearchFlag, search + "\uf8ff");
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