package project.skripsi.kateringin.Controller.Checkout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class CheckOutController extends AppCompatActivity {

    //KEY
    private static final String PREF_NAME = "CHECK_OUT_ITEM_PREF";

    //XML
    ImageButton contactEdit, locEdit;
    TextView contactNameTV, contactPhoneTV, addressTV, subTotalTV, feeTV, totalPriceTV, miniTotalPriceTV;
    AppCompatButton checkout;
    RecyclerView recyclerView;
    Toolbar toolbar;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    ArrayList<Cart> cartItems = new ArrayList<>();
    CheckOutRecyclerviewAdapter checkOutRecyclerviewAdapter;
    int subTotalPrice, feeLayanan, totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_view);

        bindView();
        setView();
        buttonAction();
    }

    public void bindView(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.checkout_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        miniTotalPriceTV = findViewById(R.id.checkout_total_fee_tv);
    }

    public void setView(){
        User user = getUserDataFromStorage();

        contactNameTV.setText(user.getName());
        contactPhoneTV.setText(user.getPhoneNumber());
        addressTV.setText("-");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        readCartData(this::cartAdapter);
    }

    public void cartAdapter(ArrayList<Cart> cartItems){
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        checkOutRecyclerviewAdapter = new CheckOutRecyclerviewAdapter(cartItems,this, sharedPreferences);
        recyclerView.setAdapter(checkOutRecyclerviewAdapter);
        setTotalPrice(cartItems);

        checkout.setOnClickListener(v ->{
            if(checkOutRecyclerviewAdapter != null){
                checkOutRecyclerviewAdapter.createSharedPreference();
            }
            User user = getUserDataFromStorage();

            Intent intent = new Intent(getApplicationContext(), ChoosePaymentController.class);

            intent.putExtra("CUSTOMER_NAME", contactNameTV.getText().toString());
            intent.putExtra("CUSTOMER_PHONE", contactPhoneTV.getText().toString());
            intent.putExtra("CUSTOMER_EMAIL", user.getEmail().toString());
            intent.putExtra("CUSTOMER_ADDRESS", addressTV.getText().toString());
            intent.putExtra("TOTAL_PRICE", totalPrice);
            intent.putExtra("FEE_LAYANAN", feeLayanan);
            startActivity(intent);
        });

    }



    public void buttonAction(){
        contactEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CheckOutContactEditController.class);
            intent.putExtra("CONTACT_EDIT_NAME", contactNameTV.getText().toString());
            intent.putExtra("CONTACT_EDIT_PHONE", contactPhoneTV.getText().toString());
            someActivityResultLauncher.launch(intent);
        });

        locEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CheckOutLocationEditController.class);
            intent.putExtra("LOCATION_EDIT", addressTV.getText().toString());
            someActivityResultLauncher.launch(intent);
        });
    }

    private void setTotalPrice(ArrayList<Cart> cartItems){
        subTotalPrice = calculateSubTotalPrice(cartItems);
        feeLayanan = subTotalPrice / 100;
        totalPrice = subTotalPrice + feeLayanan;
        subTotalTV.setText(IdrFormat.format(subTotalPrice));
        feeTV.setText(IdrFormat.format(feeLayanan));
        totalPriceTV.setText(IdrFormat.format(totalPrice));
        miniTotalPriceTV.setText(IdrFormat.format(totalPrice));

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


    private void readCartData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("cartItem");
        Query query = collectionRef
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .whereEqualTo("processed", false);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String cartId = document.getId();
                    String storeId = document.getString("storeId");
                    String menuId = document.getString("menuId");
                    String date = document.getString("date");
                    String timeRange = document.getString("timeRange");
                    String note = document.getString("note");
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
                            note,
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

