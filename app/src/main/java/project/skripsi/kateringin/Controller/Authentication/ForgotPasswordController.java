package project.skripsi.kateringin.Controller.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import project.skripsi.kateringin.R;

public class ForgotPasswordController extends AppCompatActivity {

    EditText email;

    Button resetButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_controller);

        email = findViewById(R.id.forgot_password_email);
        resetButton = findViewById(R.id.resetPasswordButton);

        Toolbar toolbar = findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(" ");

        mAuth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(v ->{
            resetPassword();
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

    private void resetPassword() {
        String strEmail = email.getText().toString();
        mAuth.sendPasswordResetEmail(strEmail)
                .addOnSuccessListener(unused -> finish())
                .addOnFailureListener(e -> Toast.makeText(ForgotPasswordController.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}