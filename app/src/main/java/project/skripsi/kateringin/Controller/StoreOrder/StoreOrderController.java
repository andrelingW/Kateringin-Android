package project.skripsi.kateringin.Controller.StoreOrder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import project.skripsi.kateringin.Recycler.StoreOrderRecycleviewAdapter;

public class StoreOrderController extends AppCompatActivity {

    //KEY
    public static final String NEXT_SCREEN = "details_screen";
    //XML
    RadioGroup radioGroup;
    RadioButton orderStatusButton, orderStatusNewButton, orderStatusOngoingButton, orderStatusShippingButton;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ConstraintLayout orderWarning;


    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    String storeId;
    StoreOrderRecycleviewAdapter orderRecycleviewAdapter;
    ArrayList<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_order_view);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.storeOrderSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        binding();
        setField();

    }


    private void binding(){
        toolbar = findViewById(R.id.store_order_toolbar);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.store_order_recycler_view);
        orderWarning = findViewById(R.id.store_order_warning);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        radioGroup = findViewById(R.id.orderStatusGroup);

        orderStatusNewButton = findViewById(R.id.orderStatusNewButton);
        orderStatusOngoingButton = findViewById(R.id.orderStatusOngoingButton);
        orderStatusShippingButton = findViewById(R.id.orderStatusShippingButton);
    }
    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }
    public void orderAdapter(ArrayList<Order> orderItems){
        if(orderItems.isEmpty()){
            orderWarning.setVisibility(View.VISIBLE);
        }else{
            orderWarning.setVisibility(View.GONE);
        }
        orderRecycleviewAdapter = new StoreOrderRecycleviewAdapter(orders, this);
        recyclerView.setAdapter(orderRecycleviewAdapter);
    }
    public void refreshData() {
        orderRecycleviewAdapter.clear();
        if (orderStatusNewButton.isChecked()) {
            readNewOrderData(this::orderAdapter);
            orders.clear();
        } else if (orderStatusOngoingButton.isChecked()) {
            readOngoingOrderData(this::orderAdapter);
            orders.clear();
        } else if (orderStatusShippingButton.isChecked()) {
            readShippingOrderData(this::orderAdapter);
            orders.clear();
        }
    }
    private void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Order");

        //default status button check (show the new order by default)
        orderStatusNewButton.setChecked(true);
        readNewOrderData(this::orderAdapter);
        orders.clear();

        radioGroup.setOnCheckedChangeListener((group, checkId) -> {
            orderStatusButton = findViewById(checkId);
            if (orderStatusButton != null) {
                if (checkId == R.id.orderStatusNewButton) {
                    readNewOrderData(this::orderAdapter);
                    orders.clear();
                } else if (checkId == R.id.orderStatusOngoingButton) {
                    readOngoingOrderData(this::orderAdapter);
                    orders.clear();
                } else if (checkId == R.id.orderStatusShippingButton) {
                    readShippingOrderData(this::orderAdapter);
                    orders.clear();
                }
            }
        });
    }

    private void readNewOrderData(FirestoreCallback firestoreCallback){
        Store store = getStoreDataFromStorage();
        storeId = store.getStoreId();
        CollectionReference collectionRef = database.collection("order");
        Query query = collectionRef
                .whereEqualTo("storeId", storeId)
                .where(Filter.equalTo("orderStatus","waiting")
                );

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

    private void readOngoingOrderData(FirestoreCallback firestoreCallback){
        Store store = getStoreDataFromStorage();
        storeId = store.getStoreId();
        CollectionReference collectionRef = database.collection("order");
        Query query = collectionRef
                .whereEqualTo("storeId", storeId)
                .where(Filter.equalTo("orderStatus","ongoing")
                );

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

    private void readShippingOrderData(FirestoreCallback firestoreCallback){
        Store store = getStoreDataFromStorage();
        storeId = store.getStoreId();
        CollectionReference collectionRef = database.collection("order");
        Query query = collectionRef
                .whereEqualTo("storeId", storeId)
                .where(Filter.equalTo("orderStatus","shipping")
                );

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
    public ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 200) {
                    refreshData();
                }
            });

}
