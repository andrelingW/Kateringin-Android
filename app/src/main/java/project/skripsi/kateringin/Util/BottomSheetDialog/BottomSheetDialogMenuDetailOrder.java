package project.skripsi.kateringin.Util.BottomSheetDialog;

import android.app.Dialog;
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

import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.CustomDateValidator;

public class BottomSheetDialogMenuDetailOrder extends BottomSheetDialogFragment {

    EditText calendar, quantity;
    RadioGroup time;
    RadioButton radioButton;
    ImageButton plus, mines;
    String quantityText;
    String timeRange, date;
    Integer quantityCounter = 0;
    AppCompatButton addToCart;
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    Boolean checkRadio;

    public interface BottomSheetListener {
        void addToCartSuccess();
    }

    private BottomSheetListener mListener;

    public void setBottomSheetListener(BottomSheetListener listener) {
        mListener = listener;
    }

    public static BottomSheetDialogMenuDetailOrder newInstance(Menu menu) {
        BottomSheetDialogMenuDetailOrder fragment = new BottomSheetDialogMenuDetailOrder();
        Bundle args = new Bundle();
        args.putSerializable("myObject", menu);
        fragment.setArguments(args);
        return fragment;
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
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkRadio = false;

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        time = view.findViewById(R.id.bottomSheetMenuDetailTimeGroup);
        calendar = view.findViewById(R.id.bottomDialogMenuDetailCalendar);
        quantity = view.findViewById(R.id.bottomSheetMenuDetailQuantity);
        plus = view.findViewById(R.id.bottomSheetMenuDetailPlus);
        mines = view.findViewById(R.id.bottomSheetMenuDetailMines);
        addToCart = view.findViewById(R.id.addToCart);

        Menu menu = (Menu) getArguments().getSerializable("myObject");

        time.setOnCheckedChangeListener((group, checkId) -> {
            radioButton = view.findViewById(checkId);
            if (radioButton != null) {
                if (checkId == R.id.bottomSheetMenuDetailMorningTime) {
                    timeRange = "08:00 - 12:00";
                    checkRadio = true;
                } else if (checkId == R.id.bottomSheetMenuDetailAfternoonTime) {
                    timeRange = "12:00 - 16:00";
                    checkRadio = true;
                } else if (checkId == R.id.bottomSheetMenuDetailNightTime) {
                    timeRange = "16:00 - 20:00";
                    checkRadio = true;
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
                    .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(new CustomDateValidator()).build())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
                    calendar.setText(date);
                }
            });
            materialDatePicker.show(getChildFragmentManager(), "tag");
        });

        addToCart.setOnClickListener(v ->{
            if (mListener != null && checkRadio && date != null) {

                Map<String, Object> newCart = new HashMap<>();
                newCart.put("menuId", menu.getMenuId() );
                newCart.put("storeId", menu.getStoreId() );
                newCart.put("userId", mAuth.getCurrentUser().getUid());
                newCart.put("date", date);
                newCart.put("timeRange", timeRange);
                newCart.put("price", menu.getMenuPrice());
                newCart.put("note", null);
                newCart.put("quantity", Integer.parseInt(quantity.getText().toString()));
                newCart.put("processed", false);

                // Add a new document with a generated ID
                database.collection("cartItem").document().set(newCart);
                mListener.addToCartSuccess();
                dismiss();

            }
            else{
                Toast.makeText(getContext(), "Pilih terlebih dahulu waktu dan tanggal!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
