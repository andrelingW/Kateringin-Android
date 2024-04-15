package project.skripsi.kateringin.Util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import project.skripsi.kateringin.R;

public class BottomSheetDialogMenuDetailOrder extends BottomSheetDialogFragment {

    EditText calendar, quantity;
    RadioGroup time;
    ImageButton plus, mines;
    String quantityText;
    String timeRange;
    Integer quantityCounter = 0;
    AppCompatButton addToCart;
    FirebaseAuth mAuth;
    FirebaseFirestore database;

    public interface BottomSheetListener {
        void addToCartSuccess();
    }

    private BottomSheetListener mListener;

    // This method will be called by the hosting Activity to set the listener
    public void setBottomSheetListener(BottomSheetListener listener) {
        mListener = listener;
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_menu_detail_order, container, false);
//        addToCart = view.findViewById(R.id.addToCart);
//        mAuth = FirebaseAuth.getInstance();
//        addToCart.setOnClickListener(v ->{
//            if (mListener != null) {
//                String menuId;
//                String userId;
//                String date;
//                int timeRange;
//                int quantity;
//                boolean processed;
//                Map<String, Object> newCart = new HashMap<>();
//                newCart.put("menuId", "M0001" );
//                newCart.put("userId", mAuth.getCurrentUser().getUid());
//                newCart.put("date", user.getPhoneNumber());
//                newCart.put("DOB", user.getBOD());
//                newCart.put("gender",user.getGender());
//                newCart.put("profileImage", null);
//                newCart.put("email", email);
//
//                // Add a new document with a generated ID
//                database.collection("users").document(mAuth.getCurrentUser().getUid().toString())
//                        .set(newCart)
//                mListener.addToCartSuccess();
//            }
//            dismiss();
//        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        time = view.findViewById(R.id.bottomSheetMenuDetailTimeGroup);
        calendar = view.findViewById(R.id.bottomDialogMenuDetailCalendar);
        quantity = view.findViewById(R.id.bottomSheetMenuDetailQuantity);
        plus = view.findViewById(R.id.bottomSheetMenuDetailPlus);
        mines = view.findViewById(R.id.bottomSheetMenuDetailMines);
        addToCart = view.findViewById(R.id.addToCart);

        time.setOnCheckedChangeListener((group, checkId) -> {
            RadioButton radioButton = view.findViewById(checkId);
            if (radioButton != null) {
                if (checkId == R.id.bottomSheetMenuDetailMorningTime) {
                    timeRange = "08:00 - 12:00";
                } else if (checkId == R.id.bottomSheetMenuDetailAfternoonTime) {
                    timeRange = "12:00 - 16:00";
                } else if (checkId == R.id.bottomSheetMenuDetailNightTime) {
                    timeRange = "16:00 - 20:00";
                }
            }
        });

        plus.setOnClickListener(v ->{
            quantityText = quantity.getText().toString();
            quantityCounter = Integer.parseInt(quantityText);
            quantityCounter += 1;
            quantity.setText(String.format(String.valueOf(quantityCounter)));
        });

        mines.setOnClickListener(v ->{
            quantityText = quantity.getText().toString();
            quantityCounter = Integer.parseInt(quantityText);
            if(quantityCounter == 1){
                quantity.setText("1");
            } else{
                quantityCounter -= 1;
                quantity.setText(String.format(String.valueOf(quantityCounter)));
            }
        });


        calendar.setOnClickListener(v ->{
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(new CustomDateValidator()).build())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
                    calendar.setText(date);
                }
            });
            materialDatePicker.show(getChildFragmentManager(), "tag");
        });

        addToCart.setOnClickListener(v ->{
            if (mListener != null) {

                Map<String, Object> newCart = new HashMap<>();
                newCart.put("cartItemId", "haaha");
                newCart.put("menuId", "M0001" );
                newCart.put("storeId", "M0003" );
                newCart.put("userId", mAuth.getCurrentUser().getUid());
                newCart.put("date", "asdasdasd");
                newCart.put("timeRange", timeRange);
                newCart.put("price", 15000);
                newCart.put("quantity", Integer.parseInt(quantity.getText().toString()));
                newCart.put("processed", false);

                // Add a new document with a generated ID
                database.collection("cartItem").document().set(newCart);
                mListener.addToCartSuccess();
            }
            dismiss();
        });

    }

}
