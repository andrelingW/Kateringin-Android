package project.skripsi.kateringin.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

import project.skripsi.kateringin.Controller.Checkout.CheckOutController;
import project.skripsi.kateringin.Controller.FoodResultController;
import project.skripsi.kateringin.Controller.MenuDetailController;
import project.skripsi.kateringin.Controller.OrderDetailController;
import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.CartRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.ExploreRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.OrderRecycleviewAdapter;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;

public class OrderFragment extends Fragment {

    RecyclerView recyclerView;
    OrderRecycleviewAdapter orderRecycleviewAdapter;
    ArrayList<Order> orders = new ArrayList<>();

    FirebaseFirestore database;
    FirebaseAuth mAuth;

    public static OrderFragment newInstance() {

        Bundle args = new Bundle();

        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        recyclerView = rootView.findViewById(R.id.order_recycler_view);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));

        readOrderData(this::orderAdapter);


        return rootView;
    }

    public void orderAdapter(ArrayList<Order> cartItems){
        orderRecycleviewAdapter = new OrderRecycleviewAdapter(orders,this, getContext());
        recyclerView.setAdapter(orderRecycleviewAdapter);

    }

    private void readOrderData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("order");
        Query query = collectionRef
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid());


        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String orderId = document.getId();
                    String storeId = document.getString("storeId");
                    String userId = document.getString("userId");
                    String receiverName = document.getString("receiverName");
                    String receiverEmail = document.getString("receiverEmail");
                    String receiverPhone = document.getString("receiverPhone");
                    String receiverAddress = document.getString("receiverAddress");

                    ArrayList<Map<String, Object>> orderItemsList = (ArrayList<Map<String, Object>>) document.get("orderItems");

                    ArrayList<OrderItem> listOfOrderItem = new ArrayList<>();
                    for (Map<String, Object> item : orderItemsList) {
                        OrderItem order = new OrderItem();

                        order.setCartItemId((String) item.get("cartItemId"));
                        order.setDate((String) item.get("date"));
                        order.setNote((String) item.get("note"));
                        order.setMenuId((String) item.get("menuId"));
                        order.setPrice(((Long) item.get("price")).intValue());
                        order.setQuantity(((Long) item.get("quantity")).intValue());
                        order.setTimeRange((String) item.get("timeRange"));

                        listOfOrderItem.add(order);
                    }

                    orders.add(new Order(
                            orderId,
                            storeId,
                            userId,
                            receiverAddress,
                            receiverName,
                            receiverPhone,
                            receiverEmail,
                            listOfOrderItem
                    ));
                }
                firestoreCallback.onCallback(orders);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private interface FirestoreCallback{
        void onCallback(ArrayList<Order> list);
    }
}