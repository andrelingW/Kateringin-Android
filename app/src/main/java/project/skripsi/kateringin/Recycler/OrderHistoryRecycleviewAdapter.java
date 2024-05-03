package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import project.skripsi.kateringin.Controller.Order.OrderDetailController;
import project.skripsi.kateringin.Controller.Order.OrderHistoryDetailController;
import project.skripsi.kateringin.Fragment.OrderFragment;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.R;

public class OrderHistoryRecycleviewAdapter extends RecyclerView.Adapter<OrderHistoryRecycleviewAdapter.ViewHolder> {

    private ArrayList<Order> orderItems;
    private OnClickListener onClickListener;
    private OrderFragment fragment;
    private Context context;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;

    public OrderHistoryRecycleviewAdapter(ArrayList<Order> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderHistoryRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_order_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OrderHistoryRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final Order orderItem = orderItems.get(position);

        holder.orderId.setText("Order No #" + orderItem.getOrderId());
        holder.orderStatus.setText(orderItem.getOrderStatus());

        if(orderItem.getOrderStatus().equalsIgnoreCase("complete")){
            holder.orderStatus.setBackgroundResource(R.drawable.custom_completed_detail_button);
            holder.orderStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if(orderItem.getOrderStatus().equalsIgnoreCase("canceled")){
            holder.orderStatus.setBackgroundResource(R.drawable.custom_canceled_detail_button);
            holder.orderStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

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
                Intent intent = new Intent(context, OrderHistoryDetailController.class);
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

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, storeName, orderStatus;
        AppCompatButton detailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_item_orderId_text);
            storeName = itemView.findViewById(R.id.order_item_store_name_text);
            orderStatus = itemView.findViewById(R.id.order_item_order_status_text);
            detailButton = itemView.findViewById(R.id.order_item_detail_button);

        }
    }

}
