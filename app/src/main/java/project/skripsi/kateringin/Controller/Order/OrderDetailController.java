package project.skripsi.kateringin.Controller.Order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Review.ReviewController;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.OrderDetailRecycleviewAdapter;
import project.skripsi.kateringin.Util.IdrFormat;

public class OrderDetailController extends AppCompatActivity {

    //XML
    RecyclerView recyclerView;
    TextView orderId, receiverName, receiverPhone, receiverAddress, subTotalPriceTV, feeLayananTV, totalPriceTV, tracker;
    AppCompatButton accepted;
    Toolbar toolbar;

    //FIELD
    OrderDetailRecycleviewAdapter orderDetailRecycleviewAdapter;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_view);
        binding();
        setField();
        recyclerAdapter();
        button();
    }

    private void binding(){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        order = (Order) getIntent().getSerializableExtra("DETAILS_SCREEN");
        toolbar = findViewById(R.id.order_detail_toolbar);
        recyclerView = findViewById(R.id.order_detail_recyclerView);
        orderId = findViewById(R.id.order_detail_orderId_text);
        receiverName = findViewById(R.id.order_detail_contact_name_tv);
        receiverPhone = findViewById(R.id.order_detail_contact_phone_tv);
        receiverAddress = findViewById(R.id.order_detail_loc_tv);
        subTotalPriceTV = findViewById(R.id.order_detail_subtotal_tv);
        feeLayananTV = findViewById(R.id.order_detail_fee_tv);
        totalPriceTV = findViewById(R.id.order_detail_total_payment_tv);
        tracker = findViewById(R.id.order_detail_tracker_link_ev);
        accepted = findViewById(R.id.order_detail_accepted_button);
    }

    private void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        setTotalPrice(order.getOrderItem());
        orderId.setText("No Order #" + order.getOrderId());
        receiverName.setText(order.getReceiverName());
        receiverPhone.setText(order.getReceiverPhone());
        receiverAddress.setText(order.getReceiverAddress());

        DocumentReference menuDocRef = database.collection("order").document(order.getOrderId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if(document.getString("orderStatus").equalsIgnoreCase("shipping")){
                        accepted.setEnabled(true);
                        accepted.setBackgroundResource(R.drawable.custom_active_button);
                    } else{
                        accepted.setEnabled(false);
                        accepted.setBackgroundResource(R.drawable.custom_unactive_button);
                    }
                } else {
                    System.out.println("No such document");
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void button(){
        accepted.setOnClickListener(v ->{
            Intent intent = new Intent(this, ReviewController.class);
            intent.putExtra("ORDER_REVIEW", order);
            startActivity(intent);
            finish();
        });
    }

    private void recyclerAdapter(){
        orderDetailRecycleviewAdapter = new OrderDetailRecycleviewAdapter(order.getOrderItem(),this);
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
}