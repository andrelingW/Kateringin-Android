package project.skripsi.kateringin.Controller;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.Gopay;
import com.midtrans.sdk.corekit.models.snap.Shopeepay;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.SdkConfig;


import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.uikit.external.UiKitApi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MidtransController extends AppCompatActivity implements TransactionFinishedCallback {

    private Button buttonUiKit, buttonDirectCreditCard, buttonDirectBcaVa, buttonDirectMandiriVa,
            buttonDirectBniVa, buttonDirectAtmBersamaVa, buttonDirectPermataVa, buttonPayWithSnapToken;
    private EditText editTextSnapToken;

    FirebaseFirestore database;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midtrans_controller);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        bindViews();
        initActionButtons();
        initMidtransSdk();
    }

    private TransactionRequest initTransactionRequest() {
        // Create new Transaction Request
        TransactionRequest transactionRequestNew = new
                TransactionRequest(System.currentTimeMillis() + "", 36500.0);
        transactionRequestNew.setCustomerDetails(initCustomerDetails());
        transactionRequestNew.setGopay(new Gopay("mysamplesdk:://midtrans"));
        transactionRequestNew.setShopeepay(new Shopeepay("mysamplesdk:://midtrans"));

        ItemDetails itemDetails1 = new ItemDetails("ITEM_ID_1", 36500.0, 1, "ITEM_NAME_1");
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        itemDetailsList.add(itemDetails1);
        transactionRequestNew.setItemDetails(itemDetailsList);
        return transactionRequestNew;
    }

    private CustomerDetails initCustomerDetails() {
        //define customer detail (mandatory for coreflow)
        CustomerDetails mCustomerDetails = new CustomerDetails();
        mCustomerDetails.setPhone("085310102020");
        mCustomerDetails.setFirstName("user fullname");
        mCustomerDetails.setEmail("mail@mail.com");
        mCustomerDetails.setCustomerIdentifier("mail@mail.com");
        return mCustomerDetails;
    }

    private void initMidtransSdk() {
        String client_key = SdkConfig.MERCHANT_CLIENT_KEY;
        String base_url = SdkConfig.MERCHANT_BASE_CHECKOUT_URL;
        SdkUIFlowBuilder sdkUIFlowBuilder = SdkUIFlowBuilder.init()
                .setClientKey(client_key) // client_key is mandatory
                .setContext(this) // context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(base_url)//set merchant url
                .enableLog(true) // enable sdk log
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
//        buttonUiKit = (Button) findViewById(R.id.button_uikit);
//        buttonDirectCreditCard = (Button) findViewById(R.id.button_direct_credit_card);
        buttonDirectBcaVa = (Button) findViewById(R.id.bcaTester);
//        buttonDirectMandiriVa = (Button) findViewById(R.id.button_direct_mandiri_va);
//        buttonDirectBniVa = (Button) findViewById(R.id.button_direct_bni_va);
//        buttonDirectPermataVa = (Button) findViewById(R.id.button_direct_permata_va);
//        buttonDirectAtmBersamaVa = (Button) findViewById(R.id.button_direct_atm_bersama_va);
//        buttonPayWithSnapToken = (Button) findViewById(R.id.button_snap_pay);
//        editTextSnapToken = (EditText) findViewById(R.id.edit_snaptoken);
    }

    private void initActionButtons() {
//        buttonUiKit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
//                MidtransSDK.getInstance().startPaymentUiFlow(MidtransController.this);
//            }
//        });
//
//        buttonDirectCreditCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
//                MidtransSDK.getInstance().startPaymentUiFlow(MidtransController.this, PaymentMethod.CREDIT_CARD);
//            }
//        });
//
//
        buttonDirectBcaVa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
                MidtransSDK.getInstance().startPaymentUiFlow(MidtransController.this, PaymentMethod.BANK_TRANSFER_BCA);
            }
        });
//
//
//        buttonDirectBniVa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
//                MidtransSDK.getInstance().startPaymentUiFlow(MidtransController.this, PaymentMethod.BANK_TRANSFER_BNI);
//            }
//        });
//
//        buttonDirectMandiriVa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
//                MidtransSDK.getInstance().startPaymentUiFlow(MidtransController.this, PaymentMethod.BANK_TRANSFER_MANDIRI);
//            }
//        });
//
//
//        buttonDirectPermataVa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
//                MidtransSDK.getInstance().startPaymentUiFlow(MidtransController.this, PaymentMethod.BANK_TRANSFER_PERMATA);
//            }
//        });
//
//        buttonDirectAtmBersamaVa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
//                MidtransSDK.getInstance().startPaymentUiFlow(MidtransController.this, PaymentMethod.BCA_KLIKPAY);
//            }
//        });
//
//        buttonPayWithSnapToken.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String snaptokenValue = editTextSnapToken.getEditableText().toString();
//                if (snaptokenValue != null) {
//                    MidtransSDK.getInstance().startPaymentUiFlow(MidtransController.this, snaptokenValue);
//                }
//            }
//        });
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