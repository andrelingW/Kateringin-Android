package project.skripsi.kateringin.Util.BottomSheetDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import project.skripsi.kateringin.R;

public class BottomSheetDialogProfileHelp extends BottomSheetDialogFragment {

    AppCompatButton email, whatsapp;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_dialog_profile_help, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        email = view.findViewById(R.id.emailButton);
        whatsapp = view.findViewById(R.id.whatsappButton);


        email.setOnClickListener(v ->{
            String recipientEmail = "andrewsterling78@gmail.com";

            String subject = "Help Email -" + mAuth.getCurrentUser().getUid().toString();

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + recipientEmail)); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            startActivity(intent);


        });

        whatsapp.setOnClickListener(v ->{
            String phoneNumber = "628999533219";
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));
            startActivity(intent);
        });

    }
}
