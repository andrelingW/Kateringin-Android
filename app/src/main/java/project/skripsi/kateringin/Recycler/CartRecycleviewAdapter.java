package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import project.skripsi.kateringin.Fragment.CartFragment;
import project.skripsi.kateringin.Model.Cart;
import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.Model.Store;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.RecyclerviewItem.CartItem;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;

public class CartRecycleviewAdapter extends RecyclerView.Adapter<CartRecycleviewAdapter.ViewHolder> {
    private OnDeleteItemClickListener onDeleteItemClickListener;
//    private ArrayList<CartItem> cartItems;

    private ArrayList<Cart> cartItems;
    private OnClickListener onClickListener;
    private CartFragment fragment;

    //TEST
    private Menu menu;
    private Store store;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;

//    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
//        this.onDeleteItemClickListener = listener;
//    }
//
//
//    public CartRecycleviewAdapter(ArrayList<CartItem> cartItems, CartFragment cartFragment) {
//        this.cartItems = cartItems;
//        this.fragment = cartFragment;
//    }
//
//    @NonNull
//    @Override
//    public CartRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_cart_item_card, parent, false);
//        return new ViewHolder(view);
//    }
//    public interface OnDeleteItemClickListener {
//        void onDeleteItemClick(int position);
//    }
//    @Override
//    public void onBindViewHolder(@NonNull CartRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        database = FirebaseFirestore.getInstance();
//        final CartItem cartItem = cartItems.get(position);
//
//        holder.foodName.setText(cartItem.getFoodName());
//        holder.quantity.setText(cartItem.getQuantity().toString());
//        holder.foodPrice.setText(cartItem.getFoodPrice().toString());
//
//        holder.delete.setOnClickListener(v -> {
//            if (onDeleteItemClickListener != null) {
//                onDeleteItemClickListener.onDeleteItemClick(position); // Pass position to listener
//            }
//        });
//
//        holder.plus.setOnClickListener(v ->{
//            cartItem.setQuantity(cartItem.getQuantity() + 1);
//            holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
//            notifyDataSetChanged();
//            fragment.updateTotalPrice();
//        });
//
//
//        holder.mines.setOnClickListener(v ->{
//            if(cartItem.getQuantity() > 1){
//                cartItem.setQuantity(cartItem.getQuantity() - 1);
//                holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
//                notifyDataSetChanged();
//                fragment.updateTotalPrice();
//            }
//
//        });
//
//        holder.itemView.setOnClickListener(view -> {
//            if (onClickListener != null) {
//                onClickListener.onClick(position, cartItem);
//            }
//        });
//
//    }
//
//    public void removeAt(int position) {
//        cartItems.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, cartItems.size());
//    }
//
//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }
//
//    public interface OnClickListener {
//        void onClick(int position, CartItem model);
//    }
//
//    @Override
//    public int getItemCount() {
//        return cartItems.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView foodName, foodPrice, storeName, date, time;
//        ImageButton plus, mines, delete;
//
//        EditText quantity;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            foodName = itemView.findViewById(R.id.cart_item_foodName);
//            foodPrice = itemView.findViewById(R.id.cart_item_foodPrice);
//            storeName = itemView.findViewById(R.id.cart_item_storeName);
//            quantity = itemView.findViewById(R.id.cart_item_foodQuantity);
//            date = itemView.findViewById(R.id.cart_item_date_tv);
//            time = itemView.findViewById(R.id.cart_item_time_tv);
//            plus = itemView.findViewById(R.id.cart_item_plus_button);
//            mines = itemView.findViewById(R.id.cart_item_mines_button);
//            delete = itemView.findViewById(R.id.cart_item_delete_button);
//        }
//    }

    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        this.onDeleteItemClickListener = listener;
    }


    public CartRecycleviewAdapter(ArrayList<Cart> cartItems, CartFragment cartFragment) {
        this.cartItems = cartItems;
        this.fragment = cartFragment;
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
    @Override
    public void onBindViewHolder(@NonNull CartRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final Cart cartItem = cartItems.get(position);

//        getStoreData(cartItem.getStoreId());
//        getMenuData(cartItem.getMenuId());

//        holder.foodName.setText(menu.getMenuName());
//        holder.foodPrice.setText(String.valueOf(menu.getMenuPrice()));
        holder.foodName.setText("menu.getMenuName()");
        holder.foodPrice.setText("String.valueOf(menu.getMenuPrice())");
//        holder.storeName.setText(store.getStoreName());
        holder.storeName.setText("store.getStoreName()");
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.time.setText(cartItem.getTimeRange());
        holder.date.setText(cartItem.getDate());

        holder.delete.setOnClickListener(v -> {
            if (onDeleteItemClickListener != null) {
                onDeleteItemClickListener.onDeleteItemClick(position); // Pass position to listener
            }
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

        EditText quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.cart_item_foodName);
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
    public void updateAllData(){
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
                        // Handle failed fetch
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void updateToFirestore(String cartItemId, int newQuantity) {
        // Reference to the document you want to update in Firestore
        DocumentReference docRef = database.collection("cartItem").document(cartItemId);

        // Update only the quantity field
        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", newQuantity);

        docRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Quantity updated successfully in Firestore
                        // Handle success
                    } else {
                        // Handle failure
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getStoreData(String storeId){

        store = new Store();

        DocumentReference docRef = database.collection("store").document(storeId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    store.setStoreName(document.getString("storeName"));
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
    }

    public void getMenuData(String menuId){
        menu = new Menu();

        DocumentReference docRef = database.collection("menu").document(menuId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    menu.setMenuName(document.getString("menuName"));
                    menu.setMenuPrice(Integer.parseInt(document.getString("menuPrice")));
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
    }
}
