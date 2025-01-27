package project.skripsi.kateringin.Controller.Store;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import project.skripsi.kateringin.Controller.Product.ProductController;
import project.skripsi.kateringin.Controller.StoreEarning.StoreEarningController;
import project.skripsi.kateringin.Controller.StoreOrder.StoreOrderController;
import project.skripsi.kateringin.Controller.StoreOrder.StoreOrderHistoryController;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class StoreMainScreenController extends AppCompatActivity {
    TextView totalEarningText;
    ImageView storeProfileImage;
    ConstraintLayout totalEarning, storeProduct, storeOrder, storeHistory, storeProfile, storeLogout;

    FirebaseFirestore database;
    FirebaseAuth mAuth;
    String storeId;

    @Override
    protected void onResume() {
        super.onResume();
        setStoreImage();
        getStoreBalance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_mainscreen_view);
        binding();
        setStoreImage();
        getStoreBalance();
        button();
    }

    private void setStoreImage() {
        Store store = getStoreDataFromStorage();
        //set image
        if(store.getStoreUrlPhoto() == null){
            Glide.with(this)
                    .load(R.drawable.default_image_profile)
                    .into(storeProfileImage);
        }else{
            RequestBuilder<Drawable> requestBuilder= Glide.with(storeProfileImage.getContext())
                    .asDrawable().sizeMultiplier(0.1f);

            Glide.with(this)
                    .load(store.getStoreUrlPhoto())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .thumbnail(requestBuilder)
                    .placeholder(R.drawable.default_image_profile)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(storeProfileImage);
        }
    }

    private void binding(){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        totalEarningText = findViewById(R.id.totalEarningWithCurrency);
        totalEarning = findViewById(R.id.totalEarning);
        storeProduct = findViewById(R.id.storeProduct);
        storeOrder = findViewById(R.id.storeOrder);
        storeHistory = findViewById(R.id.storeHistory);
        storeProfile = findViewById(R.id.storeProfile);
        storeLogout = findViewById(R.id.storeLogout);
        storeProfileImage = findViewById(R.id.store_mainscreen_info_imagePicture);
    }
    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }
    private void getStoreBalance(){
        Store store = getStoreDataFromStorage();
        storeId = store.getStoreId();
        //GET Earning BALANCE
        CollectionReference collectionReference = database.collection("wallet");
        Query query = collectionReference.whereEqualTo("userId", storeId);
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if(document.exists()){
                                int balance = document.getLong("balance").intValue();
                                totalEarningText.setText(IdrFormat.format(balance));
                            }else {
                                System.out.println("No such document");
                            }
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private void button(){
        totalEarning.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), StoreEarningController.class);
            startActivity(intent);
        });
        storeProduct.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), ProductController.class);
            startActivity(intent);
        });

        storeOrder.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), StoreOrderController.class);
            startActivity(intent);
        });
        storeHistory.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), StoreOrderHistoryController.class);
            startActivity(intent);
        });
        storeProfile.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), StoreProfileController.class);
            startActivity(intent);
        });
        storeLogout.setOnClickListener(v->{
            finish();
        });

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
