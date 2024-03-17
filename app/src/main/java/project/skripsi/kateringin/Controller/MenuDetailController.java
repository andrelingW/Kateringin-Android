package project.skripsi.kateringin.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;
import project.skripsi.kateringin.Util.BottomSheetDialogMenuDetailOrder;
import project.skripsi.kateringin.Util.BottomSheetDialogProfileLogout;

public class MenuDetailController extends AppCompatActivity {

    Button order;
    FoodItem foodItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail_controller);
        foodItem = (FoodItem) getIntent().getSerializableExtra("details_screen");
        order = findViewById(R.id.menuDetailOrder);

        Log.i("test", foodItem.getFoodName().toString());
        order.setOnClickListener(v ->{
            BottomSheetDialogMenuDetailOrder bottomSheetDialog = new BottomSheetDialogMenuDetailOrder();
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