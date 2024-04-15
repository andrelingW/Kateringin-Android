package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.RecyclerviewItem.CartItem;
import project.skripsi.kateringin.RecyclerviewItem.CheckOutItem;

public class CheckOutRecyclerviewAdapter extends RecyclerView.Adapter<CheckOutRecyclerviewAdapter.ViewHolder>{
    private ArrayList<Cart> checkOutItems;
    private CheckOutRecyclerviewAdapter.OnClickListener onClickListener;
    private Context context;

    public CheckOutRecyclerviewAdapter(ArrayList<Cart> checkOutItems, Context context) {
        this.checkOutItems = checkOutItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckOutRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_checkout_item_card, parent, false);
        return new CheckOutRecyclerviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutRecyclerviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Cart checkOutItem = checkOutItems.get(position);
        //IMAGE
//        if(checkOutItem.getProfileImageUrl() == null){
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
        holder.menuName.setText("checkOutItem.getmenu");
        holder.menuPrice.setText(String.valueOf(checkOutItem.getPrice()));
        holder.storeName.setText("test");
        holder.itemQuantity.setText(String.valueOf(checkOutItem.getQuantity()));
        holder.orderDate.setText(String.valueOf(checkOutItem.getDate()));
        holder.orderTime.setText(String.valueOf(checkOutItem.getTimeRange()));

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
