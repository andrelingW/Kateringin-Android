package project.skripsi.kateringin.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;
import project.skripsi.kateringin.Util.ExploreRecycleviewAdapter;

public class FoodResultController extends AppCompatActivity {

    ArrayList<FoodItem> foodItems;
    RecyclerView recyclerView;

    public static final String NEXT_SCREEN = "details_screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_result_view);

        Toolbar toolbar = findViewById(R.id.search_result_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.search_result_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        foodItems = new ArrayList<FoodItem>();
        foodItems.add(new FoodItem("12","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("12","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("12","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("12","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));

        ExploreRecycleviewAdapter exploreRecycleviewAdapter = new ExploreRecycleviewAdapter(foodItems,this);
        recyclerView.setAdapter(exploreRecycleviewAdapter);

        exploreRecycleviewAdapter.setOnClickListener(new ExploreRecycleviewAdapter.OnClickListener() {
            @Override
            public void onClick(int position, FoodItem model) {
                Intent intent = new Intent(FoodResultController.this, MenuDetailController.class);
                // Passing the data to the
                // EmployeeDetails Activity
                intent.putExtra(NEXT_SCREEN, model);
                startActivity(intent);
            }
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
}