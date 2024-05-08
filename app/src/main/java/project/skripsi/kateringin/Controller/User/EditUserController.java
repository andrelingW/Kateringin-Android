package project.skripsi.kateringin.Controller.User;

import static project.skripsi.kateringin.Util.UtilClass.LoadingUtil.animateView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.CustomBeforeDateValidator;

public class EditUserController extends AppCompatActivity {

    //XML
    ImageView profileImage;
    EditText name,phonenumber,dob;
    RadioButton male, female;
    RadioGroup radioGroup;
    Button save, cancel, changeImage, dobButton;
    View progressOverlay;
    Bitmap cropped;
    Toolbar toolbar;


    //Field
    String docId;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_edit_user);
        binding();
        setField();
        button();
    }

    private void binding(){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        docId = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        profileImage = findViewById(R.id.edit_profile_info_imagePicture);
        name = findViewById(R.id.edit_profile_name_field);
        phonenumber = findViewById(R.id.edit_profile_phoneNumber_field);
        dob = findViewById(R.id.edit_profile_dob_field);
        male = findViewById(R.id.editProfileRadioMale);
        female = findViewById(R.id.editProfileRadioFemale);
        save = findViewById(R.id.edit_profile_save_button);
        cancel = findViewById(R.id.edit_profile_cancel_button);
        changeImage = findViewById(R.id.edit_profile_change_button);
        dobButton = findViewById(R.id.edit_profile_dob_button);
        radioGroup = findViewById(R.id.editProfile_radioGroup);
        progressOverlay = findViewById(R.id.progress_overlay);
        toolbar = findViewById(R.id.edit_profile_toolbar);
    }

    private void setField(){
        progressOverlay.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Edit Profile");

        User user = getUserDataFromStorage();

        name.setText(user.getName());
        phonenumber.setText(user.getPhoneNumber());
        dob.setText(user.getBOD());

        //set image
        if(user.getProfileImageUrl() == null){
            Glide.with(this)
                     .load(R.drawable.default_image_profile)
                     .into(profileImage);
        }else{
            RequestBuilder<Drawable> requestBuilder= Glide.with(profileImage.getContext())
                    .asDrawable().sizeMultiplier(0.1f);

            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .thumbnail(requestBuilder)
                    .placeholder(R.drawable.default_image_profile)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(profileImage);
        }

        //set gender
        if(user.getGender().equals("male")){
            male.setChecked(true);
        }else if(user.getGender().equals("female")){
            female.setChecked(true);
        }
    }

    private void button(){
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);

            if(radioButton.getText() == "Female"){
                radioButton.setChecked(true);
            }else if(radioButton.getText() == "Male"){
                radioButton.setChecked(true);
            }
        });

        save.setOnClickListener(v ->{
            animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            updateProfile(getUserDataFromStorage());
        });

        cancel.setOnClickListener(v ->{
            finish();
        });

        changeImage.setOnClickListener(v ->{
            launcher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        dobButton.setOnClickListener(v ->{
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                    .setTitleText("Select Date")
                    .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(new CustomBeforeDateValidator()).build())
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
                dob.setText(date);
            });
            materialDatePicker.show(getSupportFragmentManager(), "tag");
        });
    }

    private User getUserDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        User user = gson.fromJson(sharedPreferences.getString("userObject", ""), User.class);

        return user;
    }

    private void updateUserDataToStorage(User user){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("userObject", gson.toJson(user));
        prefsEditor.commit();
    }

    // Single mode image picker
    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        if (uri == null) {
            Log.d("PhotoPicker", "Selected URI: " + uri);
        } else {
            cropImageProperties(uri);
        }
    });

    // Set image profile after cropping
    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
            profileImage.setImageBitmap(cropped);
        } else{
            Log.d("TAG", ": "+ result);
        }
    });

    private void cropImageProperties(Uri uri){
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.aspectRatioX = 1;
        cropImageOptions.aspectRatioY = 1;
        cropImageOptions.fixAspectRatio = true;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    private void updateProfile(User user){
        user.setName(name.getText().toString());
        user.setPhoneNumber(phonenumber.getText().toString());
        user.setBOD(dob.getText().toString());

        //gender
        if(male.isChecked()){
            user.setGender("male");
        }else if(female.isChecked()){
            user.setGender("female");
        }

        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(mAuth.getCurrentUser().getUid() + ".jpg");
        StorageReference mountainImagesRef = storageRef.child("images/" + mAuth.getCurrentUser().getUid() + ".jpg");

        mountainsRef.getName().equals(mountainImagesRef.getName());
        mountainsRef.getPath().equals(mountainImagesRef.getPath());

        Bitmap imageBit=((BitmapDrawable)profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Log.e("ERROR", "uploading image failure");
        }).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                user.setProfileImageUrl(uri.toString());
                // Update to database
                database.collection("user")
                        .document(docId)
                        .update("profileImage",uri.toString()).addOnCompleteListener(task -> {
                            updateUserDataToStorage(user);

                            Map<String, Object> map= new HashMap<>();
                            map.put("name", user.getName());
                            map.put("phone", user.getPhoneNumber());
                            map.put("gender", user.getGender());
                            map.put("DOB", user.getBOD());
                            map.put("profileImage", user.getProfileImageUrl());

                            database.collection("user")
                                    .document(docId)
                                    .update(map).addOnCompleteListener(task1 -> {
                                        animateView(progressOverlay, View.GONE, 0, 200);
                                        finish();
                                    });
                        });
            });
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
}