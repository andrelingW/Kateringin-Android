package project.skripsi.kateringin.Controller.StoreOrder;

import static android.content.ContentValues.TAG;
import static project.skripsi.kateringin.Util.UtilClass.LoadingUtil.animateView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.StoreOrderDetailRecycleviewAdapter;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class StoreOrderDetailController extends AppCompatActivity {

    //XML
    RecyclerView recyclerView;
    TextView orderId, receiverName, receiverPhone, receiverAddress, subTotalPriceTV, feeLayananTV, totalPriceTV;
    AppCompatButton acceptOrder, cancelOrder;
    Toolbar toolbar;
    ConstraintLayout manageOrderLayout;
    View progressOverlay;


    //FIELD
    StoreOrderDetailRecycleviewAdapter orderDetailRecycleviewAdapter;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    Order order;
    boolean isAccepted;
    String orderStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_order_detail_view);
        binding();
        setField();
        recyclerAdapter();
        button();
    }

    private void binding(){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressOverlay = findViewById(R.id.progress_overlay);
        order = (Order) getIntent().getSerializableExtra("DETAILS_SCREEN");
        toolbar = findViewById(R.id.store_order_detail_toolbar);
        recyclerView = findViewById(R.id.store_order_detail_recyclerView);
        orderId = findViewById(R.id.store_order_detail_orderId_text);
        receiverName = findViewById(R.id.store_order_detail_contact_name_tv);
        receiverPhone = findViewById(R.id.store_order_detail_contact_phone_tv);
        receiverAddress = findViewById(R.id.store_order_detail_loc_tv);
        subTotalPriceTV = findViewById(R.id.store_order_detail_subtotal_tv);
        feeLayananTV = findViewById(R.id.store_order_detail_fee_tv);
        totalPriceTV = findViewById(R.id.store_order_detail_total_payment_tv);
        acceptOrder = findViewById(R.id.store_order_detail_accept_button);
        cancelOrder = findViewById(R.id.store_order_detail_cancel_button);
        manageOrderLayout = findViewById(R.id.store_order_detail_layout);
    }

    private void setField(){
        progressOverlay.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        if(!order.getOrderStatus().equalsIgnoreCase("waiting")){
            manageOrderLayout.setVisibility(View.GONE);
        }

        setTotalPrice(order.getOrderItems());
        orderId.setText("Order ID #" + order.getOrderId());
        receiverName.setText(order.getReceiverName());
        receiverPhone.setText(order.getReceiverPhone());
        receiverAddress.setText(order.getReceiverAddress());


    }

    private void button(){
        acceptOrder.setOnClickListener(v ->{
            isAccepted = true;
            updateOrderStatus();
            finish();

        });

        cancelOrder.setOnClickListener(v ->{
            updateOrderStatus();
            finish();
        });
    }

    private void recyclerAdapter(){
        orderDetailRecycleviewAdapter = new StoreOrderDetailRecycleviewAdapter(order.getOrderItems(),this, order.getOrderId());
        recyclerView.setAdapter(orderDetailRecycleviewAdapter);
    }

    private void setTotalPrice(ArrayList<OrderItem> cartItems){
        int subTotalPrice = calculateSubTotalPrice(cartItems);
        int feeLayanan = subTotalPrice / 100;
        int totalPrice = subTotalPrice + feeLayanan;

        subTotalPriceTV.setText(IdrFormat.format(subTotalPrice));
        feeLayananTV.setText(IdrFormat.format(feeLayanan));
        totalPriceTV.setText(IdrFormat.format(totalPrice));

    }

    private int calculateSubTotalPrice(ArrayList<OrderItem> orderItems) {
        int totalPrice = 0;
        for (OrderItem item : orderItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
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

    private void updateOrderStatus(){

        if (isAccepted){
            orderStatus = "ongoing";
        }else if(!isAccepted){
            orderStatus = "canceled";
        }

        DocumentReference docRef = database.collection("order").document(order.getOrderId());
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){

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
                        order.setOrderItemStatus(orderStatus);
                        order.setOrderItemLinkTracker((String) item.get("orderItemLinkTracker"));
                        listOfOrderItem.add(order);
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("orderStatus", orderStatus);
                    map.put("orderItems", listOfOrderItem);

                    database.collection("order").document(order.getOrderId())
                            .update(map)
                            .addOnCompleteListener(innerTaskAddUser -> {
                                animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                                finish();
                            })
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3333 && resultCode == RESULT_OK) {
            String orderId = data.getStringExtra("ORDER_ID");
            DocumentReference docRef = database.collection("order").document(orderId);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
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
                        orderDetailRecycleviewAdapter = new StoreOrderDetailRecycleviewAdapter(listOfOrderItem,this, order.getOrderId());
                        orderDetailRecycleviewAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(orderDetailRecycleviewAdapter);

                    }
                }
            });

        }
    }


}