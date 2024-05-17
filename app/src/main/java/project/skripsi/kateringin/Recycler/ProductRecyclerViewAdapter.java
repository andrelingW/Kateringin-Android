package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    private OnDeleteItemClickListener onDeleteItemClickListener;
    private OnEditItemClickListener onEditItemClickListener;
//    private OnClickListener onClickListener;
    
    private ArrayList<Menu> foodItems;
    private Context context;

    String menuName, menuPhotoUrl;
    long menuPrice;
    private FirebaseStorage storage;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    
    public ProductRecyclerViewAdapter(ArrayList<Menu> foodItems, Context context) {
        this.foodItems = foodItems;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_store_product_item_card, parent, false);
        return new ViewHolder(view);
    }
    
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        final Menu foodItem = foodItems.get(position);


        DocumentReference menuDocRef = database.collection("menu").document(foodItem.getMenuId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    menuName = document.getString("menuName");
                    menuPrice = document.getLong("menuPrice");
                    menuPhotoUrl = document.getString("menuPhotoUrl");
                    holder.storeProductName.setText(menuName);
                    holder.storeProductPrice.setText(IdrFormat.format(menuPrice));

                    Glide.with(context)
                            .load(menuPhotoUrl)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    holder.storeProductImage.setImageDrawable(resource);
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

        holder.storeProductDeleteButton.setOnClickListener(v -> {
            if (onDeleteItemClickListener != null) {
                onDeleteItemClickListener.onDeleteItemClick(position);
            }
        });

        holder.storeProductEditButton.setOnClickListener(v -> {
            if (onEditItemClickListener != null) {
                onEditItemClickListener.onClick(position, foodItem);
            }
        });
        
        Glide.with(context)
                .load(foodItem.getMenuPhotoUrl())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .placeholder(R.drawable.menu_placeholder)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(holder.storeProductImage);

        holder.storeProductName.setText(foodItem.getMenuName());
        holder.storeProductPrice.setText(IdrFormat.format(foodItem.getMenuPrice()));

//        holder.itemView.setOnClickListener(view -> {
//            if (onClickListener != null) {
//                onClickListener.onClick(position, foodItem);
//            }
//        });

    }
    public void removeAt(int position) {
        String id = foodItems.get(position).getMenuId();

        StorageReference imageRef = storage.getReferenceFromUrl(foodItems.get(position).getMenuPhotoUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CollectionReference collectionRef = database.collection("menu");
                Query query = collectionRef.whereEqualTo("storeId", foodItems.get(position).getStoreId());

                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        database.collection("menu").document(id).delete();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
                foodItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, foodItems.size());
            }
        });


    }
    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        this.onDeleteItemClickListener = listener;
    }
    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(int position);
    }
    public void setOnEditItemClickListener(OnEditItemClickListener onEditItemClickListener) {
        this.onEditItemClickListener = onEditItemClickListener;
    }

    public interface OnEditItemClickListener {
        void onClick(int position, Menu model);
    }
//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }
//
//    public interface OnClickListener {
//        void onClick(int position, Menu model);
//    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView storeProductImage;
        TextView storeProductName;
        TextView storeProductPrice;
        ImageButton storeProductEditButton, storeProductDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeProductImage = itemView.findViewById(R.id.store_product_menu_image);
            storeProductName = itemView.findViewById(R.id.store_product_name_text);
            storeProductPrice = itemView.findViewById(R.id.store_product_price);
            storeProductEditButton = itemView.findViewById(R.id.store_product_edit_button);
            storeProductDeleteButton = itemView.findViewById(R.id.store_product_delete_button);
        }
    }

}
