package project.skripsi.kateringin.Controller.Checkout;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.Gopay;
import com.midtrans.sdk.corekit.models.snap.Shopeepay;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.MidtransController;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.SdkConfig;

public class ChoosePaymentController extends AppCompatActivity implements TransactionFinishedCallback {
    private ImageButton bcaVA, mandiriVA, bniVA, briVA, permataVA, cimbVA;

    FirebaseFirestore database;
    FirebaseAuth mAuth;

    String name, phone, email;
    int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_payment_view);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();



        bindViews();
        initActionButtons();
        initMidtransSdk();
    }

    private TransactionRequest initTransactionRequest() {
        // Create new Transaction Request
        TransactionRequest transactionRequestNew = new
                TransactionRequest(System.currentTimeMillis() + "", 50500.0);
        transactionRequestNew.setCustomerDetails(initCustomerDetails());

        ItemDetails itemDetails1 = new ItemDetails("ITEM_ID_3", 50500.0, 1, "ITEM_NAME_1");
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(itemDetails1);
        transactionRequestNew.setItemDetails(itemDetailsList);
        return transactionRequestNew;
    }

    private CustomerDetails initCustomerDetails() {
        CustomerDetails mCustomerDetails = new CustomerDetails();
        mCustomerDetails.setPhone("aasda");
        mCustomerDetails.setFirstName("asdasd");
        mCustomerDetails.setEmail("dasdasd@gmail.com");

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
                .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // will replace theme on snap theme on MAP
                .setLanguage("en");
        sdkUIFlowBuilder.buildSDK();
        uiKitCustomSetting();
    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if (result.getResponse() != null) {
            switch (result.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:

                    Map<String, Object> newOrder = new HashMap<>();
                    newOrder.put("orderId", result.getResponse().getTransactionId());

                    // Add a new document with a generated ID
                    database.collection("orders").document(mAuth.getCurrentUser().getUid().toString())
                            .set(newOrder)
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
                    Toast.makeText(this, "Transaction Finished. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction Pending. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction Failed. ID: " + result.getResponse().getTransactionId() + ". Message: " + result.getResponse().getStatusMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        } else if (result.isTransactionCanceled()) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show();
        } else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void bindViews() {
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

//        bcaVA.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
//                MidtransSDK.getInstance().startPaymentUiFlow(ChoosePaymentController.this, PaymentMethod.BANK_TRANSFER_BCA);
//            }
//        });

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

    private void uiKitCustomSetting() {
        UIKitCustomSetting uIKitCustomSetting = new UIKitCustomSetting();
        uIKitCustomSetting.setSaveCardChecked(true);
        MidtransSDK.getInstance().setUiKitCustomSetting(uIKitCustomSetting);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


}