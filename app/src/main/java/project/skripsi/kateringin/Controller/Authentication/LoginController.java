package project.skripsi.kateringin.Controller.Authentication;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import project.skripsi.kateringin.Controller.MainScreenController;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class LoginController extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore database;

    //Xml
    EditText emailTxt, passwordTxt;
    Button login;
    TextView register, emailAlert, passwordAlert, forgotPassword;

    String email,password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        emailTxt = findViewById(R.id.loginEmailEdit);
        passwordTxt = findViewById(R.id.loginPasswordEdit);
        register = findViewById(R.id.registerTxt);
        emailAlert = findViewById(R.id.loginEmailAlert);
        passwordAlert = findViewById(R.id.loginPasswordAlert);
        forgotPassword = findViewById(R.id.login_forgot_password);
        login = findViewById(R.id.loginButton);

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
//            email = emailTxt.getText().toString();
//            password = passwordTxt.getText().toString();

            email = "andrewsterling78@gmail.com";
            password = "testing123";
            if(checkData()){
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if(mAuth.getCurrentUser().isEmailVerified()){
                                        saveUserData();
                                        Intent intent = new Intent(getApplicationContext(), MainScreenController.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        showSnackbar(v);
                                    }
                                    // Sign in success, update UI with the signed-in user's information

                                } else {
                                    // If sign in fails, display a message to the user.
                                    showSnackbar(v);
                                }
                            }
                        });
            }
        });
    }

    private void saveUserData(){
        String docId = mAuth.getCurrentUser().getUid();

        DocumentReference docRef = database.collection("users").document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
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
                    } else {
                        Log.i("TAG", "No such document");
                    }
                } else {
                    Log.i("TAG", "get failed with ", task.getException());
                }
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
        // create an instance of the snackbar
        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);

        // inflate the custom_snackbar_view created previously
        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar, null);

        // set the background of the default snackbar as transparent
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // now change the layout of the snackbar
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);


        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);

        snackbar.show();
    }

}
