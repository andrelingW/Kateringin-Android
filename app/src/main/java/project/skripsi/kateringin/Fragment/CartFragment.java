package project.skripsi.kateringin.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.CartRecycleviewAdapter;
import project.skripsi.kateringin.RecyclerviewItem.CartItem;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;
import project.skripsi.kateringin.Util.ExploreRecycleviewAdapter;


public class CartFragment extends Fragment {

    ArrayList<CartItem> cartItems;
    RecyclerView recyclerView;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = rootView.findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        cartItems = new ArrayList<CartItem>();
        cartItems.add(new CartItem(null,"nasi goreng cakalang",null,null,null,null,null,null));
        cartItems.add(new CartItem(null,"nasi goreng ",null,null,null,null,null,null));
        cartItems.add(new CartItem(null,"nasadang",null,null,null,null,null,null));
        cartItems.add(new CartItem(null,"nasadang",null,null,null,null,null,null));

        CartRecycleviewAdapter cartRecycleviewAdapter = new CartRecycleviewAdapter(cartItems,getActivity());
        recyclerView.setAdapter(cartRecycleviewAdapter);
        return rootView;
    }
}