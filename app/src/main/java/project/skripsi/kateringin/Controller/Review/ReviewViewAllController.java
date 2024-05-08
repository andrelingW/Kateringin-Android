package project.skripsi.kateringin.Controller.Review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Helper.MenuDetailController;
import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.ReviewRecycleviewAdapter;

public class ReviewViewAllController extends AppCompatActivity {

    //XML
    Toolbar toolbar;
    RecyclerView recyclerView;
    ConstraintLayout reviewWarning;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    ReviewRecycleviewAdapter recycleviewAdapter;
    ArrayList<Review> reviews = new ArrayList<>();
    String reviewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view_all_view);
        binding();
        setField();
        readReviewData(this::reviewAdapter);

    }

    private void setField() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void binding() {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.review_view_all_toolbar);
        recyclerView = findViewById(R.id.review_view_all_recyclerview);
        reviewId = (String) getIntent().getSerializableExtra("MENU_ID");
        reviewWarning = findViewById(R.id.review_warning);
    }

    public void reviewAdapter(ArrayList<Review> reviewItems){
        if(reviewItems.isEmpty()){
            reviewWarning.setVisibility(View.VISIBLE);
        }else{
            reviewWarning.setVisibility(View.GONE);
        }
        recycleviewAdapter = new ReviewRecycleviewAdapter(reviewItems,this);
        recyclerView.setAdapter(recycleviewAdapter);
    }
    private void readReviewData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("review");
        Query query = collectionRef
                .whereEqualTo("menuId", reviewId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String reviewId = document.getId();
                    String menuId = document.getString("menuId");
                    String userId = document.getString("userId");
                    Double rate = document.getDouble("rate");
                    String comment = document.getString("detail");

                    reviews.add(new Review(
                            reviewId,
                            menuId,
                            userId,
                            rate,
                            comment
                    ));
                }
                firestoreCallback.onCallback(reviews);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private interface FirestoreCallback{
        void onCallback(ArrayList<Review> list);
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