package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import project.skripsi.kateringin.Fragment.CartFragment;
import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class CartRecycleviewAdapter extends RecyclerView.Adapter<CartRecycleviewAdapter.ViewHolder> {
    private OnDeleteItemClickListener onDeleteItemClickListener;

    private ArrayList<Cart> cartItems;
    private OnClickListener onClickListener;
    private CartFragment fragment;
    private Context context;

    String menuName, storeName, menuPhotoUrl;
    long menuPrice;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;

    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        this.onDeleteItemClickListener = listener;
    }


    public CartRecycleviewAdapter(ArrayList<Cart> cartItems, CartFragment cartFragment, Context context) {
        this.cartItems = cartItems;
        this.fragment = cartFragment;
        this.context = context;
    }

    @NonNull
    @Override
    public CartRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_cart_item_card, parent, false);
        return new ViewHolder(view);
    }
    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(int position);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CartRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final Cart cartItem = cartItems.get(position);

        DocumentReference docRef = database.collection("store").document(cartItem.getStoreId());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    storeName = document.getString("storeName");
                    holder.storeName.setText(storeName);
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


        DocumentReference menuDocRef = database.collection("menu").document(cartItem.getMenuId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    menuName = document.getString("menuName");
                    menuPrice = document.getLong("menuPrice");
                    menuPhotoUrl = document.getString("menuPhotoUrl");
                    holder.foodName.setText(menuName);
                    holder.foodPrice.setText(IdrFormat.format(menuPrice));


                    Glide.with(context)
                            .load(menuPhotoUrl)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    holder.foodImage.setImageDrawable(resource);
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

        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.time.setText(cartItem.getTimeRange());
        holder.date.setText(cartItem.getDate());

        holder.delete.setOnClickListener(v -> {
            if (onDeleteItemClickListener != null) {
                onDeleteItemClickListener.onDeleteItemClick(position);
            }
        });

        holder.quantity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                cartItem.setQuantity(Integer.parseInt(holder.quantity.getText().toString()));
                holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
                notifyDataSetChanged();
                holder.quantity.clearFocus();
                return true;
            }
            return false;
        });

        holder.plus.setOnClickListener(v ->{
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
            notifyDataSetChanged();
            fragment.updateTotalPrice(cartItems);
        });

        holder.mines.setOnClickListener(v ->{
            if(cartItem.getQuantity() > 1){
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
                notifyDataSetChanged();
                fragment.updateTotalPrice(cartItems);
            }

        });

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, cartItem);
            }
        });

    }

    public void removeAt(int position) {
        String id = cartItems.get(position).getCartItemId();

        CollectionReference collectionRef = database.collection("cartItem");
        Query query = collectionRef.whereEqualTo("userId", mAuth.getCurrentUser().getUid());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                database.collection("cartItem").document(id).delete();
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Cart model);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView foodName, foodPrice, storeName, date, time;
        ImageButton plus, mines, delete;
        ImageView foodImage;

        EditText quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.cart_item_foodName);
            foodImage = itemView.findViewById(R.id.cart_item_image);
            foodPrice = itemView.findViewById(R.id.cart_item_foodPrice);
            storeName = itemView.findViewById(R.id.cart_item_storeName);
            quantity = itemView.findViewById(R.id.cart_item_foodQuantity);
            date = itemView.findViewById(R.id.cart_item_date_tv);
            time = itemView.findViewById(R.id.cart_item_time_tv);
            plus = itemView.findViewById(R.id.cart_item_plus_button);
            mines = itemView.findViewById(R.id.cart_item_mines_button);
            delete = itemView.findViewById(R.id.cart_item_delete_button);
        }
    }


    /** CUSTOM FUNCTION **/
    public void updateAllData(FirebaseFirestore database, FirebaseAuth mAuth){
        String userId = mAuth.getCurrentUser().getUid();
        CollectionReference cartCollectionRef = database.collection("cartItem");

        cartCollectionRef.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            for (Cart cart : cartItems){
                                String cartItemId = cart.getCartItemId();
                                if(Objects.equals(cartItemId, documentId)){
                                    updateToFirestore(cartItemId, cart.getQuantity());
                                }
                            }
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void updateToFirestore(String cartItemId, int newQuantity) {
        DocumentReference docRef = database.collection("cartItem").document(cartItemId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", newQuantity);

        docRef.update(updates);
    }

}
