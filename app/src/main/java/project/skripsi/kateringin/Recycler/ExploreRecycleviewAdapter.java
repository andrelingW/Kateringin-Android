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
import project.skripsi.kateringin.TESTING.FoodItem;

public class ExploreRecycleviewAdapter extends RecyclerView.Adapter<ExploreRecycleviewAdapter.ViewHolder> {

    private ArrayList<FoodItem> foodItems;
    private OnClickListener onClickListener;
    private Context context;

    public ExploreRecycleviewAdapter(ArrayList<FoodItem> foodItems, Context context) {
        this.foodItems = foodItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ExploreRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_food_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final FoodItem foodItem = foodItems.get(position);
        holder.foodImage.setImageURI(foodItems.get(position).getImageUrl());
        holder.foodName.setText(foodItems.get(position).getFoodName());

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
        void onClick(int position, FoodItem model);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName;
        TextView foodPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.explore_food_image);
            foodName = itemView.findViewById(R.id.explore_food_name);
            foodPrice = itemView.findViewById(R.id.explore_food_price);
        }
    }
}
