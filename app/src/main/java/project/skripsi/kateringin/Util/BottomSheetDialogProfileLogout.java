package project.skripsi.kateringin.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import project.skripsi.kateringin.Controller.Authentication.LoginController;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class BottomSheetDialogProfileLogout extends BottomSheetDialogFragment {

    AppCompatButton logout;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_dialog_profile_logout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        logout = view.findViewById(R.id.logOutButton);

        logout.setOnClickListener(v ->{
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().apply();

            Intent intent = new Intent(getActivity(), LoginController.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dismiss();
        });
        // Setup your dialog content here
    }
}
