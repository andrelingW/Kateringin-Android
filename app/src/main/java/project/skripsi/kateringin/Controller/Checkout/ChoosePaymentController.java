package project.skripsi.kateringin.Controller.Checkout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.models.BillingAddress;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.Controller.SuccessMessage.PaymentSuccessController;
import project.skripsi.kateringin.Model.TransactionResponse;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Repository.TransactionStatusInterface;
import project.skripsi.kateringin.Util.SdkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChoosePaymentController extends AppCompatActivity implements TransactionFinishedCallback {

    //KEY
    private static final String PREF_NAME = "CHECK_OUT_ITEM_PREF";
    private static final String KEY_MY_LIST = "CHECK_OUT_ITEM";

    //XML
    private ConstraintLayout bcaVA, mandiriVA, bniVA, briVA, permataVA, cimbVA;
    Toolbar toolbar;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    String name, phone, email, address;
    int totalPrice, feeLayanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_payment_view);

        bindViews();
        initActionButtons();
        initMidtransSdk();
    }

    private void bindViews() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        name = (String) getIntent().getSerializableExtra("CUSTOMER_NAME");
        phone = (String) getIntent().getSerializableExtra("CUSTOMER_PHONE");
        email = (String) getIntent().getSerializableExtra("CUSTOMER_EMAIL");
        address = (String) getIntent().getSerializableExtra("CUSTOMER_ADDRESS");
        totalPrice = (int) getIntent().getSerializableExtra("TOTAL_PRICE");
        feeLayanan = (int) getIntent().getSerializableExtra("FEE_LAYANAN");
        toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bcaVA = findViewById(R.id.bca_va_method);
        mandiriVA = findViewById(R.id.mandiri_va_method);
        bniVA = findViewById(R.id.bni_va_method);
        briVA = findViewById(R.id.bri_va_method);
        permataVA = findViewById(R.id.permata_va_method);
        cimbVA = findViewById(R.id.cimb_va_method);
    }

    private void initActionButtons() {

        bcaVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(ChoosePaymentController.this, PaymentMethod.BANK_TRANSFER_BCA);
        });

        mandiriVA.setOnClickListener(v -> {
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(ChoosePaymentController.this, PaymentMethod.BANK_TRANSFER_MANDIRI);
        });

        bniVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(ChoosePaymentController.this, PaymentMethod.BANK_TRANSFER_BNI);
        });

        briVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(ChoosePaymentController.this, PaymentMethod.BANK_TRANSFER_BRI);
        });

        permataVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(ChoosePaymentController.this, PaymentMethod.BANK_TRANSFER_PERMATA);
        });

        cimbVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(ChoosePaymentController.this, PaymentMethod.BANK_TRANSFER_OTHER);
        });
    }

    private TransactionRequest initTransactionRequest() {
        TransactionRequest transactionRequestNew = new
                TransactionRequest(String.valueOf(System.currentTimeMillis()), totalPrice);
        transactionRequestNew.setCustomerDetails(initCustomerDetails());

        ArrayList<Cart> listOfCartItems = getCartItems(this);
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();

        for (Cart item : listOfCartItems) {
            DocumentReference menuDocRef = database.collection("menu").document(item.getMenuId());
            menuDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String menuName = document.getString("menuName");
                        ItemDetails itemDetails = new ItemDetails(item.getCartItemId(), (double) item.getPrice(), item.getQuantity(), menuName);
                        itemDetailsList.add(itemDetails);
                    } else {
                        System.out.println("No such document");
                    }
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            });
        }
        transactionRequestNew.setItemDetails(itemDetailsList);
        return transactionRequestNew;
    }

    private CustomerDetails initCustomerDetails() {
        CustomerDetails mCustomerDetails = new CustomerDetails();
        mCustomerDetails.setPhone(phone);
        mCustomerDetails.setFirstName(name);
        mCustomerDetails.setEmail(email);
        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setAddress(address);
        mCustomerDetails.setBillingAddress(billingAddress);

        return mCustomerDetails;
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

    private void initMidtransSdk() {
        String client_key = SdkConfig.MERCHANT_CLIENT_KEY;
        String base_url = SdkConfig.MERCHANT_BASE_CHECKOUT_URL;
        SdkUIFlowBuilder sdkUIFlowBuilder = SdkUIFlowBuilder.init()
                .setClientKey(client_key)
                .setContext(this)
                .setTransactionFinishedCallback(this)
                .setMerchantBaseUrl(base_url)
                .enableLog(true)
                .setLanguage("en");
        sdkUIFlowBuilder.buildSDK();
        uiKitCustomSetting();
    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if(result.getResponse() != null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.sandbox.midtrans.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TransactionStatusInterface service = retrofit.create(TransactionStatusInterface.class);
            Call<TransactionResponse> call = service.getTransactionResponse(result.getResponse().getTransactionId());
            call.enqueue(new Callback<TransactionResponse>() {
                @Override
                public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                    if(Objects.equals(response.body().getTransaction_status(), "settlement")){
                        pushOrder();
                        updateCartItemStatus();
                        clearSharedPreferences(getApplicationContext());
                        Toast.makeText(getApplicationContext(), "Transaction Finished. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), PaymentSuccessController.class);
                        startActivity(intent);
                    }
                    else if(Objects.equals(response.body().getTransaction_status(), "pending")){
                        Toast.makeText(getApplicationContext(), "Transaction pending", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<TransactionResponse> call, Throwable throwable) {

                }
            });
        }else if (result.isTransactionCanceled()) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show();
        } else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uiKitCustomSetting() {
        UIKitCustomSetting uIKitCustomSetting = new UIKitCustomSetting();
        uIKitCustomSetting.setSaveCardChecked(true);
        MidtransSDK.getInstance().setUiKitCustomSetting(uIKitCustomSetting);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public static ArrayList<Cart> getCartItems(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_MY_LIST, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Cart>>() {}.getType();
            List<Cart> cartList = gson.fromJson(json, type);

            return new ArrayList<>(cartList);
        }
        return new ArrayList<>();
    }


    //FUNCTION TO SPLIT AND PUSH ORDER TO FIRESTORE
    public void pushOrder(){
        ArrayList<Cart> orderItemList= getCartItems(this);
        Map<String, ArrayList<OrderItem>> orderMap = splitOrderFromDifferentStore(orderItemList);

        for (Map.Entry<String, ArrayList<OrderItem>> entry : orderMap.entrySet()) {
            ArrayList<OrderItem> itemsInStore = entry.getValue();
            String storeId = entry.getKey();
            pushToFirestore(itemsInStore, storeId);
        }
    }

    //SPLIT ORDER BASED ON STORE
    private Map<String, ArrayList<OrderItem>> splitOrderFromDifferentStore(ArrayList<Cart> orderItemList) {
        Map<String, ArrayList<OrderItem>> orderMap = new HashMap<>();

        for (Cart item : orderItemList) {
            String storeId = item.getStoreId();
            if (!orderMap.containsKey(storeId)) {
                orderMap.put(storeId, new ArrayList<>());
            }
            OrderItem temp = new OrderItem(
                    item.getCartItemId(),
                    item.getMenuId(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getTimeRange(),
                    item.getDate(),
                    item.getNote()
            );
            orderMap.get(storeId).add(temp);
        }
        return orderMap;
    }

    //PUSH ORDER TO FIRESTORE
    public void pushToFirestore(ArrayList<OrderItem> groupedItems, String storeId) {
        CollectionReference collectionRef = database.collection("order");
        Map<String, Object> data = new HashMap<>();

        data.put("receiverName", name);
        data.put("receiverEmail", email);
        data.put("receiverPhone", phone);
        data.put("receiverAddress", address);

        data.put("storeId", storeId);
        data.put("userId", mAuth.getCurrentUser().getUid());
        data.put("orderStatus", "waiting");

        data.put("orderItems", groupedItems);

        collectionRef.document(String.valueOf(System.currentTimeMillis())).set(data);
    }

    //UPDATE CART ITEM STATUS
    public void updateCartItemStatus(){
        ArrayList<Cart> listOfCartItems = getCartItems(this);

        String userId = mAuth.getCurrentUser().getUid();
        CollectionReference cartCollectionRef = database.collection("cartItem");

        cartCollectionRef.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            for (Cart cart : listOfCartItems){
                                String cartItemId = cart.getCartItemId();
                                if(Objects.equals(cartItemId, documentId)){
                                    updateToFirestore(cartItemId);
                                }
                            }
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void updateToFirestore(String cartItemId) {
        DocumentReference docRef = database.collection("cartItem").document(cartItemId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("processed", true);
        docRef.update(updates);
    }

    public static void clearSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

}