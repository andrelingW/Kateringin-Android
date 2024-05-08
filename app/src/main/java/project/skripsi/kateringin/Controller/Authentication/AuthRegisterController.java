package project.skripsi.kateringin.Controller.Authentication;

import static android.content.ContentValues.TAG;

import static project.skripsi.kateringin.Util.UtilClass.LoadingUtil.animateView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.Helper.TermsAndConditionController;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class AuthRegisterController extends AppCompatActivity {

    //XML
    EditText emailTxt, passwordTxt, confirmPasswordTxt;
    CheckBox termAndCondition;
    Button registerButton, backButton;
    TextView tncRedirect, emailAlert, passwordAlert, confirmPasswordAlert, tncAlert;
    View progressOverlay;

    //FIELD
    String email, password, confirmPassword;
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    User user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_register_view);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding();
        button();
    }

    private void binding(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        user = (User) getIntent().getSerializableExtra("USER_OBJECT");
        emailTxt = findViewById(R.id.emailText);
        passwordTxt = findViewById(R.id.auth_register_password_et);
        tncAlert = findViewById(R.id.auth_register_tnc_warning);
        confirmPasswordTxt = findViewById(R.id.auth_register_confirm_password_et);
        termAndCondition = findViewById(R.id.tncCheckbox);
        tncRedirect = findViewById(R.id.auth_register_tnc_tv);
        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
        emailAlert = findViewById(R.id.email_alert);
        passwordAlert = findViewById(R.id.password_alert);
        confirmPasswordAlert = findViewById(R.id.confirm_password_alert);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
    }

    private void button(){
        passwordTxt.setHint("Enter your password");
        passwordTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                passwordTxt.setHint("");
            }else{
                passwordTxt.setHint("Enter your password");
            }
        });

        confirmPasswordTxt.setHint("Re-enter your password");
        confirmPasswordTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                confirmPasswordTxt.setHint("");
            }else{
                confirmPasswordTxt.setHint("Re-enter your password");
            }
        });

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
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(innerTaskVerif -> {
                                    if(innerTaskVerif.isSuccessful()){
                                        Map<String, Object> newUser = new HashMap<>();
                                        newUser.put("name", user.getName());
                                        newUser.put("UID", mAuth.getCurrentUser().getUid());
                                        newUser.put("phone", user.getPhoneNumber());
                                        newUser.put("DOB", user.getBOD());
                                        newUser.put("gender",user.getGender());
                                        newUser.put("profileImage", null);
                                        newUser.put("email", email);
                                        newUser.put("isOwner", false);

                                        database.collection("user").document(mAuth.getCurrentUser().getUid())
                                                .set(newUser)
                                                .addOnCompleteListener(innerTaskAddUser -> {
                                                    Map<String, Object> newWallet = new HashMap<>();
                                                    newWallet.put("userId", mAuth.getCurrentUser().getUid());
                                                    newWallet.put("balance", 0);

                                                    database.collection("wallet").document()
                                                            .set(newWallet)
                                                            .addOnCompleteListener(innerTaskAddWallet -> {
                                                                animateView(progressOverlay, View.GONE, 0, 200);
                                                                Intent intent = new Intent(getApplicationContext(), EmailVerificationController.class);
                                                                startActivity(intent);
                                                                finish();
                                                            })
                                                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                                                })
                                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));


                                    }
                                });
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
        if(!termAndCondition.isChecked()){
            tncAlert.setVisibility(View.VISIBLE);
            status = false;
        }else{
            tncAlert.setVisibility(View.GONE);
        }

        return status;
    }


}