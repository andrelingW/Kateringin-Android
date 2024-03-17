package project.skripsi.kateringin.Controller;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class AuthRegisterController extends AppCompatActivity {

    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore database;

    //Xml
    EditText emailTxt, passwordTxt;
    CheckBox termAndCondition;
    Button registerButton, backButton;

    //Field
    String email, password;

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
        passwordTxt = findViewById(R.id.passwordText);
        termAndCondition = findViewById(R.id.tncCheckbox);

        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), RegisterController.class);
            intent.putExtra("USER_OBJECT", user);
            startActivity(intent);
            finish();
        });

        registerButton.setOnClickListener(v ->{

            email = emailTxt.getText().toString();
            password = passwordTxt.getText().toString();

            if(user != null && !email.isEmpty() && !password.isEmpty() && termAndCondition.isChecked()){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
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

                                                // Add a new document with a generated ID
                                                database.collection("users").document(mAuth.getCurrentUser().getUid().toString())
                                                        .set(newUser)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error writing document", e);
                                                            }
                                                        });
                                                Intent intent = new Intent(getApplicationContext(), EmailVerificationController.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
//                                                displaySnackBar(v);
                                            }
                                        }
                                    });
                                    // Sign in success, update UI with the signed-in user's information
                                } else {
//                                    displaySnackBar(v);
                                }
                            }
                        });
            }
        });



    }
//    void displaySnackBar(View v){
//        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
//
//        // inflate the custom_snackbar_view created previously
//        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar, null);
//
//        // set the background of the default snackbar as transparent
//        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
//
//        // now change the layout of the snackbar
//        @SuppressLint("RestrictedApi")
//        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
//
//        // set padding of the all corners as 0
//        snackbarLayout.setPadding(0, 0, 0, 0);
//
//        // register the button from the custom_snackbar_view layout file
//        Button bGotoWebsite = customSnackView.findViewById(R.id.gotoWebsiteButton);
//
//        // now handle the same button with onClickListener
//        bGotoWebsite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Redirecting to Website", Toast.LENGTH_SHORT).show();
//                snackbar.dismiss();
//            }
//        });
//
//        // add the custom snack bar layout to snackbar layout
//        snackbarLayout.addView(customSnackView, 0);
//
//        snackbar.show();
//    }
}