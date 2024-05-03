package project.skripsi.kateringin.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Checkout.CheckOutController;
import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.CartRecycleviewAdapter;
import project.skripsi.kateringin.Util.IdrFormat;

public class CartFragment extends Fragment {

    //XML
    AppCompatButton checkout;
    TextView totalPriceTV;
    RecyclerView recyclerView;
    ConstraintLayout cartEmptyWarning;

    //FIELD
    ArrayList<Cart> cartItems = new ArrayList<>();
    CartRecycleviewAdapter cartRecycleviewAdapter;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    public static CartFragment newInstance() {
        Bundle args = new Bundle();
        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(cartRecycleviewAdapter != null){
            cartRecycleviewAdapter.updateAllData(database, mAuth);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        binding(rootView);
        readCartData(this::cartAdapter);
        return rootView;
    }

    private void binding(View rootView){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        cartEmptyWarning = rootView.findViewById(R.id.cart_empty_warning);
        cartEmptyWarning.setVisibility(View.VISIBLE);
        checkout = rootView.findViewById(R.id.cart_checkout_button);
        totalPriceTV = rootView.findViewById(R.id.cart_total_price);
        recyclerView = rootView.findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));

        emptyCart();
    }

//    public void refresh(){
//        Log.d("TAG", "refresh: asdasd");
//        FragmentTransaction ft = getParentFragment().
//        ft.detach(this).attach(this).commit();
//    }


    public void emptyCart(){
        cartEmptyWarning.setVisibility(View.VISIBLE);
        checkout.setEnabled(false);
        checkout.setBackgroundResource(R.drawable.custom_unactive_button);
    }

    public void notEmptyCart(){
        cartEmptyWarning.setVisibility(View.GONE);
        checkout.setEnabled(true);
        checkout.setBackgroundResource(R.drawable.custom_active_button);
    }

    public void cartAdapter(ArrayList<Cart> cartItems){
        if(!cartItems.isEmpty()){
            notEmptyCart();
        }
        cartRecycleviewAdapter = new CartRecycleviewAdapter(cartItems,this, getContext());
        recyclerView.setAdapter(cartRecycleviewAdapter);

        cartRecycleviewAdapter.setOnDeleteItemClickListener(position -> {
            cartRecycleviewAdapter.removeAt(position);
            updateTotalPrice(cartItems);
        });

        checkout.setOnClickListener(v ->{
            if(cartRecycleviewAdapter != null){
                cartRecycleviewAdapter.updateAllData(database, mAuth);
            }
            Intent intent = new Intent(getActivity(), CheckOutController.class);
            startActivity(intent);
        });
        updateTotalPrice(cartItems);
    }

    public void updateTotalPrice(ArrayList<Cart> cartItems) {
        int totalPrice = calculateTotalPrice(cartItems);
        totalPriceTV.setText(IdrFormat.format(totalPrice));
    }

    private int calculateTotalPrice(ArrayList<Cart> cartItems) {
        int totalPrice = 0;
        for (Cart item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    private void readCartData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("cartItem");
        Query query = collectionRef
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .whereEqualTo("processed", false);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String cartId = document.getId();
                    String storeId = document.getString("storeId");
                    String menuId = document.getString("menuId");
                    String date = document.getString("date");
                    String timeRange = document.getString("timeRange");
                    int quantity = document.getLong("quantity").intValue();
                    int price = document.getLong("price").intValue();
                    boolean process = document.getBoolean("processed");

                    cartItems.add(new Cart(
                            cartId,
                            menuId,
                            storeId,
                            mAuth.getCurrentUser().getUid(),
                            date,
                            timeRange,
                            null,
                            quantity,
                            price,
                            process
                    ));
                }
                firestoreCallback.onCallback(cartItems);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private interface FirestoreCallback{
        void onCallback(ArrayList<Cart> list);
    }
}