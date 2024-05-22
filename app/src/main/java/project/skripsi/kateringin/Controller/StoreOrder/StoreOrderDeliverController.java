package project.skripsi.kateringin.Controller.StoreOrder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Map;

import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class StoreOrderDeliverController extends AppCompatActivity {

    //XML
    Toolbar toolbar;
    EditText deliveryLinkEditText;
    TextView deliverOrderItemName, deliverOrderItemPrice, deliverOrderItemQuantity, deliverOrderItemNote, deliverOrderItemTime, deliverOrderItemDate, deliveryLinkTitle;
    AppCompatButton cancelButton, submitButton;
    ImageView deliverOrderMenuImage;
    MaterialSpinner spinner;
    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    OrderItem order;
    String mainOrderId, deliveryMethod, deliveryLink;
    String[] data = {"Store Delivery Service", "Other Delivery Service"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_deliver_order_view);
        binding();
        setField();
        buttonAndLogic();
    }



    private void binding() {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.store_deliver_order_toolbar);
        order = (OrderItem) getIntent().getSerializableExtra("ORDER_DETAIL_ITEM");
        mainOrderId = (String) getIntent().getSerializableExtra("ORDER_ID");

        deliverOrderMenuImage = findViewById(R.id.store_deliver_order_item_menu_image);
        deliverOrderItemName = findViewById(R.id.store_deliver_order_item_menu_name);
        deliverOrderItemPrice = findViewById(R.id.store_deliver_order_item_menu_price);
        deliverOrderItemQuantity = findViewById(R.id.store_deliver_order_item_quantity);
        deliverOrderItemNote = findViewById(R.id.store_deliver_order_item_note);
        deliverOrderItemTime = findViewById(R.id.store_deliver_order_item_time);
        deliverOrderItemDate = findViewById(R.id.store_deliver_order_item_date);
        
        cancelButton = findViewById(R.id.store_deliver_order_cancel_button);
        submitButton = findViewById(R.id.store_deliver_order_submit_button);
        spinner = findViewById(R.id.store_deliver_order_spinner);

        deliveryLinkTitle = findViewById(R.id.store_deliver_order_link_text);
        deliveryLinkEditText = findViewById(R.id.store_deliver_order_link_field);
    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deliveryLinkTitle.setVisibility(View.GONE);
        deliveryLinkEditText.setVisibility(View.GONE);

        spinner.setItems("Store Delivery Service", "Other Delivery Service");
        deliveryMethod = "Store Delivery Service";

        spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
            deliveryMethod = item;
            if(deliveryMethod.equals("Other Delivery Service")){
                deliveryLinkTitle.setVisibility(View.VISIBLE);
                deliveryLinkEditText.setVisibility(View.VISIBLE);
            }else{
                deliveryLinkTitle.setVisibility(View.GONE);
                deliveryLinkEditText.setVisibility(View.GONE);
            }
        });

        DocumentReference menuDocRef = database.collection("menu").document(order.getMenuId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    deliverOrderItemName.setText(document.getString("menuName"));
                    long menuPriceTemp = document.getLong("menuPrice");
                    deliverOrderItemPrice.setText(IdrFormat.format(menuPriceTemp));
                    Glide.with(this)
                            .load(document.getString("menuPhotoUrl"))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(deliverOrderMenuImage);
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

        deliverOrderItemQuantity.setText("x" + order.getQuantity());
        deliverOrderItemTime.setText(order.getTimeRange());
        deliverOrderItemDate.setText(order.getDate());
        if (order.getNote() == null){
            deliverOrderItemNote.setText("-");
        } else{
            deliverOrderItemNote.setText(order.getNote());
        }
    }

    private void buttonAndLogic() {
        cancelButton.setOnClickListener(v -> finish());

        submitButton.setOnClickListener(v ->{
            if(deliveryMethod.equals("Other Delivery Service")){
                deliveryLink = deliveryLinkEditText.getText().toString();
            }
            else {
                deliveryLink = "Store Delivery";
            }

            String orderItemId = order.getOrderItemId();
            DocumentReference docRef = database.collection("order").document(mainOrderId);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        String orderId = document.getId();
                        String storeId = document.getString("storeId");
                        String userId = document.getString("userId");
                        String receiverName = document.getString("receiverName");
                        String receiverPhone = document.getString("receiverPhone");
                        String receiverAddress = document.getString("receiverAddress");
                        String orderStatus = document.getString("orderStatus");

                        ArrayList<Map<String, Object>> orderItemsList = (ArrayList<Map<String, Object>>) document.get("orderItems");
                        ArrayList<OrderItem> listOfOrderItem = new ArrayList<>();

                        int counter = 0;

                        for (Map<String, Object> item : orderItemsList) {
                            OrderItem order = new OrderItem();
                            String checker = (String) item.get("orderItemId");
                            Log.d("TAG", "buttonAndLogic: " +checker + "-" + orderItemId);
                            if(checker.equalsIgnoreCase(orderItemId)) {
                                order.setOrderItemLinkTracker(deliveryLink);
                                order.setOrderItemStatus("shipping");
                            } else {
                                order.setOrderItemStatus((String) item.get("orderItemStatus"));
                                order.setOrderItemLinkTracker((String) item.get("orderItemLinkTracker"));
                            }

                            if(order.getOrderItemStatus().equalsIgnoreCase("shipping")){
                                counter++;
                            }

                            if(counter == orderItemsList.size()){
                                orderStatus = "shipping";
                            }

                            order.setDate((String) item.get("date"));
                            order.setTimeRange((String) item.get("timeRange"));
                            order.setReschedule((Boolean) item.get("reschedule"));

                            order.setOrderItemId((String) item.get("orderItemId"));
                            order.setNote((String) item.get("note"));
                            order.setMenuId((String) item.get("menuId"));
                            order.setPrice(((Long) item.get("price")).intValue());
                            order.setQuantity(((Long) item.get("quantity")).intValue());

                            listOfOrderItem.add(order);

                            Order newOrder = new Order(
                                    orderId,
                                    storeId,
                                    userId,
                                    receiverAddress,
                                    receiverName,
                                    receiverPhone,
                                    orderStatus,
                                    listOfOrderItem
                            );

                            docRef.set(newOrder);


                            finish();
                        }
                    }else{
                        Log.i("TAG", "No such document");
                    }
                } else {
                    Log.i("TAG", "get failed with ", task.getException());
                }
            });



        });
    }
    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("ORDER_ID", mainOrderId);
        setResult(RESULT_OK, intent);
        super.finish();
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