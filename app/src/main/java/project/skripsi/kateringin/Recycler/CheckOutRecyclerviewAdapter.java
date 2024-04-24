package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.RecyclerviewItem.CartItem;
import project.skripsi.kateringin.RecyclerviewItem.CheckOutItem;
import project.skripsi.kateringin.Util.IdrFormat;

public class CheckOutRecyclerviewAdapter extends RecyclerView.Adapter<CheckOutRecyclerviewAdapter.ViewHolder>{
    private static final String KEY_MY_LIST = "CHECK_OUT_ITEM";

    private ArrayList<Cart> checkOutItems;
    private CheckOutRecyclerviewAdapter.OnClickListener onClickListener;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private SharedPreferences sharedPreferences;



    public CheckOutRecyclerviewAdapter(ArrayList<Cart> checkOutItems, Context context, SharedPreferences sharedPreferences) {
        this.checkOutItems = checkOutItems;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public CheckOutRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_checkout_item_card, parent, false);
        return new CheckOutRecyclerviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutRecyclerviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final Cart checkOutItem = checkOutItems.get(position);

        DocumentReference docRef = database.collection("store").document(checkOutItem.getStoreId());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    holder.storeName.setText(document.getString("storeName"));
                } else {
                    System.out.println("No such document");
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });


        DocumentReference menuDocRef = database.collection("menu").document(checkOutItem.getMenuId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    holder.menuName.setText(document.getString("menuName"));
                    holder.menuPrice.setText(IdrFormat.format(document.getLong("menuPrice").intValue()));
                    Glide.with(context)
                            .load(document.getString("menuPhotoUrl"))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    holder.menuImage.setImageDrawable(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                } else {
                    System.out.println("No such document");
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });

        holder.itemQuantity.setText("x" + checkOutItem.getQuantity());
        holder.orderDate.setText(String.valueOf(checkOutItem.getDate()));
        holder.orderTime.setText(String.valueOf(checkOutItem.getTimeRange()));

        holder.menuNote.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                checkOutItem.setNote(holder.menuNote.getText().toString());
                notifyDataSetChanged();
                Log.d("TAG", "onBindViewHolder: " + checkOutItem.getNote());

                holder.menuNote.clearFocus();
                return true;
            }
            return false;
        });

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, checkOutItem);
            }
        });

    }

    public void setOnClickListener(CheckOutRecyclerviewAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Cart model);
    }

    @Override
    public int getItemCount() {
        return checkOutItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView menuName, storeName, itemQuantity, orderDate, orderTime, menuPrice;
        ImageView menuImage;
        EditText menuNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menuImage = itemView.findViewById(R.id.checkout_item_menu_image);
            menuName = itemView.findViewById(R.id.checkout_item_menu_name);
            menuPrice = itemView.findViewById(R.id.checkout_item_menu_price);
            menuNote = itemView.findViewById(R.id.checkout_item_note);
            storeName = itemView.findViewById(R.id.checkout_item_store_name);
            itemQuantity = itemView.findViewById(R.id.checkout_item_quantity);
            orderDate = itemView.findViewById(R.id.checkout_item_date);
            orderTime = itemView.findViewById(R.id.checkout_item_time);
        }
    }

    public void createSharedPreference(){
        Log.d("TAG", "createSharedPreference: " + checkOutItems);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(checkOutItems);
        prefsEditor.putString(KEY_MY_LIST, json).apply();
        prefsEditor.commit();
    }

    //CUSTOM FUNCTION
//    public void getStoreData(String storeId){
//
//        store = new Store();
//
//        DocumentReference docRef = database.collection("store").document(storeId);
//
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    store.setStoreName(document.getString("storeName"));
//                } else {
//                    System.out.println("No such document");
//                }
//            } else {
//                Exception e = task.getException();
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public void getMenuData(String menuId){
//        menu = new Menu();
//
//        DocumentReference docRef = database.collection("menu").document(menuId);
//
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    menu.setMenuName(document.getString("menuName"));
//                    menu.setMenuPrice(Integer.parseInt(document.getString("menuPrice")));
//                } else {
//                    System.out.println("No such document");
//                }
//            } else {
//                Exception e = task.getException();
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
}
