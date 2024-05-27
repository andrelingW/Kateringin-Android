package project.skripsi.kateringin.Controller.Product;

import static android.content.ContentValues.TAG;
import static project.skripsi.kateringin.Util.UtilClass.LoadingUtil.animateView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.bumptech.glide.Glide;
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
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;

public class AddProductController extends AppCompatActivity {
    //XML
    Button productImageButton;
    ImageView productImage;
    EditText addProductNameEditTxt, addProductPriceEditTxt, addProductDescEditTxt, addProductCalorieEditTxt;
    RadioButton veganRadioButton, riceRadioButton, noodleRadioButton, soupRadioButton, dietRadioButton, porkRadioButton;
    TextView productNameAlert, productPriceAlert, productDescAlert, productCalorieAlert, productCategoryAlert;
    AppCompatButton addProductCancelButton, addProductSaveButton;
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

        addProductNameEditTxt = findViewById(R.id.store_product_name_field);
        addProductPriceEditTxt = findViewById(R.id.store_product_price_field);
        addProductDescEditTxt = findViewById(R.id.store_product_description_field);
        addProductCalorieEditTxt = findViewById(R.id.store_product_calorie_field);

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

        addProductCancelButton = findViewById(R.id.store_product_cancel_button);
        addProductSaveButton = findViewById(R.id.store_product_save_button);
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
        toolbar.setTitle("Add Product");

        Glide.with(this)
                .load(R.drawable.menu_placeholder)
                .into(productImage);
    }
    private void button(){
        addProductSaveButton.setOnClickListener(v ->{
            if(getAndCheckMenuData()){
                animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                insertMenuData();
            }
        });
        addProductCancelButton.setOnClickListener(v ->{
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
    private void insertMenuData(){
        Store store = getStoreDataFromStorage();

        menuId = database.collection("menu").document().getId().toString();

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

                Map<String, Object> newMenu = new HashMap<>();
                newMenu.put("storeId", store.getStoreId());
                newMenu.put("menuName", menuName);
                newMenu.put("menuSearch", menuName.toLowerCase());
                newMenu.put("menuDescription", menuDescription);
                newMenu.put("menuPrice", menuPrice);
                newMenu.put("menuCalorie", menuCalorie);
                newMenu.put("isVegan", isVegan);
                newMenu.put("isRice", isRice);
                newMenu.put("isNoodle", isNoodle);
                newMenu.put("isSoup", isSoup);
                newMenu.put("isDiet", isDiet);
                newMenu.put("isPork", isPork);
                newMenu.put("menuPhotoUrl", menuPhotoUrl);
                newMenu.put("menuRating", 0.0);

                database.collection("menu").document(menuId)
                        .set(newMenu)
                        .addOnCompleteListener(innerTaskAddUser -> {
                            animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                            finish();
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
            });
        });


    }

    private Boolean getAndCheckMenuData() {
        Boolean status = true;

        menuName = addProductNameEditTxt.getText().toString();
        menuDescription = addProductDescEditTxt.getText().toString();
        if(menuName.isEmpty()){
            productNameAlert.setVisibility(View.VISIBLE);
            addProductNameEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            productNameAlert.setVisibility(View.GONE);
            addProductNameEditTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        if(menuDescription.isEmpty()){
            productDescAlert.setVisibility(View.VISIBLE);
            addProductDescEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }else{
            productDescAlert.setVisibility(View.GONE);
            addProductDescEditTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
        }
        try
        {
            menuPrice = Integer.parseInt(addProductPriceEditTxt.getText().toString());
            if(menuPrice <= 0){
                productPriceAlert.setVisibility(View.VISIBLE);
                addProductPriceEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
                status = false;
            }else{
                productPriceAlert.setVisibility(View.GONE);
                addProductPriceEditTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
            }
        }
        catch(NumberFormatException e)
        {
            productPriceAlert.setVisibility(View.VISIBLE);
            addProductPriceEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
            status = false;
        }
        try
        {
            menuCalorie = Integer.parseInt(addProductCalorieEditTxt.getText().toString());
            if(menuCalorie < 0){
                productCalorieAlert.setVisibility(View.VISIBLE);
                addProductCalorieEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
                status = false;
            }else{
                productCalorieAlert.setVisibility(View.GONE);
                addProductCalorieEditTxt.setBackgroundResource(R.drawable.custom_normal_edit_text);
            }
        }
        catch(NumberFormatException e)
        {
            productCalorieAlert.setVisibility(View.VISIBLE);
            addProductCalorieEditTxt.setBackgroundResource(R.drawable.custom_alert_edit_text);
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
