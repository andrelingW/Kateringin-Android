package project.skripsi.kateringin.Controller.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;


import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class UserController extends AppCompatActivity {

    //XML
    TextView name, phoneNumber, dob, gender, email, userId;
    ImageView userProfileImage;
    Toolbar toolbar;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;


    @Override
    protected void onResume() {
        super.onResume();
        setField();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        binding();
        setField();
    }

    private void binding(){
        toolbar = findViewById(R.id.profile_toolbar);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.profile_info_name);
        phoneNumber= findViewById(R.id.profile_info_phoneNumber);
        dob = findViewById(R.id.profile_info_dob);
        gender = findViewById(R.id.profile_info_gender);
        email = findViewById(R.id.profile_info_email);
        userId = findViewById(R.id.profile_info_user_id);
        userProfileImage = findViewById(R.id.profile_info_imagePicture);
    }

    private void setField(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Profile Information");
        User user = getUserDataFromStorage();

        name.setText(user.getName());
        phoneNumber.setText(user.getPhoneNumber());
        dob.setText(user.getBOD());
        gender.setText(user.getGender());
        userId.setText(mAuth.getCurrentUser().getUid());
        email.setText(user.getEmail());

        //set image
        if(user.getProfileImageUrl() == null){
            Glide.with(this)
                    .load(R.drawable.default_image_profile)
                    .into(userProfileImage);
        }else{
            RequestBuilder<Drawable> requestBuilder= Glide.with(userProfileImage.getContext())
                    .asDrawable().sizeMultiplier(0.1f);

            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .thumbnail(requestBuilder)
                    .placeholder(R.drawable.default_image_profile)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(userProfileImage);
        }
    }

    private User getUserDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        User user = gson.fromJson(sharedPreferences.getString("userObject", ""), User.class);

        return user;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.menu_edit_profile) {
            Intent intent = new Intent(getApplicationContext(), EditUserController.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}