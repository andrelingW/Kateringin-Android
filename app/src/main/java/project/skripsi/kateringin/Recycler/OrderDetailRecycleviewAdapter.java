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

import project.skripsi.kateringin.Controller.OrderDetailController;
import project.skripsi.kateringin.Fragment.OrderFragment;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.IdrFormat;

public class OrderDetailRecycleviewAdapter extends RecyclerView.Adapter<OrderDetailRecycleviewAdapter.ViewHolder> {

    private ArrayList<OrderItem> orderItems;
    private OnClickListener onClickListener;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    public OrderDetailRecycleviewAdapter(ArrayList<OrderItem> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderDetailRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_order_detail_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OrderDetailRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        final OrderItem orderItem = orderItems.get(position);

        DocumentReference menuDocRef = database.collection("menu").document(orderItem.getMenuId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    holder.menuName.setText(document.getString("menuName"));

                    Glide.with(context)
                            .load(document.getString("menuPhotoUrl"))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    holder.menuPhoto.setImageDrawable(resource);
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

        holder.quantity.setText("x"+orderItem.getQuantity());
        holder.menuPrice.setText(IdrFormat.format(orderItem.getPrice()));
        holder.date.setText(orderItem.getDate());
        holder.timeRange.setText(orderItem.getTimeRange());
        holder.note.setText(orderItem.getNote());

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
        void onClick(int position, OrderItem model);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView menuName, menuPrice, quantity, date,timeRange, note;
        ImageView menuPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menuPhoto = itemView.findViewById(R.id.order_detail_item_menu_image);
            menuName = itemView.findViewById(R.id.order_detail_item_menu_name);
            quantity = itemView.findViewById(R.id.order_detail_item_quantity);
            date = itemView.findViewById(R.id.order_detail_item_date);
            timeRange = itemView.findViewById(R.id.order_detail_item_time);
            menuPrice = itemView.findViewById(R.id.order_detail_item_menu_price);
            note = itemView.findViewById(R.id.order_detail_item_note);
        }
    }


    /** CUSTOM FUNCTION **/

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

}
