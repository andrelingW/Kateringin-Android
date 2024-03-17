package project.skripsi.kateringin.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import project.skripsi.kateringin.R;

public class EmailVerificationController extends AppCompatActivity {

    Button backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification_view);

        backToLogin = findViewById(R.id.backToLogin);

        backToLogin.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), LoginController.class);
            startActivity(intent);
            finish();
        });
    }
}