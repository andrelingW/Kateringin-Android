package project.skripsi.kateringin.Controller.Helper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import project.skripsi.kateringin.Controller.Authentication.LoginController;
import project.skripsi.kateringin.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
//        startActivity(intent);
//        finish();

        new Handler().postDelayed(() -> {
            // Create an intent to launch the desired activity
            Intent intent1 = new Intent(SplashScreen.this, LoginController.class);
            startActivity(intent1);
            finish(); // Finish the current activity
        }, 3000);
    }
}