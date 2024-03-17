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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import project.skripsi.kateringin.R;

public class BottomSheetDialogMenuDetailOrder extends BottomSheetDialogFragment {

    EditText calendar, quantity;
    RadioGroup time;
    ImageButton plus, mines;
    String quantityText;
    Integer quantityCounter = 0;
    AppCompatButton addToCart;

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
        addToCart = view.findViewById(R.id.addToCart);

        addToCart.setOnClickListener(v ->{
            if (mListener != null) {
                mListener.addToCartSuccess();
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        time = view.findViewById(R.id.bottomSheetMenuDetailTimeGroup);
        calendar = view.findViewById(R.id.bottomDialogMenuDetailCalendar);
        quantity = view.findViewById(R.id.bottomSheetMenuDetailQuantity);
        plus = view.findViewById(R.id.bottomSheetMenuDetailPlus);
        mines = view.findViewById(R.id.bottomSheetMenuDetailMines);


        time.setOnCheckedChangeListener((group, checkId) -> {
            RadioButton radioButton = view.findViewById(checkId);
            if (radioButton != null) {
                if (checkId == R.id.bottomSheetMenuDetailMorningTime) {
                    Log.d("RadioButton", "Option 1 selected");
                } else if (checkId == R.id.bottomSheetMenuDetailAfternoonTime) {
                    Log.d("RadioButton", "Option 2 selected");
                } else if (checkId == R.id.bottomSheetMenuDetailNightTime) {
                    Log.d("RadioButton", "Option 3 selected");
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

//        addToCart.setOnClickListener(v ->{
//
//            dismiss();
//
//        });

    }
//    private void showSnackbar() {
//        // Use requireView() to get the root view of the fragment's layout
//        View rootView = requireView();
//
//        // Provide a meaningful message for the Snackbar
//        String message = "Item added to cart";
//
//        // Create the Snackbar with the provided message and duration
//        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
//
//        // Inflate the custom Snackbar layout
//        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar_add_to_cart_success, null);
//
//        // Get the Snackbar layout
//        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
//
//        // Set the background of the default Snackbar as transparent
//        snackbarLayout.setBackgroundColor(Color.TRANSPARENT);
//
//        // Add the custom Snackbar layout to the Snackbar layout
//        snackbarLayout.addView(customSnackView, 0);
//
//        // Show the Snackbar
//        snackbar.show();
//    }


//    private void showSnackbar(View v){
//        // create an instance of the snackbar
//        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
//
//        // inflate the custom_snackbar_view created previously
//        View customSnackView = getLayoutInflater().inflate(R.layout.custom_snackbar_add_to_cart_success, null);
//
//        // set the background of the default snackbar as transparent
//        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
//
//        // now change the layout of the snackbar
//        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
//
//        // set padding of the all corners as 0
//        snackbarLayout.setPadding(0, 0, 0, 0);
//
//
//        // add the custom snack bar layout to snackbar layout
//        snackbarLayout.addView(customSnackView, 0);
//
//        snackbar.show();
//    }

}
