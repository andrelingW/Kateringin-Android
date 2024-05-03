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
import android.widget.ImageButton;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Helper.FoodCategoryPageController;
import project.skripsi.kateringin.Controller.Helper.SearchPageController;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.TESTING.FoodItem;
import project.skripsi.kateringin.Recycler.ExploreRecycleviewAdapter;


public class ExploreFragment extends Fragment {

    //XML
    RecyclerView recyclerView;
    ConstraintLayout searchBar;
    ImageButton vegan, nasi, mie, kuah, diet, nonHalal;

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
        vegan = rootView.findViewById(R.id.subMenuVegan);
        nasi = rootView.findViewById(R.id.subMenuNasi);
        mie = rootView.findViewById(R.id.subMenuMie);
        kuah = rootView.findViewById(R.id.subMenuKuah);
        diet = rootView.findViewById(R.id.subMenuDiet);
        nonHalal = rootView.findViewById(R.id.subMenuNonHalal);
    }

    private void button(){
        searchBar.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), SearchPageController.class);
            startActivity(intent);
        });

        vegan.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), FoodCategoryPageController.class);
            intent.putExtra("SUB_ORDER_DECIDE", "VEGAN");
            startActivity(intent);
        });

        nasi.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), FoodCategoryPageController.class);
            intent.putExtra("SUB_ORDER_DECIDE", "NASI");
            startActivity(intent);
        });

        mie.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), FoodCategoryPageController.class);
            intent.putExtra("SUB_ORDER_DECIDE", "MIE");
            startActivity(intent);
        });

        kuah.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), FoodCategoryPageController.class);
            intent.putExtra("SUB_ORDER_DECIDE", "KUAH");
            startActivity(intent);
        });

        diet.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), FoodCategoryPageController.class);
            intent.putExtra("SUB_ORDER_DECIDE", "DIET");
            startActivity(intent);
        });

        nonHalal.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), FoodCategoryPageController.class);
            intent.putExtra("SUB_ORDER_DECIDE", "NONHALAL");
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