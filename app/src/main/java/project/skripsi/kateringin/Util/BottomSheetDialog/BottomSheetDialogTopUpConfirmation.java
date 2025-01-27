package project.skripsi.kateringin.Util.BottomSheetDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import project.skripsi.kateringin.Controller.Wallet.TopUp.TopUpPaymentController;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class BottomSheetDialogTopUpConfirmation extends BottomSheetDialogFragment {

    AppCompatButton submit, cancel;
    TextView topUpAmount;
    int value, balance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            value = bundle.getInt("topUpAmount");
            balance = bundle.getInt("currentBalance");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_dialog_topup_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submit = view.findViewById(R.id.top_up_confirmation_button);
        topUpAmount = view.findViewById(R.id.top_up_confirmation_text);
        cancel = view.findViewById(R.id.top_up_confirmation_cancel_button);

        topUpAmount.setText(IdrFormat.format(value+1000));
        cancel.setOnClickListener(v ->{
            dismiss();
        });
        submit.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), TopUpPaymentController.class);
            intent.putExtra("CURRENT_BALANCE", balance);
            intent.putExtra("TOP_UP_AMOUNT", value);
            startActivity(intent);
            dismiss();
        });
    }
}
