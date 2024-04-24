package project.skripsi.kateringin.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

import project.skripsi.kateringin.Fragment.OrderFragment;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.ReviewPageRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.ReviewRecycleviewAdapter;

public class ReviewController extends AppCompatActivity {

    AppCompatButton submit, pass;
    RecyclerView recyclerView;
    ReviewPageRecycleviewAdapter recycleviewAdapter;
    FirebaseFirestore database;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view);

        submit = findViewById(R.id.review_submit_button);
        pass = findViewById(R.id.review_ignore_button);

        Order order = (Order) getIntent().getSerializableExtra("ORDER_REVIEW");
        ArrayList<OrderItem> list = new ArrayList<>(order.getOrderItem());
        ArrayList<Review> reviews = new ArrayList<>();

        for(OrderItem orderItem : list){
            Review review = new Review(null, orderItem.getMenuId(),order.getUserId(), 0.0, null);
            reviews.add(review);
        }

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.review_page_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
//        ArrayList<Review> reviews = new ArrayList<>();
//        reviews.add(new Review());
//        reviews.add(new Review());
//        reviews.add(new Review());
//        reviews.add(new Review());
//        reviews.add(new Review());
//        reviews.add(new Review());

        recycleviewAdapter = new ReviewPageRecycleviewAdapter(reviews,this);
        recyclerView.setAdapter(recycleviewAdapter);

        submit.setOnClickListener(v ->{


        });
        pass.setOnClickListener(v ->{

        });
    }

}