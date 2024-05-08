package project.skripsi.kateringin.Controller.Review;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.Controller.SuccessMessage.ReviewSuccessController;
import project.skripsi.kateringin.Controller.SuccessMessage.ThankYouController;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.ReviewPageRecycleviewAdapter;

public class ReviewController extends AppCompatActivity {

    //XML
    AppCompatButton submit, pass;
    RecyclerView recyclerView;

    //FIELD
    ArrayList<OrderItem> list;
    ArrayList<Review> reviews = new ArrayList<>();
    ReviewPageRecycleviewAdapter recycleviewAdapter;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view);
        binding();
        setField();
        recyclerAdapter();
        button();
    }

    private void binding(){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        submit = findViewById(R.id.review_submit_button);
        pass = findViewById(R.id.review_ignore_button);
        recyclerView = findViewById(R.id.review_page_recyclerView);
        order = (Order) getIntent().getSerializableExtra("ORDER_REVIEW");
    }

    private void setField() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        list = new ArrayList<>(order.getOrderItems());

        for(OrderItem orderItem : list){
            Review review = new Review(null, orderItem.getMenuId(),order.getUserId(), 0.0, null);
            reviews.add(review);
        }
    }

    private void recyclerAdapter() {
        recycleviewAdapter = new ReviewPageRecycleviewAdapter(reviews,this);
        recyclerView.setAdapter(recycleviewAdapter);
    }

    private void button() {
        submit.setOnClickListener(v ->{
            if(recycleviewAdapter != null){
                recycleviewAdapter.pushToFirestore();
                updateOrderStatus();
                Intent intent = new Intent(this, ReviewSuccessController.class);
                intent.putExtra("RATING_CALCULATE", order);
                startActivity(intent);
            }
        });
        pass.setOnClickListener(v ->{
            Intent intent = new Intent(this, ThankYouController.class);
            updateOrderStatus();
            startActivity(intent);
        });
    }

    public void updateOrderStatus(){
        DocumentReference docRef = database.collection("order").document(order.getOrderId());
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
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
                        order.setDate((String) item.get("date"));
                        order.setTimeRange((String) item.get("timeRange"));
                        order.setReschedule((Boolean) item.get("reschedule"));
                        order.setOrderItemStatus("complete");
                        order.setOrderItemId((String) item.get("orderItemId"));
                        order.setNote((String) item.get("note"));
                        order.setMenuId((String) item.get("menuId"));
                        order.setPrice(((Long) item.get("price")).intValue());
                        order.setQuantity(((Long) item.get("quantity")).intValue());
                        order.setOrderItemLinkTracker((String) item.get("orderItemLinkTracker"));

                        listOfOrderItem.add(order);

                        Order newOrder = new Order(
                                orderId,
                                storeId,
                                userId,
                                receiverAddress,
                                receiverName,
                                receiverPhone,
                                "complete",
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
//        DocumentReference docRef = database.collection("order").document(order.getOrderId());
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("orderStatus", "complete");
//        docRef.update(updates);
        });
    }

}