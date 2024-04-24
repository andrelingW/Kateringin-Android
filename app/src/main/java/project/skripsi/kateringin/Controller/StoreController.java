package project.skripsi.kateringin.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.ExploreRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.StoreRecycleviewAdapter;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;

public class StoreController extends AppCompatActivity {
    RecyclerView recyclerView;
    StoreRecycleviewAdapter storeRecycleviewAdapter;

    ArrayList<FoodItem> foodItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_view);

        recyclerView = findViewById(R.id.store_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        foodItems = new ArrayList<FoodItem>();
        foodItems.add(new FoodItem("12","Nasi Goreng Cakalang",null,null,null,null,null));
        foodItems.add(new FoodItem("1","Nasi Goreng Cakalang",null,null,null,null,null));

        storeRecycleviewAdapter = new StoreRecycleviewAdapter(foodItems,this);
        recyclerView.setAdapter(storeRecycleviewAdapter);
    }
}