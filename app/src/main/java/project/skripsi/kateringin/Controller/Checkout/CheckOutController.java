package project.skripsi.kateringin.Controller.Checkout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.CheckOutRecyclerviewAdapter;

public class CheckOutController extends AppCompatActivity {
    private static final String PREF_NAME = "CHECK_OUT_ITEM_PREF";
    private static final String KEY_MY_LIST = "CHECK_OUT_ITEM";

    ImageButton contactEdit, locEdit;
    TextView contactNameTV, contactPhoneTV, addressTV, subTotalTV, feeTV, totalPriceTV;
    AppCompatButton checkout;
    RecyclerView recyclerView;
    EditText test;

    FirebaseFirestore database;
    FirebaseAuth mAuth;

    ArrayList<Cart> cartItems = new ArrayList<>();
    CheckOutRecyclerviewAdapter checkOutRecyclerviewAdapter;

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

        readCartData(this::cartAdapter);

//        //TESTING
//        checkOutItems = new ArrayList<CheckOutItem>();
//        checkOutItems.add(new CheckOutItem(null,"nasi goreng cakalang",null,null,null,null,null,null));
//        checkOutItems.add(new CheckOutItem(null,"nasi goreng ",null,null,null,null,null,null));
//
//        CheckOutRecyclerviewAdapter checkOutRecycleviewAdapter = new CheckOutRecyclerviewAdapter(checkOutItems,this);
//        recyclerView.setAdapter(checkOutRecycleviewAdapter);

    }

    public void cartAdapter(ArrayList<Cart> cartItems){
        checkOutRecyclerviewAdapter = new CheckOutRecyclerviewAdapter(cartItems,this);
        recyclerView.setAdapter(checkOutRecyclerviewAdapter);
        setTotalPrice(cartItems);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        prefsEditor.putString(KEY_MY_LIST, json).apply();
        prefsEditor.commit();
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
            User user = getUserDataFromStorage();

            Intent intent = new Intent(getApplicationContext(), ChoosePaymentController.class);

            intent.putExtra("CUSTOMER_NAME", contactNameTV.getText().toString());
            intent.putExtra("CUSTOMER_PHONE", contactPhoneTV.getText().toString());
            intent.putExtra("CUSTOMER_EMAIL", user.getEmail().toString());
            intent.putExtra("CUSTOMER_ADDRESS", addressTV.getText().toString());
            intent.putExtra("TOTAL_PRICE", Integer.parseInt(totalPriceTV.getText().toString()));
            startActivity(intent);
        });

    }

    private void setTotalPrice(ArrayList<Cart> cartItems){
        int subTotalPrice = calculateSubTotalPrice(cartItems);
        int totalPrice = subTotalPrice + 15000;
        subTotalTV.setText("Rp " + subTotalPrice);
        feeTV.setText("Rp 15.000");
        totalPriceTV.setText(String.valueOf(totalPrice));

    }

    private int calculateSubTotalPrice(ArrayList<Cart> cartItems) {
        int totalPrice = 0;
        for (Cart item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
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


//    public void subTotal(){
////        int subTotalPrice = calculateItemsTotalPrice();
////        subTotalTV.setText("Rp " + subTotalPrice);
//    }
//    public void TotalPrice() {
////        int totalPrice = calculateItemsTotalPrice() + 15000;
////        totalPriceTV.setText("Rp " + totalPrice);
//    }

    private void readCartData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("cartItem");
        Query query = collectionRef.whereEqualTo("userId", mAuth.getCurrentUser().getUid());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String cartId = document.getId();
                    String storeId = document.getString("storeId");
                    String menuId = document.getString("menuId");
                    String date = document.getString("date");
                    String timeRange = document.getString("timeRange");
                    int quantity = document.getLong("quantity").intValue();
                    int price = document.getLong("price").intValue();
                    boolean process = document.getBoolean("processed");

                    cartItems.add(new Cart(
                            cartId,
                            menuId,
                            storeId,
                            mAuth.getCurrentUser().getUid(),
                            date,
                            timeRange,
                            quantity,
                            price,
                            process
                    ));
                }
                firestoreCallback.onCallback(cartItems);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }
    private interface FirestoreCallback{
        void onCallback(ArrayList<Cart> list);
    }

}

