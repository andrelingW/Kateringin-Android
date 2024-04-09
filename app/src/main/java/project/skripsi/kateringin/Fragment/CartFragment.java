package project.skripsi.kateringin.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.skripsi.kateringin.Controller.Checkout.CheckOutController;
import project.skripsi.kateringin.Controller.UserController;
import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.CartRecycleviewAdapter;
import project.skripsi.kateringin.RecyclerviewItem.CartItem;

public class CartFragment extends Fragment {

//    ArrayList<CartItem> cartItems;

    ArrayList<Cart> cartItems = new ArrayList<>();
    ArrayList<Cart> tempItems = new ArrayList<>();
    RecyclerView recyclerView;
    CartRecycleviewAdapter cartRecycleviewAdapter;
    AppCompatButton checkout;
    TextView totalPriceTV;
    FirebaseFirestore database;
    FirebaseAuth mAuth;

//    private Store store;
//    private Menu menu;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TAG", "onDestroyView: masok?");

        if(cartRecycleviewAdapter != null){
            Log.d("TAG", "onDestroyView: called?");
            cartRecycleviewAdapter.updateAllData();
        }

//        deleteDocuments();
//        insertCartItems();
    }


    private void insertCartItems() {
        CollectionReference collectionRef = database.collection("cartItem");

        for (Cart cartItem : cartItems) {
            Map<String, Object> data = new HashMap<>();

            data.put("cartItemId", cartItem.getCartItemId());
            data.put("menuId", cartItem.getMenuId());
            data.put("userId", cartItem.getUserId());
            data.put("date", cartItem.getDate());
            data.put("timeRange", cartItem.getTimeRange());
            data.put("price", cartItem.getPrice());
            data.put("quantity", cartItem.getQuantity());
            data.put("processed", cartItem.isProcessed());

            collectionRef.add(data);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        Log.d("TAG", "onCreateView: ");
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = rootView.findViewById(R.id.cartRecyclerView);
        checkout = rootView.findViewById(R.id.cart_checkout_button);
        totalPriceTV = rootView.findViewById(R.id.cart_total_price);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));

        readCartData(this::cartAdapter);


        return rootView;
    }

    public void cartAdapter(ArrayList<Cart> cartItems){
        cartRecycleviewAdapter = new CartRecycleviewAdapter(cartItems,this);
        recyclerView.setAdapter(cartRecycleviewAdapter);

        cartRecycleviewAdapter.setOnDeleteItemClickListener(position -> {
            cartRecycleviewAdapter.removeAt(position);
            updateTotalPrice(cartItems);
        });

        checkout.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), CheckOutController.class);
            startActivity(intent);
        });
        updateTotalPrice(cartItems);
    }

    public void updateTotalPrice(ArrayList<Cart> cartItems) {
        int totalPrice = calculateTotalPrice(cartItems);
        totalPriceTV.setText("Rp " + totalPrice);
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
        Query query = collectionRef.whereEqualTo("userId", mAuth.getCurrentUser().getUid());

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

                    tempItems.add(new Cart(
                            cartId,
                            menuId,
                            storeId,
                            mAuth.getCurrentUser().getUid(),
                            date,
                            timeRange,
                            quantity,
                            price,
                            process
                    ));
                }
                firestoreCallback.onCallback(tempItems);
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

//    public ArrayList<Cart> getCartData(){
//
//        CollectionReference collectionRef = database.collection("cartItem");
//        Query query = collectionRef.whereEqualTo("userId", mAuth.getCurrentUser().getUid());
//
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                ArrayList<Cart> tempCart = new ArrayList<>();
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    String cartId = document.getId();
//                    String storeId = document.getString("storeId");
//                    String menuId = document.getString("menuId");
//                    String date = document.getString("date");
//                    String timeRange = document.getString("timeRange");
//                    int quantity = document.getLong("quantity").intValue();
//                    int price = document.getLong("price").intValue();
//                    boolean process = document.getBoolean("processed");
////                    getStoreData(storeId);
////                    getMenuData(menuId);
//
////                    String time =  document.getString("quantity");
////                    String timeRange = "";
////                    switch(time){
////                        case "1":
////                            timeRange = "08:00 - 12:00";
////                            break;
////                        case "2":
////                            timeRange = "12:00 - 16:00";
////                            break;
////                        case "3":
////                            timeRange = "16:00 - 20:00";
////                            break;
////                    }
//                    Log.d("TAG", "getCartData: ");
//                    Log.d("TAG", cartId);
//                    tempCart.add(new Cart(
//                            cartId,
//                            menuId,
//                            storeId,
//                            mAuth.getCurrentUser().getUid(),
//                            date,
//                            timeRange,
//                            quantity,
//                            price,
//                            process
//                            ));
//
//                    testing.add(new Cart(
//                            cartId,
//                            menuId,
//                            storeId,
//                            mAuth.getCurrentUser().getUid(),
//                            date,
//                            timeRange,
//                            quantity,
//                            price,
//                            process
//                    ));
//                }
//            } else {
//                Exception e = task.getException();
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        if(tempCart.isEmpty()){
//            Log.d("TAG", "cibai empty");
//        }
//        if (testing.isEmpty()) {
//            Log.d("TAG", "cibai empty2");
//        }
//
//        return tempCart;
//    }

//    public void getStoreData(String storeId){
//
//        store = new Store();
//
//        DocumentReference docRef = database.collection("stores").document(storeId);
//
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    store.setStoreName(document.getString("storeName"));
//                } else {
//                    System.out.println("No such document");
//                }
//            } else {
//                Exception e = task.getException();
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public void getMenuData(String menuId){
//        menu = new Menu();
//
//        DocumentReference docRef = database.collection("menus").document(menuId);
//
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    menu.setMenuName(document.getString("menuName"));
//                    menu.setMenuPrice(Integer.parseInt(document.getString("menuPrice")));
//                } else {
//                    System.out.println("No such document");
//                }
//            } else {
//                Exception e = task.getException();
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

}