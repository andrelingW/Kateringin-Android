package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;

import project.skripsi.kateringin.Controller.StoreOrder.StoreOrderDeliverController;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class StoreOrderDetailRecycleviewAdapter extends RecyclerView.Adapter<StoreOrderDetailRecycleviewAdapter.ViewHolder> {

    private ArrayList<OrderItem> orderItems;
    private OnClickListener onClickListener;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    String orderId;

    public StoreOrderDetailRecycleviewAdapter(ArrayList<OrderItem> orderItems, Context context, String orderId) {
        this.orderItems = orderItems;
        this.context = context;
        this.orderId = orderId;
    }

    @NonNull
    @Override
    public StoreOrderDetailRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_store_order_detail_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull StoreOrderDetailRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

        holder.deliverButton.setVisibility(View.GONE);
        holder.rescheduleIndicator.setVisibility(View.GONE);
        if(orderItem.getOrderItemStatus().equalsIgnoreCase("waiting")){
//            holder.rescheduleIndicator.setVisibility(View.GONE);
//            holder.deliverButton.setEnabled(false);
//            holder.deliverButton.setBackgroundResource(R.drawable.custom_unactive_button);

        } else if(orderItem.getOrderItemStatus().equalsIgnoreCase("ongoing")){
            holder.deliverButton.setVisibility(View.VISIBLE);
            holder.deliverButton.setEnabled(false);
            holder.deliverButton.setBackgroundResource(R.drawable.custom_unactive_button);

            Long currentTime = System.currentTimeMillis();
            Date date = new Date(currentTime);
            SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy");

            String currDate = sdfDate.format(date);

            if(currDate.equals(orderItem.getDate())){
                holder.deliverButton.setEnabled(true);
                holder.deliverButton.setBackgroundResource(R.drawable.custom_active_button);
            }

            if(orderItem.getReschedule() != null){
                if(orderItem.getReschedule()){
                    holder.rescheduleIndicator.setVisibility(View.VISIBLE);
                }
            }

        } else if(orderItem.getOrderItemStatus().equalsIgnoreCase("shipping")){
            Log.d("TAG", "onBindViewHolderASD: " + orderItem.getReschedule());

            if(orderItem.getReschedule() != null){
                if(orderItem.getReschedule()){
                    holder.rescheduleIndicator.setVisibility(View.VISIBLE);
                }
            }
            holder.deliverButton.setVisibility(View.GONE);
//            holder.deliverButton.setEnabled(false);
//            holder.deliverButton.setBackgroundResource(R.drawable.custom_unactive_button);
        }
        else if(orderItem.getOrderItemStatus().equalsIgnoreCase("canceled") ||
                orderItem.getOrderItemStatus().equalsIgnoreCase("complete")){
            Log.d("TAG", "onBindViewHolderASD: " + orderItem.getReschedule());

            if(orderItem.getReschedule() != null){
                if(orderItem.getReschedule()){
                    holder.rescheduleIndicator.setVisibility(View.VISIBLE);
                }
            }
        }

        if(orderItem.getOrderItemLinkTracker() == null){
            holder.orderLink.setText("-");
        } else{
            holder.orderLink.setText(orderItem.getOrderItemLinkTracker());

        }
        holder.orderLink.setOnLongClickListener(v ->{
            String text = holder.orderLink.getText().toString();
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", text);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        });


        if(orderItem.getNote() == null || orderItem.getNote().isEmpty() || orderItem.getNote().equalsIgnoreCase("")){
            holder.menuNote.setText("-");
        } else{
            holder.menuNote.setText(orderItem.getNote());
        }




        holder.deliverButton.setOnClickListener(v ->{
            Context innerContext = holder.itemView.getContext();
            Intent intent = new Intent(innerContext, StoreOrderDeliverController.class);
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
        AppCompatButton deliverButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menuPhoto = itemView.findViewById(R.id.store_order_detail_item_menu_image);
            menuName = itemView.findViewById(R.id.store_order_detail_item_menu_name);
            quantity = itemView.findViewById(R.id.store_order_detail_item_quantity);
            date = itemView.findViewById(R.id.store_order_detail_item_date);
            timeRange = itemView.findViewById(R.id.store_order_detail_item_time);
            menuPrice = itemView.findViewById(R.id.store_order_detail_item_menu_price);
            deliverButton = itemView.findViewById(R.id.store_order_detail_item_deliver_button);
            rescheduleIndicator = itemView.findViewById(R.id.store_order_detail_reschedule_indicator);
            menuNote = itemView.findViewById(R.id.store_order_detail_item_note);
            orderLink = itemView.findViewById(R.id.store_order_detail_item_order_tracker_link);
            orderStatus = itemView.findViewById(R.id.store_order_detail_item_status);
        }
    }
}
