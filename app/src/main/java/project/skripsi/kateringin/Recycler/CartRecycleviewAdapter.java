package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.RecyclerviewItem.CartItem;
import project.skripsi.kateringin.RecyclerviewItem.FoodItem;

public class CartRecycleviewAdapter extends RecyclerView.Adapter<CartRecycleviewAdapter.ViewHolder> {

    private ArrayList<CartItem> cartItems;
    private OnClickListener onClickListener;
    private Context context;

    public CartRecycleviewAdapter(ArrayList<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CartRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_cart_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final CartItem cartItem = cartItems.get(position);
        holder.foodName.setText(cartItems.get(position).getFoodName());
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
                onClickListener.onClick(position, cartItem);
            }
        });


//        holder.foodPrice.setText(foodItems.get(position).getPrice().toString());
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, CartItem model);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView foodName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.cartFoodName);
        }
    }
}
