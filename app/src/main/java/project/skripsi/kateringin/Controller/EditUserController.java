package project.skripsi.kateringin.Controller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.LoadingDialog;

public class EditUserController extends AppCompatActivity {

    // Firebase + Other Need
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    FirebaseStorage storage;

    // View
    ImageView profileImage;
    EditText name,phonenumber,dob;
    RadioButton male, female;
    RadioGroup radioGroup;
    Button save, cancel, changeImage, dobButton;

    //Field
    String docId;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Toolbar toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Edit Profile");

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        docId = mAuth.getCurrentUser().getUid();

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

        final LoadingDialog loadingDialog = new LoadingDialog(EditUserController.this);
        setField();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);

            if(radioButton.getText() == "Perempuan"){
                radioButton.setChecked(true);
            }else if(radioButton.getText() == "Laki - Laki"){
                radioButton.setChecked(true);
            }
        });

        save.setOnClickListener(v ->{
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            updateProfile(getUserDataFromStorage());
            loadingDialog.startLoading();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    loadingDialog.dismissDialog();
                    finish();
                    // this code will be executed after 2 seconds
                }
            }, 5000);

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
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
                dob.setText(date);
            });
            materialDatePicker.show(getSupportFragmentManager(), "tag");
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
            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
            profileImage.setImageBitmap(cropped);
        }
    });

    private void setField(){
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
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.i("testing", uri.toString());
                    user.setProfileImageUrl(uri.toString());

                    // Update to database
                    database.collection("users")
                            .document(docId)
                            .update("profileImage",uri.toString());
                }
            });
        });
        updateUserDataToStorage(user);

        Map<String, Object> map= new HashMap<>();
        map.put("name", user.getName());
        map.put("phone", user.getPhoneNumber());
        map.put("gender", user.getGender());
        map.put("DOB", user.getBOD());
        map.put("profileImage", user.getProfileImageUrl());

        // Update to database
        database.collection("users")
                .document(docId)
                .update(map);
    }
}