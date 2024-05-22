package project.skripsi.kateringin.Controller.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.Controller.Helper.MenuDetailController;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.MenuRecycleviewAdapter;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogFilter;
import project.skripsi.kateringin.Util.UtilClass.RecyclerItemSpacer;

public class FoodResultController extends AppCompatActivity {

    //KEY
    public static final String NEXT_SCREEN = "details_screen";

    //XML
    RecyclerView recyclerView;
    Toolbar toolbar;
    ConstraintLayout searchNotFound;
    ImageButton cartShortcut;

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
        button();
        readMenuData(this::menuAdapter);
    }

    private void button() {
        cartShortcut.setOnClickListener(v ->{
            Intent intent = new Intent(this, MainScreenController.class);
            intent.putExtra("fragmentId", R.layout.fragment_cart);
            intent.putExtra("menuItemId", R.id.menu_cart);
            startActivity(intent);
            finish();
        });
    }

    private void binding(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.search_result_recyclerview);
        search = (String) getIntent().getSerializableExtra("SEARCH");
        toolbar = findViewById(R.id.search_result_toolbar);
        searchNotFound = findViewById(R.id.search_result_not_found_warning);
        cartShortcut = findViewById(R.id.search_result_cart_shortcut_button);
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
        }
        menuRecycleviewAdapter = new MenuRecycleviewAdapter(menuItems,this);
        recyclerView.setAdapter(menuRecycleviewAdapter);
        int space = getResources().getDimensionPixelSize(R.dimen.recyclerview_item_2_column_space_other);
        recyclerView.addItemDecoration(new RecyclerItemSpacer(this, space));

        menuRecycleviewAdapter.setOnClickListener((position, model) -> {
            Intent intent = new Intent(FoodResultController.this, MenuDetailController.class);
            intent.putExtra(NEXT_SCREEN, model);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.menu_filter) {
            Bundle bundle = new Bundle();
            bundle.putString("search", search);

            BottomSheetDialogFilter bottomSheetDialogFragment = new BottomSheetDialogFilter();
            bottomSheetDialogFragment.setArguments(bundle);

            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readMenuData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("menu");

        collectionRef
                .whereGreaterThanOrEqualTo("menuSearch",search)
                .whereLessThanOrEqualTo("menuSearch", search + "\uf8ff")
                .orderBy("menuRating", Query.Direction.DESCENDING)
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

                    Double menuRating = document.getDouble("menuRating");
                    Log.d("TAG", "readMenuData: testing" + menuRating);
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