package project.skripsi.kateringin.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import project.skripsi.kateringin.Controller.Helper.TermsAndConditionController;
import project.skripsi.kateringin.Controller.User.UserController;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.BottomSheetDialogProfileHelp;
import project.skripsi.kateringin.Util.BottomSheetDialogProfileLogout;

public class UserFragment extends Fragment {

    //XML
    TextView nameTxt, emailTxt, phoneTxt;
    ConstraintLayout editProfile, help, logout, termsAndCondition;
    ImageView profileImage;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;

    @Override
    public void onResume() {
        super.onResume();
//        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("userObject", "");
//        User user = gson.fromJson(json, User.class);
//
//        if(user.getProfileImageUrl() == null){
//            Glide.with(this)
//                    .load(R.drawable.default_image_profile)
//                    .into(profileImage);
//        }else{
//            RequestBuilder<Drawable> requestBuilder= Glide.with(profileImage.getContext())
//                    .asDrawable().sizeMultiplier(0.1f);
//
//            Glide.with(this)
//                    .load(user.getProfileImageUrl())
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
//                    .thumbnail(requestBuilder)
//                    .placeholder(R.drawable.default_image_profile)
//                    .apply(RequestOptions.skipMemoryCacheOf(true))
//                    .into(profileImage);
//        }
        setField();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
       binding(rootView);
       setField();
       button();

        return rootView;
    }

    private void binding(View rootView){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        nameTxt = rootView.findViewById(R.id.profile_name);
        emailTxt = rootView.findViewById(R.id.profile_email);
        phoneTxt = rootView.findViewById(R.id.profile_phone_number);
        profileImage = rootView.findViewById(R.id.profile_info_imagePicture);
        editProfile = rootView.findViewById(R.id.editProfileLayout);
        help = rootView.findViewById(R.id.profilelHelp);
        logout = rootView.findViewById(R.id.profileLogout);
        termsAndCondition = rootView.findViewById(R.id.profileTermsAndCondition);
    }

    private void setField(){
        User user = getUserSharedPreference();
        nameTxt.setText(user.getName());
        emailTxt.setText(user.getEmail());
        phoneTxt.setText(user.getPhoneNumber());
        if(user.getProfileImageUrl() == null){
            Glide.with(this)
                    .load(R.drawable.default_image_profile)
                    .into(profileImage);
        }else{
            RequestBuilder<Drawable> requestBuilder= Glide.with(profileImage.getContext())
                    .asDrawable().sizeMultiplier(0.1f);

            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .thumbnail(requestBuilder)
                    .placeholder(R.drawable.default_image_profile)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(profileImage);
        }
    }

    private User getUserSharedPreference(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("userObject", "");
        User user = gson.fromJson(json, User.class);
        return user;
    }

    private void button(){
        editProfile.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), UserController.class);
            startActivity(intent);
        });

        help.setOnClickListener(v ->{
            BottomSheetDialogProfileHelp bottomSheetDialog = new BottomSheetDialogProfileHelp();
            bottomSheetDialog.show(getParentFragmentManager(), bottomSheetDialog.getTag());
        });

        logout.setOnClickListener(v ->{
            BottomSheetDialogProfileLogout bottomSheetDialog = new BottomSheetDialogProfileLogout();
            bottomSheetDialog.show(getParentFragmentManager(), bottomSheetDialog.getTag());
        });

        termsAndCondition.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), TermsAndConditionController.class);
            startActivity(intent);
        });
    }
}