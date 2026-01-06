package com.example.filmspace_mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.repository.MovieRepository;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<Movie> movies = new ArrayList<>();
    private Context context;
    private OnMovieClickListener listener;
    private MovieRepository movieRepository;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(OnMovieClickListener listener, MovieRepository movieRepository) {
        this.listener = listener;
        this.movieRepository = movieRepository;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Đảm bảo layout này khớp với layout Netflix style (có tvRating, tvViewCount)
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_film, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy movie tại vị trí hiện tại (ngay lúc bind)
        Movie movie = movies.get(position);

        // 1. Set Title
        if (movie.getTitle() != null) {
            holder.title.setText(movie.getTitle());
        }

        // 2. Set Rating
        if (holder.tvRating != null) {
            holder.tvRating.setText(String.valueOf(movie.getRating()));
        }

        // 3. Load Ảnh
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(16));

        Glide.with(context)
                .load(movie.getPosterUrl())
                .apply(requestOptions)
                .placeholder(R.drawable.movie_poster_placeholder)
                .error(R.drawable.movie_poster_placeholder)
                .into(holder.pic);

        // 4. GỌI API LẤY VIEW COUNT
        if (holder.tvViewCount != null && movieRepository != null) {
            // Đặt text loading trước khi gọi
            holder.tvViewCount.setText("...");

            // Lưu lại ID của movie đang request để đối chiếu sau này (tránh ViewHolder bị tái sử dụng)
            holder.itemView.setTag(movie.getId());

            movieRepository.getMovieViews(movie.getId(), new RepositoryCallback<Long>() {
                @Override
                public void onSuccess(Long views) {
                    // [QUAN TRỌNG] Kiểm tra xem ViewHolder có còn hợp lệ không
                    int currentPos = holder.getBindingAdapterPosition(); // Thay cho getAdapterPosition() ở bản Android mới
                    if (currentPos == RecyclerView.NO_POSITION) {
                        return; // Item đã bị xóa hoặc không còn tồn tại
                    }

                    // Kiểm tra xem ViewHolder này có còn đang giữ đúng Movie ID mà ta đã request không
                    // (Trường hợp scroll nhanh, ViewHolder bị tái sử dụng cho movie khác trước khi API trả về)
                    if (holder.itemView.getTag() != null && (int)holder.itemView.getTag() == movie.getId()) {
                        holder.tvViewCount.setText(formatViews(views) + " views");
                    }
                }

                @Override
                public void onError(String error) {
                    int currentPos = holder.getBindingAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION) {
                        if (holder.itemView.getTag() != null && (int)holder.itemView.getTag() == movie.getId()) {
                            holder.tvViewCount.setText("0 views");
                        }
                    }
                }
            });
        }

        // Click Listener
        holder.itemView.setOnClickListener(v -> {
            // Khi click, cũng nên kiểm tra lại position thực tế
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && listener != null) {
                // Lấy movie từ danh sách dựa trên position thực tế
                Movie currentMovie = movies.get(currentPos);
                listener.onMovieClick(currentMovie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    private String formatViews(long views) {
        if (views >= 1000000) return String.format("%.1fM", views / 1000000.0);
        if (views >= 1000) return String.format("%.1fK", views / 1000.0);
        return String.valueOf(views);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView pic;
        TextView tvRating;
        TextView tvViewCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.filmTitle);
            pic = itemView.findViewById(R.id.pic);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvViewCount = itemView.findViewById(R.id.tvViewCount);
        }
    }
}