package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.IdrFormat;

public class ReviewRecycleviewAdapter extends RecyclerView.Adapter<ReviewRecycleviewAdapter.ViewHolder> {

    private ArrayList<Review> reviews;
    private OnClickListener onClickListener;
    private Context context;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    public ReviewRecycleviewAdapter(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_review_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ReviewRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final Review review = reviews.get(position);

        DocumentReference docRef = database.collection("user").document(review.getUserId());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    holder.username.setText(document.getString("name"));
                    Glide.with(context)
                            .load(document.getString("profileImage"))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    holder.userPhoto.setImageDrawable(resource);
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

        holder.rate.setText(review.getRate() +" / 5");
        holder.comment.setText(review.getComment());

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, review);
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Review model);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username, rate, comment;
        ImageView userPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.review_username_tv);
            rate = itemView.findViewById(R.id.review_menu_rating_tv);
            comment = itemView.findViewById(R.id.review_comment_tv);
            userPhoto = itemView.findViewById(R.id.review_item_user_image);
        }
    }
}
