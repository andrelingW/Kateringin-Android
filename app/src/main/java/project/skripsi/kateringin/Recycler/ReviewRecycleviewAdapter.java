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

import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.Model.Review;
import project.skripsi.kateringin.R;

public class ReviewRecycleviewAdapter extends RecyclerView.Adapter<ReviewRecycleviewAdapter.ViewHolder> {

    private ArrayList<Review> reviews;
    private OnClickListener onClickListener;
    private Context context;

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
        final Review review = reviews.get(position);

        holder.username.setText("Riska Okta Lestari");
        holder.rate.setText(4+" / 5");
        holder.comment.setText("This text is just for testing purpose");

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.review_username_tv);
            rate = itemView.findViewById(R.id.review_menu_rating_tv);
            comment = itemView.findViewById(R.id.review_comment_tv);
        }
    }


    /** CUSTOM FUNCTION **/

//    public void getStoreData(String storeId){
//
//        store = new Store();
//
//        DocumentReference docRef = database.collection("store").document(storeId);
//
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    store.setStoreName(document.getString("storeName"));
//                } else {
//                    System.out.println("No such document");
//                }
//            } else {
//                Exception e = task.getException();
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

}
