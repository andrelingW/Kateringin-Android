package project.skripsi.kateringin.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Helper.SearchPageController;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.TESTING.FoodItem;
import project.skripsi.kateringin.Recycler.ExploreRecycleviewAdapter;


public class ExploreFragment extends Fragment {

    //XML
    RecyclerView recyclerView;
    ConstraintLayout searchBar;

    //FIELD
    ArrayList<FoodItem> foodItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        binding(rootView);
        button();
        testing();
        recyclerAdapter();

        return rootView;
    }

    private void binding(View rootView){
        recyclerView = rootView.findViewById(R.id.explore_recyclerview);
        searchBar = rootView.findViewById(R.id.explore_search_bar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
    }

    private void button(){
        searchBar.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), SearchPageController.class);
            startActivity(intent);
        });
    }

    private void recyclerAdapter(){
        ExploreRecycleviewAdapter exploreRecycleviewAdapter = new ExploreRecycleviewAdapter(foodItems,getActivity());
        recyclerView.setAdapter(exploreRecycleviewAdapter);
    }

    private void testing() {
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

    }

}