package project.skripsi.kateringin.Controller.SuccessMessage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.R;

public class ReviewSuccessController extends AppCompatActivity {
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_success_view);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue, null));
        getWindow().getDecorView().setSystemUiVisibility(0);

        database = FirebaseFirestore.getInstance();
        Order order = (Order) getIntent().getSerializableExtra("RATING_CALCULATE");
        ArrayList<OrderItem> list = new ArrayList<>(order.getOrderItems());

        for(OrderItem orderItem : list){
            CollectionReference collectionReference = database.collection("review");
            Query query = collectionReference.whereEqualTo("menuId",orderItem.getMenuId());

            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Double rating = 0.0;
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        rating = rating + documentSnapshot.getDouble("rate");
                    }
                    Double result = rating / task.getResult().size();

                    DocumentReference docRef = database.collection("menu").document(orderItem.getMenuId());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("menuRating", result);
                    docRef.update(updates);
                }
            });


        }

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainScreenController.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}