package project.skripsi.kateringin.Util;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Controller.SuccessMessage.WithdrawSuccessController;
import project.skripsi.kateringin.Controller.Wallet.TopUp.TopUpPaymentController;
import project.skripsi.kateringin.R;

public class BottomSheetDialogWithdrawConfirmation extends BottomSheetDialogFragment {

    AppCompatButton submit;
    TextView withdrawUpAmount;
    int value, balance;
    FirebaseFirestore database;
    FirebaseAuth mAuth;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        submit = view.findViewById(R.id.withdraw_confirmation_button);
        withdrawUpAmount = view.findViewById(R.id.withdraw_confirmation_text);

        withdrawUpAmount.setText(IdrFormat.format(value));
        submit.setOnClickListener(v ->{
            CollectionReference collectionReference = database.collection("wallet");
            Query query = collectionReference.whereEqualTo("userId", mAuth.getCurrentUser().getUid());
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        String docId = document.getId();
                        DocumentReference documentReference = database.collection("wallet").document(docId);
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("balance", balance - (value - 1000));
                        documentReference.update(updates).addOnCompleteListener(innerTask -> {
                            //CREATE TRANSACTION HISTORY
                            CollectionReference collectionRef = database.collection("walletHistory");
                            Map<String, Object> data = new HashMap<>();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

                            data.put("userId", mAuth.getCurrentUser().getUid());
                            data.put("transactionType", "withdraw");
                            data.put("timestamp", Timestamp.now());
                            data.put("amount", value - 1000);
                            data.put("date", dateFormat.format(new Date()));

                            collectionRef.document().set(data);
                            Intent intent = new Intent(getActivity(), WithdrawSuccessController.class);
                            startActivity(intent);
                            dismiss();
                        });
                    }
                }
            });


        });
    }
}
