package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.RecyclerviewItem.CartItem;
import project.skripsi.kateringin.RecyclerviewItem.CheckOutItem;

public class CheckOutRecyclerviewAdapter extends RecyclerView.Adapter<CheckOutRecyclerviewAdapter.ViewHolder>{
    private ArrayList<CheckOutItem> checkOutItems;
    private CheckOutRecyclerviewAdapter.OnClickListener onClickListener;
    private Context context;

    public CheckOutRecyclerviewAdapter(ArrayList<CheckOutItem> checkOutItems, Context context) {
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

        final CheckOutItem checkOutItem = checkOutItems.get(position);
        holder.foodName.setText(checkOutItems.get(position).getFoodName());
//
//        final FoodItem foodItem = foodItems.get(position);
//        holder.foodImage.setImageURI(foodItems.get(position).getImageUrl());
//        holder.foodName.setText(foodItems.get(position).getFoodName());
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onClickListener != null) {
//                    onClickListener.onClick(position, foodItem);
//                }
//            }
//        });

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, checkOutItem);
            }
        });


//        holder.foodPrice.setText(foodItems.get(position).getPrice().toString());
    }

    public void setOnClickListener(CheckOutRecyclerviewAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, CheckOutItem model);
    }

    @Override
    public int getItemCount() {
        return checkOutItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView foodName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.cartFoodName);
        }
    }
}
