package project.skripsi.kateringin.Controller.Order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import project.skripsi.kateringin.Controller.Checkout.CheckOutController;
import project.skripsi.kateringin.Controller.Review.ReviewController;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.OrderDetailRecycleviewAdapter;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogAcceptedConfirmation;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogTopUpConfirmation;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class OrderDetailController extends AppCompatActivity {

    //XML
    RecyclerView recyclerView;
    TextView orderId, receiverName, receiverPhone, receiverAddress, subTotalPriceTV, feeLayananTV, totalPriceTV;
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
        accepted = findViewById(R.id.order_detail_accepted_button);
    }

    private void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        setTotalPrice(order.getOrderItems());
        orderId.setText("Order ID #" + order.getOrderId());
        receiverName.setText(order.getReceiverName());
        receiverPhone.setText(order.getReceiverPhone());
        receiverAddress.setText(order.getReceiverAddress());

        DocumentReference menuDocRef = database.collection("order").document(order.getOrderId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<Map<String, Object>> orderItemsList = (ArrayList<Map<String, Object>>) document.get("orderItems");
                    int counter = 0;
                    for (Map<String, Object> item : orderItemsList) {

                        String orderItemStatus = (String) item.get("orderItemStatus");
                        if(orderItemStatus.equalsIgnoreCase("shipping")){
                            counter++;
                        }
                    }

                    if(counter == orderItemsList.size()){
                        accepted.setEnabled(true);
                        accepted.setBackgroundResource(R.drawable.custom_active_button);
                    } else{
                        accepted.setEnabled(false);
                        accepted.setBackgroundResource(R.drawable.custom_unactive_button);
                    }
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
//            Intent intent = new Intent(this, ReviewController.class);
//            intent.putExtra("ORDER_REVIEW", order);
//            startActivity(intent);
//            finish();

            Bundle bundle = new Bundle();
            bundle.putSerializable("ORDER_REVIEW", order);

            BottomSheetDialogAcceptedConfirmation bottomSheetDialogFragment = new BottomSheetDialogAcceptedConfirmation();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        });
    }

    private void recyclerAdapter(){
        orderDetailRecycleviewAdapter = new OrderDetailRecycleviewAdapter(order.getOrderItems(),this, order.getOrderId());
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
            Intent intent = new Intent(getApplicationContext(), CheckOutController.class);
            setResult(200, intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        orderDetailRecycleviewAdapter = new OrderDetailRecycleviewAdapter(listOfOrderItem,this, order.getOrderId());
                        orderDetailRecycleviewAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(orderDetailRecycleviewAdapter);

                    }
                }
            });

        }
    }


}