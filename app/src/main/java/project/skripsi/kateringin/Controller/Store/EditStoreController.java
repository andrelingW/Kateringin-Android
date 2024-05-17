package project.skripsi.kateringin.Controller.Store;

import static project.skripsi.kateringin.Util.UtilClass.LoadingUtil.animateView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;

public class EditStoreController extends AppCompatActivity {

    //XML
    ImageView storeProfileImage;
    TextView storeSubDistrictTxtView;
    EditText storeNameEditTxt, storeDescEditTxt, storePhoneNumberEditTxt, storeSubDistrictEditTxt;
    Button save, cancel, changeImage;
    View progressOverlay;
    Bitmap cropped;
    Toolbar toolbar;


    //Field
    ListView listSubdistrictView;
    ArrayList<String> listSubdistrict;
    Dialog dialog;
    
    String docId;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_store_edit_view);
        binding();
        setField();
        subdistrictDropdown();
        button();
    }

    private void binding(){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();

        storeProfileImage = findViewById(R.id.store_edit_profile_info_imagePicture);
        storeNameEditTxt = findViewById(R.id.store_edit_profile_name_field);
        storeDescEditTxt = findViewById(R.id.store_edit_profile_desc_field);
        storePhoneNumberEditTxt = findViewById(R.id.store_edit_profile_phone_field);

        storeSubDistrictTxtView = findViewById(R.id.store_profile_subdistrict_field_view);

        save = findViewById(R.id.store_edit_profile_save_button);
        cancel = findViewById(R.id.store_edit_profile_cancel_button);
        changeImage = findViewById(R.id.store_edit_profile_change_button);

        progressOverlay = findViewById(R.id.progress_overlay);
        toolbar = findViewById(R.id.store_edit_profile_toolbar);
    }
    private void subdistrictDropdown(){
        listSubdistrict = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.array_subdistricts)));
        Collections.sort(listSubdistrict);
        storeSubDistrictTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(EditStoreController.this);
                dialog.setContentView((R.layout.dialog_searchable_spinner));
                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);

                dialog.getWindow().setLayout(width, height);
//                dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                storeSubDistrictEditTxt = dialog.findViewById(R.id.storeSubDistrictEditText);
                listSubdistrictView = dialog.findViewById(R.id.listSubdistrictView);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditStoreController.this, android.R.layout.simple_list_item_1, listSubdistrict);

                listSubdistrictView.setAdapter(adapter);

                storeSubDistrictEditTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listSubdistrictView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        storeSubDistrictTxtView.setText(adapter.getItem(position));
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void setField(){
        progressOverlay.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Edit Store Profile");

        Store store = getStoreDataFromStorage();

        docId = store.getStoreId();

        storeNameEditTxt.setText(store.getStoreName());
        storeDescEditTxt.setText(store.getStoreDesc());
        storePhoneNumberEditTxt.setText(store.getStorePhone());
        storeSubDistrictTxtView.setText(store.getStoreSubDistrict());

        //set image
        if(store.getStoreUrlPhoto() == null){
            Glide.with(this)
                     .load(R.drawable.default_image_profile)
                     .into(storeProfileImage);
        }else{
            RequestBuilder<Drawable> requestBuilder= Glide.with(storeProfileImage.getContext())
                    .asDrawable().sizeMultiplier(0.1f);

            Glide.with(this)
                    .load(store.getStoreUrlPhoto())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .thumbnail(requestBuilder)
                    .placeholder(R.drawable.default_image_profile)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(storeProfileImage);
        }


    }

    private void button(){

        save.setOnClickListener(v ->{
            animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            updateProfile(getStoreDataFromStorage());
        });

        cancel.setOnClickListener(v ->{
            finish();
        });

        changeImage.setOnClickListener(v ->{
            launcher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });


    }

    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }

    private void updateStoreDataToStorage(Store store){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("storeObject", gson.toJson(store));
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
            storeProfileImage.setImageBitmap(cropped);
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

    private void updateProfile(Store store){
        store.setStoreName(storeNameEditTxt.getText().toString());
        store.setStoreDesc(storeDescEditTxt.getText().toString());
        store.setStoreSubDistrict(storeSubDistrictTxtView.getText().toString());
        store.setStorePhone(storePhoneNumberEditTxt.getText().toString());


        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(store.getStoreId() + ".jpg");
        StorageReference mountainImagesRef = storageRef.child("images/" + store.getStoreId() + ".jpg");

        mountainsRef.getName().equals(mountainImagesRef.getName());
        mountainsRef.getPath().equals(mountainImagesRef.getPath());

        Bitmap imageBit=((BitmapDrawable)storeProfileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Log.e("ERROR", "uploading image failure");
        }).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                store.setStoreUrlPhoto(uri.toString());
                // Update to database
                database.collection("store")
                        .document(docId)
                        .update("storePhotoUrl",uri.toString()).addOnCompleteListener(task -> {
                            updateStoreDataToStorage(store);

                            Map<String, Object> map= new HashMap<>();
                            map.put("storeName", store.getStoreName());
                            map.put("storeDescription", store.getStoreDesc());
                            map.put("storeSubDistrict", store.getStoreSubDistrict());
                            map.put("storePhoneNumber", store.getStorePhone());
                            map.put("storePhotoUrl", store.getStoreUrlPhoto());

                            database.collection("store")
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