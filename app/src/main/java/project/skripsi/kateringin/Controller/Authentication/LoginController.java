package project.skripsi.kateringin.Controller.Authentication;

import static project.skripsi.kateringin.Util.LoadingUtil.animateView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    View progressOverlay;

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
        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();

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

//            email = "andrewsterling78@gmail.com";
//            password = "anjing123";
            if(checkData()){
                animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if(mAuth.getCurrentUser().isEmailVerified()){
//                                        saveUserData();
                                        String docId = mAuth.getCurrentUser().getUid();

                                        DocumentReference docRef = database.collection("user").document(docId);
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

                                                        animateView(progressOverlay, View.GONE, 0, 200);
                                                        Intent intent = new Intent(getApplicationContext(), MainScreenController.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Log.i("TAG", "No such document");
                                                    }
                                                } else {
                                                    Log.i("TAG", "get failed with ", task.getException());
                                                }
                                            }
                                        });
//                                        animateView(progressOverlay, View.GONE, 0, 200);
//                                        Intent intent = new Intent(getApplicationContext(), MainScreenController.class);
//                                        startActivity(intent);
//                                        finish();
                                    }else{
                                        animateView(progressOverlay, View.GONE, 0, 200);
                                        showSnackbar(v);
                                    }
                                    // Sign in success, update UI with the signed-in user's information

                                } else {
                                    animateView(progressOverlay, View.GONE, 0, 200);
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

    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }

}
