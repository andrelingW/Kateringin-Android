package project.skripsi.kateringin.Controller.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import project.skripsi.kateringin.Controller.Authentication.LoginController;
import project.skripsi.kateringin.R;

public class EmailVerificationController extends AppCompatActivity {

    //XML
    Button backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification_view);
        binding();
        button();
    }

    private void binding(){
        backToLogin = findViewById(R.id.backToLogin);
    }

    private void button(){
        backToLogin.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), LoginController.class);
            startActivity(intent);
            finish();
        });
    }
}