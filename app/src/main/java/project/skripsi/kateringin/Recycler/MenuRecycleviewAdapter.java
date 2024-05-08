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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import project.skripsi.kateringin.Model.Menu;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class MenuRecycleviewAdapter extends RecyclerView.Adapter<MenuRecycleviewAdapter.ViewHolder> {

    private ArrayList<Menu> foodItems;
    private OnClickListener onClickListener;
    private Context context;

    public MenuRecycleviewAdapter(ArrayList<Menu> foodItems, Context context) {
        this.foodItems = foodItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_food_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final Menu foodItem = foodItems.get(position);
        Glide.with(context)
                .load(foodItem.getMenuPhotoUrl())
                .placeholder(R.drawable.menu_placeholder)
                .into(holder.foodImage);

        holder.foodName.setText(foodItem.getMenuName());
        holder.foodPrice.setText(IdrFormat.format(foodItem.getMenuPrice()));
        holder.foodRate.setText(String.valueOf(foodItem.getMenuRating()));

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, foodItem);
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Menu model);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName;
        TextView foodPrice;
        TextView foodRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.explore_food_image);
            foodName = itemView.findViewById(R.id.explore_food_name);
            foodPrice = itemView.findViewById(R.id.explore_food_price);
            foodRate = itemView.findViewById(R.id.explore_food_rating);
        }
    }
}
