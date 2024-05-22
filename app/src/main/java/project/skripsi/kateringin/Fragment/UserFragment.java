package project.skripsi.kateringin.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import project.skripsi.kateringin.Controller.Helper.MainScreenController;
import project.skripsi.kateringin.Controller.Helper.TermsAndConditionController;
import project.skripsi.kateringin.Controller.Store.StoreMainScreenController;
import project.skripsi.kateringin.Controller.StoreRegistration.StoreRegisterStartController;
import project.skripsi.kateringin.Controller.Wallet.WalletController;
import project.skripsi.kateringin.Controller.User.UserController;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogProfileHelp;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogProfileLogout;
import project.skripsi.kateringin.Util.UtilClass.LoadingUtil;

public class UserFragment extends Fragment {

    //XML
    TextView nameTxt, emailTxt, phoneTxt;
    ConstraintLayout editProfile, help, logout, termsAndCondition, cateringMode, wallet;
    ImageView profileImage;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;

    @Override
    public void onResume() {
        super.onResume();
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
        wallet = rootView.findViewById(R.id.profileWallet);
        termsAndCondition = rootView.findViewById(R.id.profileTermsAndCondition);
        cateringMode = rootView.findViewById(R.id.profileCateringMode);
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
                    .thumbnail(requestBuilder)
                    .placeholder(R.drawable.default_image_profile)
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
        cateringMode.setOnClickListener(v -> {
                    DocumentReference docRef = database.collection("user").document(mAuth.getCurrentUser().getUid().toString());
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Boolean check = document.getBoolean("isOwner");

                                if (check) {
                                    CollectionReference collectionRef = database.collection("store");
                                    collectionRef
                                            .whereEqualTo("ownerId", mAuth.getCurrentUser().getUid())
                                            .get().addOnCompleteListener(innerTask -> {
                                                if (innerTask.isSuccessful()) {
                                                    for (QueryDocumentSnapshot innerDocument : innerTask.getResult()) {
                                                        if (innerDocument.exists()) {
                                                            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("sharedPrefer", Context.MODE_PRIVATE);

                                                            Store store = new Store();
                                                            store.setStoreId(innerDocument.getId());
                                                            store.setStoreName(innerDocument.get("storeName").toString());
                                                            store.setStoreDesc(innerDocument.get("storeDescription").toString());
                                                            store.setStorePhone(innerDocument.get("storePhoneNumber").toString());
                                                            if (innerDocument.get("storePhotoUrl") != null) {
                                                                store.setStoreUrlPhoto(innerDocument.get("storePhotoUrl").toString());
                                                            }
                                                            store.setStoreSubDistrict(innerDocument.get("storeSubDistrict").toString());

                                                            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                                                            Gson gson = new Gson();
                                                            String json = gson.toJson(store);
                                                            prefsEditor.putString("storeObject", json);
                                                            prefsEditor.commit();

                                                            Intent intent = new Intent(getActivity(), StoreMainScreenController.class);
                                                            startActivity(intent);
                                                        } else {
                                                            Log.i("TAG", "No such innerDocument");
                                                        }
                                                    }
                                                } else {
                                                    Exception e = task.getException();
                                                    if (e != null) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                } else {
                                    Intent intent = new Intent(getActivity(), StoreRegisterStartController.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    });

        });

        wallet.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), WalletController.class);
            startActivity(intent);
        });
    }
}