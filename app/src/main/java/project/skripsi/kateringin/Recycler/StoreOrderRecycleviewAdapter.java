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

import project.skripsi.kateringin.Controller.StoreOrder.StoreOrderDetailController;
import project.skripsi.kateringin.Fragment.OrderFragment;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class StoreOrderRecycleviewAdapter extends RecyclerView.Adapter<StoreOrderRecycleviewAdapter.ViewHolder> {

    private ArrayList<Order> orderItems;
    private OnClickListener onClickListener;
    private OrderFragment fragment;
    private Context context;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private String menuName;
    private int counter;

    public StoreOrderRecycleviewAdapter(ArrayList<Order> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_store_order_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull StoreOrderRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final Order orderItem = orderItems.get(position);

        holder.orderId.setText("Order ID #" + orderItem.getOrderId());

        if(orderItem.getOrderStatus().equalsIgnoreCase("waiting")){
            holder.orderStatusIndicator.setColorFilter(ContextCompat.getColor(this.context, R.color.blue));
        } else if(orderItem.getOrderStatus().equalsIgnoreCase("ongoing") || orderItem.getOrderStatus().equalsIgnoreCase("shipping")){
            holder.orderStatusIndicator.setColorFilter(ContextCompat.getColor(this.context, R.color.yellow));
        }

        int totalPrice = 0, count = 0;
        String menuId = "";
        for (OrderItem item : orderItem.getOrderItems()) {
            if(count == 0){
                menuId = item.getMenuId();
            }
            totalPrice += item.getPrice() * item.getQuantity();
            count++;
        }
        counter = count;

        holder.orderPrice.setText(IdrFormat.format(totalPrice));

        DocumentReference docRef = database.collection("menu").document(menuId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    menuName = document.getString("menuName");
                    if(counter == 2){
                        holder.menuName.setText(menuName + " +" + (counter-1) + " more item");
                    }
                    else if(counter > 2){
                        holder.menuName.setText(menuName + " +" + (counter-1) + " more items");
                    }
                    else {
                        holder.menuName.setText(menuName);
                    }
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
                Intent intent = new Intent(context, StoreOrderDetailController.class);
                intent.putExtra("DETAILS_SCREEN", orderItem);
                context.startActivity(intent);
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

        TextView orderId, menuName, orderPrice;
        ImageView orderStatusIndicator;
        AppCompatButton detailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatusIndicator = itemView.findViewById(R.id.order_status_indicator);
            orderId = itemView.findViewById(R.id.store_order_item_orderId_text);
            menuName = itemView.findViewById(R.id.store_order_item_menu_name_text);
            orderPrice = itemView.findViewById(R.id.store_order_item_order_price_text);
            detailButton = itemView.findViewById(R.id.store_order_item_detail_button);

        }
    }

}
