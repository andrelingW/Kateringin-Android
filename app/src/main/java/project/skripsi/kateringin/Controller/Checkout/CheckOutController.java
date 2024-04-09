package project.skripsi.kateringin.Controller.Checkout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.EmailVerificationController;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.CheckOutRecyclerviewAdapter;
import project.skripsi.kateringin.RecyclerviewItem.CartItem;
import project.skripsi.kateringin.RecyclerviewItem.CheckOutItem;

public class CheckOutController extends AppCompatActivity {
    ImageButton contactEdit, locEdit;
    TextView contactNameTV, contactPhoneTV, addressTV, subTotalTV, feeTV, totalPriceTV;
    AppCompatButton checkout;

    RecyclerView recyclerView;

    FirebaseFirestore database;
    FirebaseAuth mAuth;
    ArrayList<CheckOutItem> checkOutItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_view);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        bindView();
        setView();
        buttonAction();
    }

    public void bindView(){
        contactEdit = findViewById(R.id.checkout_contact_edit_button);
        locEdit = findViewById(R.id.checkout_location_edit_button);
        contactNameTV = findViewById(R.id.checkout_contact_name_tv);
        contactPhoneTV = findViewById(R.id.checkout_contact_phone_tv);
        addressTV = findViewById(R.id.checkout_loc_tv);
        recyclerView = findViewById(R.id.checkout_recyclerView);
        checkout = findViewById(R.id.checkout_order_button);
        subTotalTV = findViewById(R.id.checkout_subtotal_tv);
        feeTV = findViewById(R.id.checkout_fee_tv);
        totalPriceTV = findViewById(R.id.checkout_total_price_tv);
    }



    public void setView(){
        User user = getUserDataFromStorage();

        contactNameTV.setText(user.getName());
        contactPhoneTV.setText(user.getPhoneNumber());
        addressTV.setText("-");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        //TESTING
        checkOutItems = new ArrayList<CheckOutItem>();
        checkOutItems.add(new CheckOutItem(null,"nasi goreng cakalang",null,null,null,null,null,null));
        checkOutItems.add(new CheckOutItem(null,"nasi goreng ",null,null,null,null,null,null));

        CheckOutRecyclerviewAdapter checkOutRecycleviewAdapter = new CheckOutRecyclerviewAdapter(checkOutItems,this);
        recyclerView.setAdapter(checkOutRecycleviewAdapter);

        subTotal();
        TotalPrice();

    }


    public void buttonAction(){
        contactEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CheckOutContactEditController.class);
            someActivityResultLauncher.launch(intent);
        });

        locEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CheckOutLocationEditController.class);
            someActivityResultLauncher.launch(intent);
        });

        checkout.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), ChoosePaymentController.class);
            startActivity(intent);
        });

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 1111) {
                        Intent data = result.getData();
                        String contactName = data.getStringExtra("CONTACT_EDIT_NAME");
                        String contactPhone = data.getStringExtra("CONTACT_EDIT_PHONE");
                        contactNameTV.setText(contactName);
                        contactPhoneTV.setText(contactPhone);
                    } else if (result.getResultCode() == 2222) {
                        Intent data = result.getData();
                        String location = data.getStringExtra("LOCATION_EDIT_ADDRESS");
                        addressTV.setText(location);
                    }
                }
            });

    private User getUserDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        User user = gson.fromJson(sharedPreferences.getString("userObject", ""), User.class);

        return user;
    }

    public void subTotal(){
//        int subTotalPrice = calculateItemsTotalPrice();
//        subTotalTV.setText("Rp " + subTotalPrice);
    }
    public void TotalPrice() {
//        int totalPrice = calculateItemsTotalPrice() + 15000;
//        totalPriceTV.setText("Rp " + totalPrice);
    }

//    private int calculateItemsTotalPrice() {
//        int totalPrice = 0;
//        for (CartItem item : cartItems) {
//            totalPrice += item.getFoodPrice() * item.getQuantity();
//        }
//        return totalPrice;
//    }

}

