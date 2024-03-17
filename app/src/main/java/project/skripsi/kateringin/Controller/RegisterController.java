package project.skripsi.kateringin.Controller;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class RegisterController extends AppCompatActivity {

    //Xml
    EditText nameTxt,phoneNumberTxt;
    Button nextButton, dateButton;
    RadioButton male,female;
    RadioGroup radioGroup;
    TextView nameAlert, phoneAlert, dateAlert, genderAlert, dateTxt;

    //Field
    String name,phoneNumber,gender;
    String DOB;
    User user;

    @Override
    protected void onResume() {
        super.onResume();

        //Field
        user = (User) getIntent().getSerializableExtra("USER_OBJECT");

        if(user != null){
            nameTxt.setText(user.getName());
            phoneNumberTxt.setText(user.getPhoneNumber());

            DOB = user.getBOD();
            dateTxt.setText(DOB);

            if(user.getGender().equals("male")){
                male.setChecked(true);
            }else if(user.getGender().equals("female")){
                female.setChecked(true);
            }
        }

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_view);

        //Alert
        nameAlert = findViewById(R.id.nameAlert);
        phoneAlert = findViewById(R.id.phoneNumberAlert);
        dateAlert = findViewById(R.id.dateAlert);
        genderAlert = findViewById(R.id.genderAlert);

        //Field
        nameTxt = findViewById(R.id.nameEditText);
        phoneNumberTxt = findViewById(R.id.phoneNumberEditText);
        male = findViewById(R.id.radioButtonMale);
        female = findViewById(R.id.radioButtonFemale);
        dateTxt = findViewById(R.id.dateEditField);
        radioGroup = findViewById(R.id.register_radioGroup);

        //Button
        nextButton = findViewById(R.id.nextButton);
        dateButton = findViewById(R.id.selectDate);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);

            if(radioButton.getText() == "Perempuan"){
                radioButton.setChecked(true);
            }else if(radioButton.getText() == "Laki - Laki"){
                radioButton.setChecked(true);
            }

        });



        dateButton.setOnClickListener(v -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
                        DOB = date;
                        dateTxt.setText(date);
                }
            });
            materialDatePicker.show(getSupportFragmentManager(), "tag");
        });


        nextButton.setOnClickListener(v -> {

            name = nameTxt.getText().toString();
            phoneNumber = phoneNumberTxt.getText().toString();

            //Gender
            if(male.isChecked()){
                gender = "male";
            }else if(female.isChecked()){
                gender = "female";
            }

            if(checkData()){
                user = new User(name,phoneNumber,DOB,gender);
                Intent intent = new Intent(getApplicationContext(), AuthRegisterController.class);
                intent.putExtra("USER_OBJECT", user);
                startActivity(intent);
            }
        });
    }

    private Boolean checkData() {
        Boolean status = true;
        if(name.isEmpty()){
            nameAlert.setVisibility(View.VISIBLE);
            nameTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            nameAlert.setVisibility(View.GONE);
            nameTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(gender == null){
            genderAlert.setVisibility(View.VISIBLE);
            status = false;
        }else{
            genderAlert.setVisibility(View.GONE);
        }
        if(phoneNumber.isEmpty()){
            phoneAlert.setVisibility(View.VISIBLE);
            phoneNumberTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            phoneAlert.setVisibility(View.GONE);
            phoneNumberTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(DOB == null && user.getBOD() == null){
            dateAlert.setVisibility(View.VISIBLE);
            dateTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            dateAlert.setVisibility(View.GONE);
            dateTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }

        return status;
    }
}
