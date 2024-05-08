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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Helper.FoodCategoryPageController;
import project.skripsi.kateringin.Controller.Helper.MenuDetailController;
import project.skripsi.kateringin.Controller.Search.SearchPageController;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.MenuRecycleviewAdapter;
import project.skripsi.kateringin.Util.UtilClass.RecyclerItemSpacer;


public class ExploreFragment extends Fragment {
    public static final String NEXT_SCREEN = "details_screen";

    //XML
    RecyclerView recyclerView;
    ConstraintLayout searchBar;
    ImageButton vegan, nasi, mie, kuah, diet, nonHalal;

    //FIELD
    ArrayList<Menu> menuItems = new ArrayList<>();
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    MenuRecycleviewAdapter menuRecycleviewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        binding(rootView);
        button();
        readMenuData(this::menuAdapter);

        return rootView;
    }

    private void binding(View rootView){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = rootView.findViewById(R.id.explore_recyclerview);
        searchBar = rootView.findViewById(R.id.explore_search_bar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        int space = getResources().getDimensionPixelSize(R.dimen.recyclerview_item_2_column_space_explore); // Define the spacing in XML
        recyclerView.addItemDecoration(new RecyclerItemSpacer(getActivity(), space));
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

    public void menuAdapter(ArrayList<Menu> menuItems){
        if (menuItems.isEmpty()){
        } else{
            for(int i = 0; i < 5; i++){
                menuItems.add(new Menu());
            }
        }
        menuRecycleviewAdapter = new MenuRecycleviewAdapter(menuItems,getActivity());
        recyclerView.setAdapter(menuRecycleviewAdapter);
        int space = getResources().getDimensionPixelSize(R.dimen.recyclerview_item_2_column_space_other);
        recyclerView.addItemDecoration(new RecyclerItemSpacer(getActivity(), space));

        menuRecycleviewAdapter.setOnClickListener((position, model) -> {
            Intent intent = new Intent(getActivity(), MenuDetailController.class);
            intent.putExtra(NEXT_SCREEN, model);
            startActivity(intent);
        });

    }

    private void readMenuData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("menu");

        Query query = collectionRef.limit(10);

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