package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Order.OrderDetailController;
import project.skripsi.kateringin.Fragment.OrderFragment;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.R;

public class OrderRecycleviewAdapter extends RecyclerView.Adapter<OrderRecycleviewAdapter.ViewHolder> {

    private ArrayList<Order> orderItems;
    private OnClickListener onClickListener;
    private OrderFragment fragment;
    private Context context;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;

    public OrderRecycleviewAdapter(ArrayList<Order> orderItems, OrderFragment orderFragment, Context context) {
        this.orderItems = orderItems;
        this.fragment = orderFragment;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_order_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OrderRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final Order orderItem = orderItems.get(position);

        holder.orderId.setText("Order No #" + orderItem.getOrderId());
        holder.orderStatus.setText(orderItem.getOrderStatus());

        if(orderItem.getOrderStatus().equalsIgnoreCase("waiting")){
            holder.orderStatus.setBackgroundResource(R.drawable.custom_waiting_detail_button);
            holder.orderStatus.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else if(orderItem.getOrderStatus().equalsIgnoreCase("ongoing")){
            holder.orderStatus.setBackgroundResource(R.drawable.custom_ongoing_detail_button);
            holder.orderStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        }

        DocumentReference menuDocRef = database.collection("menu").document(orderItem.getOrderItems().get(0).getMenuId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String menuPhotoUrl = document.getString("menuPhotoUrl");

                    Glide.with(context)
                            .load(menuPhotoUrl)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    holder.orderImage.setImageDrawable(resource);
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

        DocumentReference docRef = database.collection("store").document(orderItem.getStoreId());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String storeName = document.getString("storeName");
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

        holder.detailButton.setOnClickListener(v ->{
                Intent intent = new Intent(context, OrderDetailController.class);
                intent.putExtra("DETAILS_SCREEN", orderItem);
                fragment.someActivityResultLauncher.launch(intent);
        });

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, orderItem);
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Order model);
    }

    public void clear() {
        orderItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, storeName, orderStatus;
        AppCompatButton detailButton;
        ImageView orderImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_item_orderId_text);
            storeName = itemView.findViewById(R.id.order_item_store_name_text);
            orderStatus = itemView.findViewById(R.id.order_item_order_status_text);
            detailButton = itemView.findViewById(R.id.order_item_detail_button);
            orderImage = itemView.findViewById(R.id.order_item_menu_image);

        }
    }

}
