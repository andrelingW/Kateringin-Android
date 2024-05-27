package project.skripsi.kateringin.Controller.StoreOrder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.OrderHistoryRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.StoreOrderHistoryRecycleviewAdapter;

public class StoreOrderHistoryController extends AppCompatActivity {

    //XML
    Toolbar toolbar;
    RecyclerView recyclerView;
    ConstraintLayout orderHistoryWarning;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    StoreOrderHistoryRecycleviewAdapter orderHistoryRecycleviewAdapter;
    ArrayList<Order> orders = new ArrayList<>();
    String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_view);
        binding();
        setField();
        readOrderData(this::orderHistoryAdapter);
    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
    }

    private void binding() {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.order_history_toolbar);
        recyclerView = findViewById(R.id.order_history_recyclerview);
        orderHistoryWarning = findViewById(R.id.order_history_warning);
    }

    public void orderHistoryAdapter(ArrayList<Order> cartItems){
        if(cartItems.isEmpty()){
            orderHistoryWarning.setVisibility(View.VISIBLE);
        }else{
            orderHistoryWarning.setVisibility(View.GONE);
        }
        orderHistoryRecycleviewAdapter = new StoreOrderHistoryRecycleviewAdapter(orders,this);
        recyclerView.setAdapter(orderHistoryRecycleviewAdapter);
    }
    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }
    private void readOrderData(FirestoreCallback firestoreCallback){
        Store store = getStoreDataFromStorage();

        storeId = store.getStoreId();
        CollectionReference collectionRef = database.collection("order");
        Query query = collectionRef
                .whereEqualTo("storeId", storeId)
                .where(Filter.or(
                        Filter.equalTo("orderStatus","complete"),
                        Filter.equalTo("orderStatus","canceled")
                ));

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String orderId = document.getId();
                    String storeId = document.getString("storeId");
                    String userId = document.getString("userId");
                    String receiverName = document.getString("receiverName");
                    String receiverPhone = document.getString("receiverPhone");
                    String receiverAddress = document.getString("receiverAddress");
                    String orderStatus = document.getString("orderStatus");

                    ArrayList<Map<String, Object>> orderItemsList = (ArrayList<Map<String, Object>>) document.get("orderItems");
                    ArrayList<OrderItem> listOfOrderItem = new ArrayList<>();

                    for (Map<String, Object> item : orderItemsList) {
                        OrderItem order = new OrderItem();

                        order.setOrderItemId((String) item.get("orderItemId"));
                        order.setDate((String) item.get("date"));
                        order.setNote((String) item.get("note"));
                        order.setMenuId((String) item.get("menuId"));
                        order.setPrice(((Long) item.get("price")).intValue());
                        order.setQuantity(((Long) item.get("quantity")).intValue());
                        order.setTimeRange((String) item.get("timeRange"));
                        order.setReschedule((Boolean) item.get("reschedule"));
                        order.setOrderItemStatus((String) item.get("orderItemStatus"));
                        order.setOrderItemLinkTracker((String) item.get("orderItemLinkTracker"));

                        listOfOrderItem.add(order);
                    }

                    orders.add(new Order(
                            orderId,
                            storeId,
                            userId,
                            receiverAddress,
                            receiverName,
                            receiverPhone,
                            orderStatus,
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