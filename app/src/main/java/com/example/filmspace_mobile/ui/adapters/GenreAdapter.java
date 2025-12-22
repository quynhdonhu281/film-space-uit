package com.example.filmspace_mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private List<Genre> genres = new ArrayList<>();
    private Context context;
    private OnGenreClickListener listener;

    public interface OnGenreClickListener {
        void onGenreClick(Genre genre);
    }

    public GenreAdapter(OnGenreClickListener listener) {
        this.listener = listener;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.categoryTitle.setText(genre.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGenreClick(genre);
            }
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
        }
    }
}
