package com.example.filmspace_mobile.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Review;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList != null ? reviewList.size() : 0;
    }

    public void updateData(List<Review> newReviewList) {
        this.reviewList = newReviewList;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUserAvatar;
        private TextView tvUserName;
        // private RatingBar ratingBar; // ĐÃ XÓA
        private TextView tvRatingScore; // [MỚI] Để hiện điểm số (VD: 8/10)
        private TextView tvDate;
        private TextView tvComment;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            // ratingBar = itemView.findViewById(R.id.ratingBar); // ĐÃ XÓA
            tvRatingScore = itemView.findViewById(R.id.tvRatingScore); // [MỚI] Ánh xạ ID
            tvDate = itemView.findViewById(R.id.tvDate);
            tvComment = itemView.findViewById(R.id.tvComment);
        }

        public void bind(Review review) {
            tvUserName.setText(review.getUserName());
            tvDate.setText(review.getDate()); // Hoặc getFormattedDate()
            tvComment.setText(review.getComment());

            // [LOGIC MỚI] Hiển thị điểm số dạng "8/10"
            // Ép kiểu int để bỏ số thập phân (8.0 -> 8) cho đẹp
            int rating = (int) review.getRating();
            tvRatingScore.setText(rating + "/10");

            // Glide load ảnh (Uncomment nếu dùng)

            if (review.getUserAvatar() != null) {
                com.bumptech.glide.Glide.with(itemView.getContext())
                    .load(review.getUserAvatar())
                    .placeholder(R.drawable.ic_profile)
                    .into(imgUserAvatar);
            }

        }
    }
}