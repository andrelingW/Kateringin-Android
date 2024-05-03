package project.skripsi.kateringin.Util;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import project.skripsi.kateringin.Controller.Helper.FoodResultFilterController;
import project.skripsi.kateringin.Controller.Wallet.TopUp.TopUpPaymentController;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;

public class BottomSheetDialogFilter extends BottomSheetDialogFragment {

    RadioGroup price;
    RadioButton radioButton;
    AppCompatButton search;
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    Boolean checkRadio;
    int priceFlag = 0;
    String searchMenu;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            searchMenu = bundle.getString("search");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_filter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkRadio = false;

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        price = view.findViewById(R.id.bottom_sheet_dialog_filter_group);
        search = view.findViewById(R.id.bottom_sheet_dialog_filter_button);

        price.setOnCheckedChangeListener((group, checkId) -> {
            radioButton = view.findViewById(checkId);
            if (radioButton != null) {
                if (checkId == R.id.bottom_sheet_dialog_filter_1) {
                    priceFlag = 1;
                    checkRadio = true;
                } else if (checkId == R.id.bottom_sheet_dialog_filter_2) {
                    priceFlag = 2;
                    checkRadio = true;
                } else if (checkId == R.id.bottom_sheet_dialog_filter_3) {
                    priceFlag = 3;
                    checkRadio = true;
                } else if (checkId == R.id.bottom_sheet_dialog_filter_4) {
                    priceFlag = 4;
                    checkRadio = true;
                }
            }
        });

        search.setOnClickListener(v ->{
            if (checkRadio && priceFlag != 0) {

                Intent intent = new Intent(getActivity(), FoodResultFilterController.class);
                intent.putExtra("PRICE_FLAG", priceFlag);
                intent.putExtra("SEARCH", searchMenu);
                startActivity(intent);
                dismiss();

            }
            else{
                Toast.makeText(getContext(), "Pilih terlebih dahulu waktu dan tanggal!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
