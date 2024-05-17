package project.skripsi.kateringin.Util.BottomSheetDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import project.skripsi.kateringin.Controller.StoreRegistration.StoreRegisterFormController;
import project.skripsi.kateringin.R;

public class BottomSheetDialogStoreTermAndCondition extends BottomSheetDialogFragment {

    CheckBox storeTermsCheckbox;
    AppCompatButton decline, accept;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_store_terms_and_condition, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storeTermsCheckbox = view.findViewById(R.id.storeTermsCheckbox);
        decline = view.findViewById(R.id.declineStoreTermButton);
        accept = view.findViewById(R.id.acceptStoreTermButton);

        decline.setOnClickListener(v -> dismiss());
        accept.setOnClickListener(v -> {
            if(storeTermsCheckbox.isChecked()){
                Intent intent = new Intent(getActivity(), StoreRegisterFormController.class);
                startActivity(intent);
            }

        });
    }
}
