package project.skripsi.kateringin.Controller.Wallet.TopUp;

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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import project.skripsi.kateringin.Controller.SuccessMessage.PaymentSuccessController;
import project.skripsi.kateringin.Controller.SuccessMessage.TopUpSuccessController;
import project.skripsi.kateringin.Model.TransactionResponse;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Repository.TransactionStatusInterface;
import project.skripsi.kateringin.Util.SdkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TopUpPaymentController extends AppCompatActivity implements TransactionFinishedCallback {

    //XML
    private ConstraintLayout bcaVA, mandiriVA, bniVA, briVA, permataVA, cimbVA;
    Toolbar toolbar;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    String name, phone, email;
    int totalTopUp;
    int currentBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_payment_view);
        bindViews();
        initActionButtons();
        initMidtransSdk();
    }

    private User getUserDataFromStorage(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        User user = gson.fromJson(sharedPreferences.getString("userObject", ""), User.class);

        return user;
    }

    private void bindViews() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        User user = getUserDataFromStorage();
        name = user.getName();
        phone = user.getPhoneNumber();
        email = user.getEmail();
        totalTopUp = (int) getIntent().getSerializableExtra("TOP_UP_AMOUNT") + 1000;
        currentBalance = (int) getIntent().getSerializableExtra("CURRENT_BALANCE");
        toolbar = findViewById(R.id.top_up_payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bcaVA = findViewById(R.id.top_up_payment_bca_va_method);
        mandiriVA = findViewById(R.id.top_up_payment_mandiri_va_method);
        bniVA = findViewById(R.id.top_up_payment_bni_va_method);
        briVA = findViewById(R.id.top_up_payment_bri_va_method);
        permataVA = findViewById(R.id.top_up_payment_permata_va_method);
        cimbVA = findViewById(R.id.top_up_payment_cimb_va_method);
    }

    private void initActionButtons() {

        bcaVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.BANK_TRANSFER_BCA);
        });

        mandiriVA.setOnClickListener(v -> {
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.BANK_TRANSFER_MANDIRI);
        });

        bniVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.BANK_TRANSFER_BNI);
        });

        briVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.BANK_TRANSFER_BRI);
        });

        permataVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.BANK_TRANSFER_PERMATA);
        });

        cimbVA.setOnClickListener(v ->{
            MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.BANK_TRANSFER_OTHER);
        });
    }

    private TransactionRequest initTransactionRequest() {
        TransactionRequest transactionRequestNew = new
                TransactionRequest(String.valueOf(System.currentTimeMillis()), totalTopUp );
        transactionRequestNew.setCustomerDetails(initCustomerDetails());

        ItemDetails itemDetail = new ItemDetails(String.valueOf(System.currentTimeMillis()), Double.valueOf(totalTopUp) , 1, "TOP-UP");
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(itemDetail);
        transactionRequestNew.setItemDetails(itemDetailsList);
        return transactionRequestNew;
    }

    private CustomerDetails initCustomerDetails() {
        CustomerDetails mCustomerDetails = new CustomerDetails();
        mCustomerDetails.setPhone(phone);
        mCustomerDetails.setFirstName(name);
        mCustomerDetails.setEmail(email);

        return mCustomerDetails;
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
                        updateBalanceAndCreateWalletHistory();
                        Intent intent = new Intent(getApplicationContext(), TopUpSuccessController.class);
                        startActivity(intent);
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
    public void updateBalanceAndCreateWalletHistory() {
        //UPDATE WALLET BALANCE
        CollectionReference collectionReference = database.collection("wallet");
        Query query = collectionReference.whereEqualTo("userId", mAuth.getCurrentUser().getUid());

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            DocumentReference documentReference = database.collection("wallet").document(documentId);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("balance", currentBalance + totalTopUp - 1000);
                            documentReference.update(updates);
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });

        //CREATE TRANSACTION HISTORY
        CollectionReference collectionRef = database.collection("walletHistory");
        Map<String, Object> data = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        data.put("userId", mAuth.getCurrentUser().getUid());
        data.put("transactionType", "top-up");
        data.put("timestamp", Timestamp.now());
        data.put("amount", totalTopUp - 1000);
        data.put("date", dateFormat.format(new Date()));

        collectionRef.document().set(data);
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