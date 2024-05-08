package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import project.skripsi.kateringin.Controller.Order.OrderRescheduleController;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class OrderDetailRecycleviewAdapter extends RecyclerView.Adapter<OrderDetailRecycleviewAdapter.ViewHolder> {

    private ArrayList<OrderItem> orderItems;
    private OnClickListener onClickListener;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    String orderId;

    public OrderDetailRecycleviewAdapter(ArrayList<OrderItem> orderItems, Context context, String orderId) {
        this.orderItems = orderItems;
        this.context = context;
        this.orderId = orderId;
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
        holder.orderStatus.setText(orderItem.getOrderItemStatus());
        if(orderItem.getOrderItemStatus().equalsIgnoreCase("waiting")){
            holder.orderStatus.setBackgroundResource(R.drawable.custom_waiting_detail_button);
            holder.orderStatus.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else if(orderItem.getOrderItemStatus().equalsIgnoreCase("ongoing") ||
                orderItem.getOrderItemStatus().equalsIgnoreCase("shipping")){
            holder.orderStatus.setBackgroundResource(R.drawable.custom_ongoing_detail_button);
            holder.orderStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        }else if(orderItem.getOrderItemStatus().equalsIgnoreCase("canceled")){
            holder.orderStatus.setBackgroundResource(R.drawable.custom_canceled_detail_button);
            holder.orderStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }else if(orderItem.getOrderItemStatus().equalsIgnoreCase("complete")){
            holder.orderStatus.setBackgroundResource(R.drawable.custom_completed_detail_button);
            holder.orderStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

        Log.d("TAG", "onBindViewHolder: " + orderItem.getOrderItemStatus());

        if(orderItem.getOrderItemStatus().equalsIgnoreCase("waiting")){
            holder.rescheduleIndicator.setVisibility(View.GONE);
            holder.rescheduleButton.setEnabled(false);
            holder.rescheduleButton.setBackgroundResource(R.drawable.custom_unactive_button);

        } else if(orderItem.getOrderItemStatus().equalsIgnoreCase("ongoing")){
            if(orderItem.getReschedule()){
                holder.rescheduleIndicator.setVisibility(View.VISIBLE);
                holder.rescheduleButton.setEnabled(false);
                holder.rescheduleButton.setBackgroundResource(R.drawable.custom_unactive_button);
            } else{

                String compareDate = orderItem.getDate();
                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy", new Locale("id", "ID"));
                Date firestoreDate = null;
                try {
                    firestoreDate = format.parse(compareDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Date currentDate = Calendar.getInstance().getTime();

                long differenceInMilliseconds =  firestoreDate.getTime() - currentDate.getTime();
                long differenceInDays = differenceInMilliseconds / (1000 * 60 * 60 * 24);

                boolean isButtonEnabled = differenceInDays > 2;
                if(isButtonEnabled){
                    holder.rescheduleButton.setEnabled(true);
                    holder.rescheduleButton.setBackgroundResource(R.drawable.custom_active_button);
                } else{
                    holder.rescheduleButton.setEnabled(false);
                    holder.rescheduleButton.setBackgroundResource(R.drawable.custom_unactive_button);
                }
                holder.rescheduleIndicator.setVisibility(View.GONE);

            }
        } else if(orderItem.getOrderItemStatus().equalsIgnoreCase("shipping") ||
                orderItem.getOrderItemStatus().equalsIgnoreCase("canceled") ||
                orderItem.getOrderItemStatus().equalsIgnoreCase("complete")){
            holder.rescheduleIndicator.setVisibility(View.GONE);
            holder.rescheduleButton.setEnabled(false);
            holder.rescheduleButton.setBackgroundResource(R.drawable.custom_unactive_button);
        }

        if(orderItem.getOrderItemLinkTracker() == null){
            holder.orderLink.setText("-");
        } else{
            holder.orderLink.setText(orderItem.getOrderItemLinkTracker());
        }


        if(orderItem.getNote() == null || orderItem.getNote().isEmpty() || orderItem.getNote().equalsIgnoreCase("")){
            holder.menuNote.setText("-");
        } else{
            holder.menuNote.setText(orderItem.getNote());
        }




        holder.rescheduleButton.setOnClickListener(v ->{
            Context innerContext = holder.itemView.getContext();
            Intent intent = new Intent(innerContext, OrderRescheduleController.class);
            intent.putExtra("ORDER_DETAIL_ITEM", orderItem);
            intent.putExtra("ORDER_ID", orderId);
            ((Activity) context).startActivityForResult(intent, 3333);
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
        void onClick(int position, OrderItem model);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView menuName, menuPrice, quantity, date,timeRange, rescheduleIndicator, menuNote, orderLink, orderStatus;
        ImageView menuPhoto;
        AppCompatButton rescheduleButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menuPhoto = itemView.findViewById(R.id.order_detail_item_menu_image);
            menuName = itemView.findViewById(R.id.order_detail_item_menu_name);
            quantity = itemView.findViewById(R.id.order_detail_item_quantity);
            date = itemView.findViewById(R.id.order_detail_item_date);
            timeRange = itemView.findViewById(R.id.order_detail_item_time);
            menuPrice = itemView.findViewById(R.id.order_detail_item_menu_price);
            rescheduleButton = itemView.findViewById(R.id.order_detail_item_reschedule_button);
            rescheduleIndicator = itemView.findViewById(R.id.order_detail_reschedule_indicator);
            menuNote = itemView.findViewById(R.id.order_detail_item_note);
            orderLink = itemView.findViewById(R.id.order_detail_item_order_tracker_link);
            orderStatus = itemView.findViewById(R.id.order_detail_item_status);
        }
    }
}
