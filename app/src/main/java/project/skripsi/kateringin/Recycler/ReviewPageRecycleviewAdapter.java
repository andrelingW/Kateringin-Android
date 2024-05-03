package project.skripsi.kateringin.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.R;

public class ReviewPageRecycleviewAdapter extends RecyclerView.Adapter<ReviewPageRecycleviewAdapter.ViewHolder> {

    private ArrayList<Review> reviews;
    private OnClickListener onClickListener;
    private Context context;
    FirebaseFirestore database;
    public ReviewPageRecycleviewAdapter(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewPageRecycleviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_review_page_item_card, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ReviewPageRecycleviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Review review = reviews.get(position);
        database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection("menu").document(review.getMenuId());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String storeId = document.getString("storeId");
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

                    DocumentReference menuDocRef = database.collection("store").document(storeId);
                    menuDocRef.get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            DocumentSnapshot document2 = task2.getResult();
                            if (document2.exists()) {

                                holder.storeName.setText(document2.getString("storeName"));
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                e.printStackTrace();
                            }
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


        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            review.setRate((double) rating);
            notifyDataSetChanged();
        });

        holder.note.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                review.setComment(holder.note.getText().toString());
                notifyDataSetChanged();
                holder.note.clearFocus();
                return true;
            }
            return false;
        });

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

        TextView menuName, storeName;
        EditText note;
        ImageView menuPhoto;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.review_page_item_rating_bar);
            menuName = itemView.findViewById(R.id.review_page_item_menu_name);
            storeName = itemView.findViewById(R.id.review_page_item_store_name);
            note = itemView.findViewById(R.id.review_page_item_note);
            menuPhoto = itemView.findViewById(R.id.review_page_item_menu_image);

        }
    }


    /** CUSTOM FUNCTION **/
    public void pushToFirestore(){
        for(Review review : reviews){
            Map<String, Object> newReview = new HashMap<>();
            newReview.put("menuId", review.getMenuId());
            newReview.put("userId", review.getUserId());
            newReview.put("rate", review.getRate());
            newReview.put("detail", review.getComment());

            database.collection("review").document().set(newReview);
        }
    }



}
