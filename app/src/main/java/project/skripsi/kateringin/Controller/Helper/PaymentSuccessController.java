package project.skripsi.kateringin.Controller.Helper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseUser;

import project.skripsi.kateringin.Controller.Authentication.LoginController;
import project.skripsi.kateringin.Controller.MainScreenController;
import project.skripsi.kateringin.Fragment.OrderFragment;
import project.skripsi.kateringin.R;

public class PaymentSuccessController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success_view);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainScreenController.class);
            intent.putExtra("fragmentId", R.layout.fragment_order);
            intent.putExtra("menuItemId", R.id.menu_order);
            startActivity(intent);


        }, 3000);
    }
}