package project.skripsi.kateringin.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.ForgotPasswordController;
import project.skripsi.kateringin.Controller.SearchPageController;
import project.skripsi.kateringin.Controller.UserController;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;
import project.skripsi.kateringin.Util.ExploreRecycleviewAdapter;


public class ExploreFragment extends Fragment {

    SearchView searchView;
    ArrayList<FoodItem> foodItems;
    RecyclerView recyclerView;

    ConstraintLayout searchBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
//        searchView = rootView.findViewById(R.id.searchViewA);
//
//        searchView.setIconified(false);

        recyclerView = rootView.findViewById(R.id.explore_recyclerview);
        searchBar = rootView.findViewById(R.id.explore_search_bar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

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

        ExploreRecycleviewAdapter exploreRecycleviewAdapter = new ExploreRecycleviewAdapter(foodItems,getActivity());
        recyclerView.setAdapter(exploreRecycleviewAdapter);

        searchBar.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), SearchPageController.class);
            startActivity(intent);
        });
        return rootView;
    }



}