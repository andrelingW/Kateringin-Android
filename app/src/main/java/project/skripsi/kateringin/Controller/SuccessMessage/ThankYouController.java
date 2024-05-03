package project.skripsi.kateringin.Controller.SuccessMessage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.R;

public class ThankYouController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_controller);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue, null));
        getWindow().getDecorView().setSystemUiVisibility(0);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainScreenController.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}