package project.skripsi.kateringin.Controller.Authentication;

import static android.content.ContentValues.TAG;

import static project.skripsi.kateringin.Util.LoadingUtil.animateView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.Helper.TermsAndConditionController;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class AuthRegisterController extends AppCompatActivity {

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore database;

    //Xml
    EditText emailTxt, passwordTxt, confirmPasswordTxt;
    CheckBox termAndCondition;
    Button registerButton, backButton;
    TextView tncRedirect, emailAlert, passwordAlert, confirmPasswordAlert;
    View progressOverlay;


    //Field
    String email, password, confirmPassword;
    View view;

    //User
    User user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_register_view);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        user = (User) getIntent().getSerializableExtra("USER_OBJECT");

        emailTxt = findViewById(R.id.emailText);
        passwordTxt = findViewById(R.id.auth_register_password_et);
        confirmPasswordTxt = findViewById(R.id.auth_register_confirm_password_et);
        termAndCondition = findViewById(R.id.tncCheckbox);
        tncRedirect = findViewById(R.id.auth_register_tnc_tv);
        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();

        passwordTxt.setHint("Enter your password");
        confirmPasswordTxt.setHint("Re-enter your password");
        passwordTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                passwordTxt.setHint("");
            }else{
                passwordTxt.setHint("Enter your password");
            }
        });

        confirmPasswordTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                confirmPasswordTxt.setHint("");
            }else{
                confirmPasswordTxt.setHint("Re-enter your password");
            }
        });


        emailAlert = findViewById(R.id.email_alert);
        passwordAlert = findViewById(R.id.password_alert);
        confirmPasswordAlert = findViewById(R.id.confirm_password_alert);

        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), RegisterController.class);
            intent.putExtra("USER_OBJECT", user);
            startActivity(intent);
            finish();
        });

        tncRedirect.setOnClickListener(v ->{
            Intent intent = new Intent(this, TermsAndConditionController.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v ->{

            email = emailTxt.getText().toString();
            password = passwordTxt.getText().toString();
            confirmPassword = confirmPasswordTxt.getText().toString();

            if(user != null &&
                    checkData() &&
                    !email.isEmpty() &&
                    !password.isEmpty() &&
                    termAndCondition.isChecked() &&
                    password.equals(confirmPassword)){
                animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            // Create a new user with a first and last name
                                            Map<String, Object> newUser = new HashMap<>();
                                            newUser.put("name", user.getName());
                                            newUser.put("UID", mAuth.getCurrentUser().getUid());
                                            newUser.put("phone", user.getPhoneNumber());
                                            newUser.put("DOB", user.getBOD());
                                            newUser.put("gender",user.getGender());
                                            newUser.put("profileImage", null);
                                            newUser.put("email", email);
                                            newUser.put("isOwner", false);

                                            // Add a new document with a generated ID
                                            database.collection("user").document(mAuth.getCurrentUser().getUid().toString())
                                                    .set(newUser)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            animateView(progressOverlay, View.GONE, 0, 200);
                                                            Intent intent = new Intent(getApplicationContext(), EmailVerificationController.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                                        }else{
//
                                        }
                                    }
                                });
                                // Sign in success, update UI with the signed-in user's information
                            } else {
//
                            }
                        });
            }
        });



    }

    private Boolean checkData() {
        Boolean status = true;
        if(email.isEmpty()){
            emailAlert.setVisibility(View.VISIBLE);
            emailTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            emailAlert.setVisibility(View.GONE);
            emailTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(password.isEmpty()){
            passwordAlert.setVisibility(View.VISIBLE);
            passwordTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            passwordAlert.setVisibility(View.GONE);
            passwordTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(confirmPassword.isEmpty()){
            confirmPasswordAlert.setVisibility(View.VISIBLE);
            confirmPasswordTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            confirmPasswordAlert.setVisibility(View.GONE);
            confirmPasswordTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }

        return status;
    }


}