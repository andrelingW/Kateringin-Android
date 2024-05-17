package project.skripsi.kateringin.Util.BottomSheetDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.SuccessMessage.StoreWithdrawSuccessController;
import project.skripsi.kateringin.Controller.SuccessMessage.WithdrawSuccessController;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class BottomSheetDialogStoreWithdrawConfirmation extends BottomSheetDialogFragment {

    AppCompatButton submit;
    TextView withdrawUpAmount;
    int value, balance;
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    String storeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            value = bundle.getInt("withdrawAmount");
            balance = bundle.getInt("currentBalance");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_dialog_withdraw_confirmation, container, false);
    }
    private Store getStoreDataFromStorage(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Store store = gson.fromJson(sharedPreferences.getString("storeObject", ""), Store.class);

        return store;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        submit = view.findViewById(R.id.withdraw_confirmation_button);
        withdrawUpAmount = view.findViewById(R.id.withdraw_confirmation_text);

        Store store = getStoreDataFromStorage();
        storeId = store.getStoreId();
        
        withdrawUpAmount.setText(IdrFormat.format(value));
        submit.setOnClickListener(v ->{
            CollectionReference collectionReference = database.collection("wallet");
            Query query = collectionReference.whereEqualTo("userId", storeId);
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        String docId = document.getId();
                        DocumentReference documentReference = database.collection("wallet").document(docId);
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("balance", balance - value);
                        documentReference.update(updates).addOnCompleteListener(innerTask -> {
                            //CREATE TRANSACTION HISTORY
//                            CollectionReference collectionRef = database.collection("walletHistory");
//                            Map<String, Object> data = new HashMap<>();
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
//
//                            data.put("userId", storeId);
//                            data.put("transactionType", "withdraw");
//                            data.put("timestamp", Timestamp.now());
//                            data.put("amount", value);
//                            data.put("date", dateFormat.format(new Date()));

                            CollectionReference collectionRef = database.collection("transaction");
                            Map<String, Object> data = new HashMap<>();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

                            data.put("userId", storeId);
                            data.put("transactionType", "withdraw");
                            data.put("timestamp", Timestamp.now());
                            data.put("amount", value);
                            data.put("date", dateFormat.format(new Date()));

                            collectionRef.document().set(data);
                            Intent intent = new Intent(getActivity(), StoreWithdrawSuccessController.class);
                            startActivity(intent);
                            dismiss();
                        });
                    }
                }
            });


        });
    }
}
