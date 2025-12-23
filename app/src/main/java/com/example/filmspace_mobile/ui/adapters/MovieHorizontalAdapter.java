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

import java.util.ArrayList;
import java.util.List;

public class MovieHorizontalAdapter extends RecyclerView.Adapter<MovieHorizontalAdapter.ViewHolder> {
    private List<Movie> movies = new ArrayList<>();
    private Context context;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieHorizontalAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_film_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.title.setText(movie.getTitle());

        // Set genres
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            StringBuilder genresText = new StringBuilder();
            for (int i = 0; i < Math.min(2, movie.getGenres().size()); i++) {
                if (i > 0) genresText.append(", ");
                genresText.append(movie.getGenres().get(i).getName());
            }
            holder.genres.setText(genresText.toString());
            holder.genres.setVisibility(View.VISIBLE);
        } else {
            holder.genres.setVisibility(View.GONE);
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(20));

        Glide.with(context)
                .load(movie.getPosterUrl())
                .apply(requestOptions)
                .placeholder(R.drawable.movie_poster_placeholder)
                .error(R.drawable.movie_poster_placeholder)
                .into(holder.pic);

        // Click on entire card
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });

        // Click on play button
        holder.playButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView genres;
        ImageView pic;
        ImageView playButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.filmTitle);
            genres = itemView.findViewById(R.id.filmGenres);
            pic = itemView.findViewById(R.id.pic);
            playButton = itemView.findViewById(R.id.playButton);
        }
    }
}
