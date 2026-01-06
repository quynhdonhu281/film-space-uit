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
import com.example.filmspace_mobile.data.repository.MovieRepository; // Import Repository
import com.example.filmspace_mobile.data.repository.RepositoryCallback; // Import Callback

import java.util.ArrayList;
import java.util.List;

public class MovieHorizontalAdapter extends RecyclerView.Adapter<MovieHorizontalAdapter.ViewHolder> {
    private List<Movie> movies = new ArrayList<>();
    private Context context;
    private OnMovieClickListener listener;

    // [MỚI] Thêm Repository để gọi API
    private MovieRepository movieRepository;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    // [MỚI] Constructor nhận thêm Repository
    public MovieHorizontalAdapter(OnMovieClickListener listener, MovieRepository movieRepository) {
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
        // Đảm bảo tên file layout đúng với file XML bạn vừa sửa
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_film_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // 1. Tên phim
        holder.title.setText(movie.getTitle());

        // 2. Thể loại (Genres)
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            StringBuilder genresText = new StringBuilder();
            for (int i = 0; i < Math.min(2, movie.getGenres().size()); i++) {
                if (i > 0) genresText.append(" • ");
                genresText.append(movie.getGenres().get(i).getName());
            }
            holder.genres.setText(genresText.toString());
            holder.genres.setVisibility(View.VISIBLE);
        } else {
            holder.genres.setVisibility(View.GONE);
        }

        // 3. Rating [MỚI]
        if (holder.tvRating != null) {
            holder.tvRating.setText(String.valueOf(movie.getRating()));
        }

        // 4. Poster Ảnh
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(20));

        Glide.with(context)
                .load(movie.getPosterUrl())
                .apply(requestOptions)
                .placeholder(R.drawable.movie_poster_placeholder)
                .error(R.drawable.movie_poster_placeholder)
                .into(holder.pic);

        // 5. Views Count API [MỚI]
        if (holder.tvViewCount != null && movieRepository != null) {
            holder.tvViewCount.setText("...");
            // Đánh dấu ViewHolder này đang hiển thị phim nào
            holder.itemView.setTag(movie.getId());

            movieRepository.getMovieViews(movie.getId(), new RepositoryCallback<Long>() {
                @Override
                public void onSuccess(Long views) {
                    // Kiểm tra xem ViewHolder còn tồn tại không
                    int currentPos = holder.getBindingAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION) {
                        // Kiểm tra xem ViewHolder có bị tái sử dụng cho phim khác chưa
                        if (holder.itemView.getTag() != null && (int)holder.itemView.getTag() == movie.getId()) {
                            holder.tvViewCount.setText(formatViews(views));
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    // Nếu lỗi thì thôi, giữ nguyên "..." hoặc set về "0 views" tùy bạn
                }
            });
        }

        // Click vào cả thẻ
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMovieClick(movie);
        });

        // Click vào nút Play
        if (holder.btnPlay != null) {
            holder.btnPlay.setOnClickListener(v -> {
                if (listener != null) listener.onMovieClick(movie);
            });
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Hàm format view (1000 -> 1.0K)
    private String formatViews(long views) {
        if (views >= 1000000) return String.format("%.1fM", views / 1000000.0);
        if (views >= 1000) return String.format("%.1fK", views / 1000.0);
        return String.valueOf(views);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView genres;
        ImageView pic;
        ImageView btnPlay; // Đổi tên từ playButton cho khớp XML

        // View mới
        TextView tvRating;
        TextView tvViewCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.filmTitle);
            genres = itemView.findViewById(R.id.filmGenres);
            pic = itemView.findViewById(R.id.pic);
            btnPlay = itemView.findViewById(R.id.btnPlay); // Lưu ý ID này trong XML

            tvRating = itemView.findViewById(R.id.tvRating);
            tvViewCount = itemView.findViewById(R.id.tvViewCount);
        }
    }
}