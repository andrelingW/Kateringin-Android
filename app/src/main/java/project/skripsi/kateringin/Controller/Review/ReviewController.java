package project.skripsi.kateringin.Controller.Review;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
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
    }

    private void setField() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        Order order = (Order) getIntent().getSerializableExtra("ORDER_REVIEW");
        list = new ArrayList<>(order.getOrderItem());

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
                Intent intent = new Intent(this, MainScreenController.class);
                startActivity(intent);
            }
        });
        pass.setOnClickListener(v ->{
            Intent intent = new Intent(this, MainScreenController.class);
            startActivity(intent);
        });
    }



}