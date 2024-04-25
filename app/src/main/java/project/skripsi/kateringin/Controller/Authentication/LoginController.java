package project.skripsi.kateringin.Controller.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.LoadingUtil;

public class LoginController extends AppCompatActivity {

    //XML
    EditText emailTxt, passwordTxt;
    Button login;
    TextView register, emailAlert, passwordAlert, forgotPassword;
    View progressOverlay;

    //FIELD
    String email,password;
    FirebaseAuth mAuth;
    FirebaseFirestore database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);
        binding();
        button();
    }

    private void binding(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        emailTxt = findViewById(R.id.loginEmailEdit);
        passwordTxt = findViewById(R.id.loginPasswordEdit);
        register = findViewById(R.id.registerTxt);
        emailAlert = findViewById(R.id.loginEmailAlert);
        passwordAlert = findViewById(R.id.loginPasswordAlert);
        forgotPassword = findViewById(R.id.login_forgot_password);
        login = findViewById(R.id.loginButton);
        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
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

        forgotPassword.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordController.class);
            startActivity(intent);
            finish();
        });

        register.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), RegisterController.class);
            startActivity(intent);
            finish();
        });

        login.setOnClickListener(v ->{
            email = emailTxt.getText().toString();
            password = passwordTxt.getText().toString();

            if(checkData()){
                LoadingUtil.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    String docId = mAuth.getCurrentUser().getUid();
                                    DocumentReference docRef = database.collection("user").document(docId);

                                    docRef.get().addOnCompleteListener(innerTask -> {
                                        if (innerTask.isSuccessful()) {
                                            DocumentSnapshot document = innerTask.getResult();
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

                                                LoadingUtil.animateView(progressOverlay, View.GONE, 0, 200);
                                                Intent intent = new Intent(getApplicationContext(), MainScreenController.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Log.i("TAG", "No such document");
                                            }
                                        } else {
                                            Log.i("TAG", "get failed with ", innerTask.getException());
                                        }
                                    });
                                }else{
                                    LoadingUtil.animateView(progressOverlay, View.GONE, 0, 200);
                                    showSnackbar(v);
                                }
                            } else {
                                LoadingUtil.animateView(progressOverlay, View.GONE, 0, 200);
                                showSnackbar(v);
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
        return status;
    }

    private void showSnackbar(View v){
        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(customSnackView, 0);
        snackbar.show();
    }

}
