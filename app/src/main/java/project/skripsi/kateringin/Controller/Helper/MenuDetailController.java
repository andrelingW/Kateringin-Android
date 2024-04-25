package project.skripsi.kateringin.Controller.Helper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.ReviewRecycleviewAdapter;
import project.skripsi.kateringin.Util.BottomSheetDialogMenuDetailOrder;

public class MenuDetailController extends AppCompatActivity {
    ArrayList<Review> reviews = new ArrayList<>();
    TextView menuName, menuPrice, menuRating, menuDescription, menuCalorie, storeName, reviewCounter;
    ImageView menuImage;
    Button order, storeDetail;
    Menu foodItem;
    RecyclerView recyclerView;
    ReviewRecycleviewAdapter recycleviewAdapter;
    FirebaseFirestore database;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail_controller);

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        foodItem = (Menu) getIntent().getSerializableExtra("details_screen");
        order = findViewById(R.id.menuDetailOrder);
        storeDetail = findViewById(R.id.menu_detail_store_redirect_button);

        menuName = findViewById(R.id.menu_detail_menu_name);
        menuPrice = findViewById(R.id.menu_detail_menu_price);
        menuRating = findViewById(R.id.menu_detail_menu_rating);
        menuDescription = findViewById(R.id.menu_detail_menu_description);
        menuCalorie = findViewById(R.id.menu_detail_menu_calorie);
        menuImage = findViewById(R.id.menu_detail_menu_image);
        storeName = findViewById(R.id.menu_detail_store_name);
        reviewCounter = findViewById(R.id.menu_detail_review_counter);

        menuName.setText(foodItem.getMenuName());
        menuPrice.setText("Rp " +foodItem.getMenuPrice() + ",00");
        menuRating.setText(String.valueOf(foodItem.getMenuRating()));
        menuDescription.setText(foodItem.getMenuDescription());
        menuCalorie.setText(foodItem.getMenuCalorie() + " Kkal");
        storeName.setText("Warung Mpok RiskaLa");

        Glide.with(this)
                .load(foodItem.getMenuPhotoUrl())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .placeholder(R.drawable.default_image_profile)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(menuImage);

        recyclerView = findViewById(R.id.menu_detail_review_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        readReviewData(this::reviewAdapter);

        storeDetail.setOnClickListener(v ->{
            Intent intent = new Intent(this, StoreController.class);
            startActivity(intent);
        });

        order.setOnClickListener(v ->{
            BottomSheetDialogMenuDetailOrder bottomSheetDialog = BottomSheetDialogMenuDetailOrder.newInstance(foodItem);
            bottomSheetDialog.setBottomSheetListener(this::addToCartSuccess);
            bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
        });
    }
    public void reviewAdapter(ArrayList<Review> reviewItems){
        reviewCounter.setText("(" + reviewItems.size() + ")");
        recycleviewAdapter = new ReviewRecycleviewAdapter(reviewItems,this);
        recyclerView.setAdapter(recycleviewAdapter);
    }
    public void addToCartSuccess() {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT);
        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar_add_to_cart_success, null);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT);
        snackbarLayout.addView(customSnackView, 0);
        snackbar.show();
    }

    public void goBack(View view) {
        getOnBackPressedDispatcher().onBackPressed();
    }

    private void readReviewData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("review");
        Query query = collectionRef
                .whereEqualTo("menuId", foodItem.getMenuId());

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
}