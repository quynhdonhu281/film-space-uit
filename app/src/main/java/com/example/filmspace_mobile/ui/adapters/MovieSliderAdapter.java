package com.example.filmspace_mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieSliderAdapter extends RecyclerView.Adapter<MovieSliderAdapter.SliderViewHolder> {
    private List<Movie> movies = new ArrayList<>();
    private ViewPager2 viewPager2;
    private Context context;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieSliderAdapter(ViewPager2 viewPager2, OnMovieClickListener listener) {
        this.viewPager2 = viewPager2;
        this.listener = listener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_movie_slider, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        // Use modulo to create infinite loop effect
        int actualPosition = position % movies.size();
        if (movies.size() > 0) {
            holder.setImage(movies.get(actualPosition));
        }
    }

    @Override
    public int getItemCount() {
        // Return a large number to simulate infinite scrolling
        return movies.size() > 0 ? Integer.MAX_VALUE : 0;
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView movieTitle;
        private TextView movieGenres;
        private Button detailsButton;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            movieGenres = itemView.findViewById(R.id.movieGenres);
            detailsButton = itemView.findViewById(R.id.detailsButton);
        }

        void setImage(Movie movie) {
            // Load image without additional rounded corners since CardView handles it
            Glide.with(context)
                    .load(movie.getPosterUrl())
                    .apply(new RequestOptions().transform(new CenterCrop()))
                    .placeholder(R.drawable.movie_poster_placeholder)
                    .error(R.drawable.movie_poster_placeholder)
                    .into(imageView);

            // Set movie title
            movieTitle.setText(movie.getTitle());

            // Set genres
            if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                StringBuilder genresText = new StringBuilder();
                for (int i = 0; i < Math.min(3, movie.getGenres().size()); i++) {
                    if (i > 0) genresText.append(", ");
                    genresText.append(movie.getGenres().get(i).getName());
                }
                movieGenres.setText(genresText.toString());
                movieGenres.setVisibility(View.VISIBLE);
            } else {
                movieGenres.setVisibility(View.GONE);
            }

            // Set click listeners
            detailsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieClick(movie);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieClick(movie);
                }
            });
        }
    }
}
