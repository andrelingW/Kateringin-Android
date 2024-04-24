package project.skripsi.kateringin.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.Model.newMenu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.OrderDetailRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.ReviewRecycleviewAdapter;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;
import project.skripsi.kateringin.Util.BottomSheetDialogMenuDetailOrder;
import project.skripsi.kateringin.Util.BottomSheetDialogProfileLogout;

public class MenuDetailController extends AppCompatActivity {

    TextView menuName, menuPrice, menuRating, menuDescription, menuCalorie, storeName;
    ImageView menuImage;
    Button order, storeDetail;
    newMenu foodItem;
    RecyclerView recyclerView;
    ReviewRecycleviewAdapter recycleviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail_controller);
        foodItem = (newMenu) getIntent().getSerializableExtra("details_screen");
        order = findViewById(R.id.menuDetailOrder);
        storeDetail = findViewById(R.id.menu_detail_store_redirect_button);

        menuName = findViewById(R.id.menu_detail_menu_name);
        menuPrice = findViewById(R.id.menu_detail_menu_price);
        menuRating = findViewById(R.id.menu_detail_menu_rating);
        menuDescription = findViewById(R.id.menu_detail_menu_description);
        menuCalorie = findViewById(R.id.menu_detail_menu_calorie);
        menuImage = findViewById(R.id.menu_detail_menu_image);
        storeName = findViewById(R.id.menu_detail_store_name);

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
        ArrayList<Review> reviews = new ArrayList<>();
        reviews.add(new Review());
        reviews.add(new Review());
        reviews.add(new Review());
        reviews.add(new Review());
        reviews.add(new Review());
        reviews.add(new Review());

        recycleviewAdapter = new ReviewRecycleviewAdapter(reviews,this);
        recyclerView.setAdapter(recycleviewAdapter);

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

    public void addToCartSuccess() {
        // Use requireView() to get the root view of the fragment's layout
        View rootView = findViewById(android.R.id.content);


        // Create the Snackbar with the provided message and duration
        Snackbar snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT);

        // Inflate the custom Snackbar layout
        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar_add_to_cart_success, null);

        // Get the Snackbar layout
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Set the background of the default Snackbar as transparent
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT);

        // Add the custom Snackbar layout to the Snackbar layout
        snackbarLayout.addView(customSnackView, 0);

        // Show the Snackbar
        snackbar.show();
    }

    public void goBack(View view) {
        getOnBackPressedDispatcher().onBackPressed();
    }
}