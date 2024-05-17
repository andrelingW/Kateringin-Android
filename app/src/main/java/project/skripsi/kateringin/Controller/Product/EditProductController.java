package project.skripsi.kateringin.Controller.Product;

import static android.content.ContentValues.TAG;
import static project.skripsi.kateringin.Util.UtilClass.LoadingUtil.animateView;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.Store.StoreMainScreenController;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.ProductRecyclerViewAdapter;

public class EditProductController extends AppCompatActivity {
    //XML
    Button productImageButton;
    ImageView productImage;
    EditText editProductCancelButtonNameEditTxt, editProductCancelButtonPriceEditTxt, editProductCancelButtonDescEditTxt, editProductCancelButtonCalorieEditTxt;
    RadioButton veganRadioButton, riceRadioButton, noodleRadioButton, soupRadioButton, dietRadioButton, porkRadioButton;
    TextView productNameAlert, productPriceAlert, productDescAlert, productCalorieAlert, productCategoryAlert;
    AppCompatButton editProductCancelButton, editProductSaveButton;
    Toolbar toolbar;
    View progressOverlay;
    Bitmap cropped;
    

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    String menuName, menuDescription, menuPhotoUrl, menuId;
    int menuPrice, menuCalorie;
    boolean isVegan, isRice, isNoodle, isSoup, isDiet, isPork, categorySelected;
    Menu foodItem;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product_form_view);
        binding();
        setField();
        customRadioButtonLogic();
        button();
    }

    private void binding(){
        toolbar = findViewById(R.id.store_product_toolbar);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        progressOverlay = findViewById(R.id.progress_overlay);

        productImageButton = findViewById(R.id.store_product_image_button);
        productImage = findViewById(R.id.store_product_imagePicture);

        editProductCancelButtonNameEditTxt = findViewById(R.id.store_product_name_field);
        editProductCancelButtonPriceEditTxt = findViewById(R.id.store_product_price_field);
        editProductCancelButtonDescEditTxt = findViewById(R.id.store_product_description_field);
        editProductCancelButtonCalorieEditTxt = findViewById(R.id.store_product_calorie_field);

        veganRadioButton = findViewById(R.id.add_product_category_vegan);
        riceRadioButton = findViewById(R.id.add_product_category_rice);
        noodleRadioButton = findViewById(R.id.add_product_category_noodle);
        soupRadioButton = findViewById(R.id.add_product_category_soup);
        dietRadioButton = findViewById(R.id.add_product_category_diet);
        porkRadioButton = findViewById(R.id.add_product_category_pork);

        productNameAlert = findViewById(R.id.productNameAlert);
        productPriceAlert = findViewById(R.id.productPriceAlert);
        productDescAlert = findViewById(R.id.productDescAlert);
        productCalorieAlert = findViewById(R.id.productCalorieAlert);
        productCategoryAlert = findViewById(R.id.productCategoryAlert);

        editProductCancelButton = findViewById(R.id.store_product_cancel_button);
        editProductSaveButton = findViewById(R.id.store_product_save_button);

        foodItem = (Menu) getIntent().getSerializableExtra("details_screen");
    }
    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }
    private void setField(){
        progressOverlay.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Edit Product");

        if(foodItem.getMenuPhotoUrl() == null){
            Glide.with(this)
                    .load(R.drawable.menu_placeholder)
                    .into(productImage);
        }else{
            RequestBuilder<Drawable> requestBuilder= Glide.with(productImage.getContext())
                    .asDrawable().sizeMultiplier(0.1f);

            Glide.with(this)
                    .load(foodItem.getMenuPhotoUrl())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .thumbnail(requestBuilder)
                    .placeholder(R.drawable.default_image_profile)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(productImage);
        }

        editProductCancelButtonNameEditTxt.setText(foodItem.getMenuName());
        editProductCancelButtonPriceEditTxt.setText(Integer.toString(foodItem.getMenuPrice()));
        editProductCancelButtonDescEditTxt.setText(foodItem.getMenuDescription());
        editProductCancelButtonCalorieEditTxt.setText(Integer.toString(foodItem.getMenuCalorie()));
        editProductCancelButtonNameEditTxt.setText(foodItem.getMenuName());
        if(foodItem.getVegan()){
            veganRadioButton.setChecked(true);
            categorySelected = true;
        }
        else if(foodItem.getRice()){
            riceRadioButton.setChecked(true);
            categorySelected = true;
        }
        else if(foodItem.getNoodle()){
            noodleRadioButton.setChecked(true);
            categorySelected = true;
        }
        else if(foodItem.getSoup()){
            soupRadioButton.setChecked(true);
            categorySelected = true;
        }
        else if(foodItem.getDiet()){
            dietRadioButton.setChecked(true);
            categorySelected = true;
        }
        else if(foodItem.getPork()){
            porkRadioButton.setChecked(true);
            categorySelected = true;
        }



    }
    private void button(){
        editProductSaveButton.setOnClickListener(v ->{
            animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            if(getAndCheckMenuData()){
                updateMenuData();
            }
        });
        editProductCancelButton.setOnClickListener(v ->{
            finish();
        });
        productImageButton.setOnClickListener(v ->{
            launcher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }
    private void customRadioButtonLogic(){
        veganRadioButton.setOnClickListener(v ->{
            veganRadioButton.setChecked(true);
            riceRadioButton.setChecked(false);
            noodleRadioButton.setChecked(false);
            soupRadioButton.setChecked(false);
            dietRadioButton.setChecked(false);
            porkRadioButton.setChecked(false);
            isVegan = true;
            categorySelected = true;
        });
        riceRadioButton.setOnClickListener(v ->{
            veganRadioButton.setChecked(false);
            riceRadioButton.setChecked(true);
            noodleRadioButton.setChecked(false);
            soupRadioButton.setChecked(false);
            dietRadioButton.setChecked(false);
            porkRadioButton.setChecked(false);
            isRice = true;
            categorySelected = true;
        });
        noodleRadioButton.setOnClickListener(v ->{
            veganRadioButton.setChecked(false);
            riceRadioButton.setChecked(false);
            noodleRadioButton.setChecked(true);
            soupRadioButton.setChecked(false);
            dietRadioButton.setChecked(false);
            porkRadioButton.setChecked(false);
            isNoodle = true;
            categorySelected = true;
        });
        soupRadioButton.setOnClickListener(v ->{
            veganRadioButton.setChecked(false);
            riceRadioButton.setChecked(false);
            noodleRadioButton.setChecked(false);
            soupRadioButton.setChecked(true);
            dietRadioButton.setChecked(false);
            porkRadioButton.setChecked(false);
            isSoup = true;
            categorySelected = true;
        });
        dietRadioButton.setOnClickListener(v ->{
            veganRadioButton.setChecked(false);
            riceRadioButton.setChecked(false);
            noodleRadioButton.setChecked(false);
            soupRadioButton.setChecked(false);
            dietRadioButton.setChecked(true);
            porkRadioButton.setChecked(false);
            isDiet = true;
            categorySelected = true;
        });
        porkRadioButton.setOnClickListener(v ->{
            veganRadioButton.setChecked(false);
            riceRadioButton.setChecked(false);
            noodleRadioButton.setChecked(false);
            soupRadioButton.setChecked(false);
            dietRadioButton.setChecked(false);
            porkRadioButton.setChecked(true);
            isPork = true;
            categorySelected = true;
        });
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
            productImage.setImageBitmap(cropped);
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
    private void updateMenuData(){

        menuId = foodItem.getMenuId();

        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(menuId + ".jpg");
        StorageReference mountainImagesRef = storageRef.child("images/" + menuId + ".jpg");

        mountainsRef.getName().equals(mountainImagesRef.getName());
        mountainsRef.getPath().equals(mountainImagesRef.getPath());

        Bitmap imageBit=((BitmapDrawable)productImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Log.e("ERROR", "uploading image failure");
        }).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {

                menuPhotoUrl = uri.toString();

                Map<String, Object> map = new HashMap<>();
                map.put("menuName", menuName);
                map.put("menuSearch", menuName.toLowerCase());
                map.put("menuDescription", menuDescription);
                map.put("menuPrice", menuPrice);
                map.put("menuCalorie", menuCalorie);
                map.put("isVegan", isVegan);
                map.put("isRice", isRice);
                map.put("isNoodle", isNoodle);
                map.put("isSoup", isSoup);
                map.put("isDiet", isDiet);
                map.put("isPork", isPork);
                map.put("menuPhotoUrl", menuPhotoUrl);

                database.collection("menu").document(menuId)
                        .update(map)
                        .addOnCompleteListener(innerTaskAddUser -> {
                            animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                            Intent intent = new Intent(getApplicationContext(), ProductController.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
            });
        });


    }

    private Boolean getAndCheckMenuData() {
        Boolean status = true;

        menuName = editProductCancelButtonNameEditTxt.getText().toString();
        menuDescription = editProductCancelButtonDescEditTxt.getText().toString();
        if(menuName.isEmpty()){
            productNameAlert.setVisibility(View.VISIBLE);
            editProductCancelButtonNameEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            productNameAlert.setVisibility(View.GONE);
            editProductCancelButtonNameEditTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(menuDescription.isEmpty()){
            productDescAlert.setVisibility(View.VISIBLE);
            editProductCancelButtonDescEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            productDescAlert.setVisibility(View.GONE);
            editProductCancelButtonDescEditTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        try
        {
            menuPrice = Integer.parseInt(editProductCancelButtonPriceEditTxt.getText().toString());
            if(menuPrice <= 0){
                productPriceAlert.setVisibility(View.VISIBLE);
                editProductCancelButtonPriceEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
                status = false;
            }else{
                productPriceAlert.setVisibility(View.GONE);
                editProductCancelButtonPriceEditTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
            }
        }
        catch(NumberFormatException e)
        {
            productPriceAlert.setVisibility(View.VISIBLE);
            editProductCancelButtonPriceEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }
        try
        {
            menuCalorie = Integer.parseInt(editProductCancelButtonCalorieEditTxt.getText().toString());
            if(menuCalorie < 0){
                productCalorieAlert.setVisibility(View.VISIBLE);
                editProductCancelButtonCalorieEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
                status = false;
            }else{
                productCalorieAlert.setVisibility(View.GONE);
                editProductCancelButtonCalorieEditTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
            }
        }
        catch(NumberFormatException e)
        {
            productCalorieAlert.setVisibility(View.VISIBLE);
            editProductCancelButtonCalorieEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }
        if(categorySelected == false)
        {
            productCategoryAlert.setVisibility(View.VISIBLE);
            status = false;
        }
        else
        {
            productCategoryAlert.setVisibility(View.GONE);
        }

        return status;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), ProductController.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
