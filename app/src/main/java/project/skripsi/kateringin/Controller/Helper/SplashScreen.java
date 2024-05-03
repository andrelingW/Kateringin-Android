package project.skripsi.kateringin.Controller.Helper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import project.skripsi.kateringin.Controller.Authentication.LoginController;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue, null));
        getWindow().getDecorView().setSystemUiVisibility(0);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser == null){
                Intent intent = new Intent(SplashScreen.this, LoginController.class);
                startActivity(intent);
                finish();
            } else{
                DocumentReference docRef = database.collection("user").document(currentUser.getUid());
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", MODE_PRIVATE);

                            User user = new User();
                            user.setName(document.get("name").toString());
                            user.setEmail(document.get("email").toString());
                            user.setPhoneNumber(document.get("phone").toString());
                            user.setBOD(document.get("DOB").toString());
                            if(document.get("profileImage") != null){
                                user.setProfileImageUrl(document.get("profileImage").toString());
                            }
                            user.setGender(document.get("gender").toString());

                            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(user);
                            prefsEditor.putString("userObject", json);
                            prefsEditor.commit();

                            Intent intent = new Intent(getApplicationContext(), MainScreenController.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.i("TAG", "No such document");
                        }
                    } else {
                        Log.i("TAG", "get failed with ", task.getException());
                    }
                });
            }
        }, 3000);
    }
}