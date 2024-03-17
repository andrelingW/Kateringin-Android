package project.skripsi.kateringin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import project.skripsi.kateringin.Controller.FoodResultController;
import project.skripsi.kateringin.Controller.LoginController;
import project.skripsi.kateringin.Controller.SearchPageController;
import project.skripsi.kateringin.Controller.SplashScreen;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
        startActivity(intent);
        finish();
    }
}