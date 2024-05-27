package project.skripsi.kateringin.Controller.StoreOrder;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.OrderDetailRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.StoreOrderDetailRecycleviewAdapter;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class StoreOrderHistoryDetailController extends AppCompatActivity {

    //XML
    RecyclerView recyclerView;
    TextView orderId, receiverName, receiverPhone, receiverAddress, subTotalPriceTV, feeLayananTV, totalPriceTV;
    Toolbar toolbar;
    ConstraintLayout manageOrderLayout;

    //FIELD
    StoreOrderDetailRecycleviewAdapter orderDetailRecycleviewAdapter;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_order_detail_view);
        binding();
        setField();
        recyclerAdapter();
    }

    private void binding(){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        order = (Order) getIntent().getSerializableExtra("DETAILS_SCREEN");
        toolbar = findViewById(R.id.store_order_detail_toolbar);
        recyclerView = findViewById(R.id.store_order_detail_recyclerView);
        orderId = findViewById(R.id.store_order_detail_orderId_text);
        receiverName = findViewById(R.id.store_order_detail_contact_name_tv);
        receiverPhone = findViewById(R.id.store_order_detail_contact_phone_tv);
        receiverAddress = findViewById(R.id.store_order_detail_loc_tv);
        totalPriceTV = findViewById(R.id.store_order_detail_total_tv);
        manageOrderLayout = findViewById(R.id.store_order_detail_layout);
    }

    private void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Order History Detail");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        manageOrderLayout.setVisibility(View.GONE);

        setTotalPrice(order.getOrderItems());
        orderId.setText("Order ID #" + order.getOrderId());
        receiverName.setText(order.getReceiverName());
        receiverPhone.setText(order.getReceiverPhone());
        receiverAddress.setText(order.getReceiverAddress());
    }

    private void recyclerAdapter(){
        orderDetailRecycleviewAdapter = new StoreOrderDetailRecycleviewAdapter(order.getOrderItems(),this, order.getOrderId());
        recyclerView.setAdapter(orderDetailRecycleviewAdapter);
    }

    private void setTotalPrice(ArrayList<OrderItem> cartItems){
        int totalPrice = calculateTotalPrice(cartItems);
        totalPriceTV.setText(IdrFormat.format(totalPrice));
    }

    private int calculateTotalPrice(ArrayList<OrderItem> orderItems) {
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
}