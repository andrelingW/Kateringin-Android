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

import project.skripsi.kateringin.Controller.Review.ReviewController;
import project.skripsi.kateringin.Controller.Wallet.TopUp.TopUpPaymentController;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class BottomSheetDialogAcceptedConfirmation extends BottomSheetDialogFragment {

    AppCompatButton yes, no;
    Order order;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            order = (Order) bundle.getSerializable("ORDER_REVIEW");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_dialog_accepted_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        yes = view.findViewById(R.id.confirmation_accept_button);
        no = view.findViewById(R.id.confirmation_cancel_button);

        yes.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), ReviewController.class);
            intent.putExtra("ORDER_REVIEW", order);
            startActivity(intent);
            dismiss();
            getActivity().finish();
        });
        no.setOnClickListener(v -> dismiss());
    }
}
