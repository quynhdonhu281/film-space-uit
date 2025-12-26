package com.example.filmspace_mobile.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Episode;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private List<Episode> episodeList;
    private OnEpisodeClickListener listener;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(Episode episode);
    }

    public EpisodeAdapter(List<Episode> episodeList, OnEpisodeClickListener listener) {
        this.episodeList = episodeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodeList.get(position);
        holder.bind(episode);
    }

    @Override
    public int getItemCount() {
        return episodeList != null ? episodeList.size() : 0;
    }

    public void updateData(List<Episode> newEpisodeList) {
        this.episodeList = newEpisodeList;
        notifyDataSetChanged();
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgEpisodeThumb;
        private TextView tvEpisodeTitle;
        private TextView tvDuration;
        private TextView tvEpisodeDesc;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEpisodeThumb = itemView.findViewById(R.id.imgEpisodeThumb);
            tvEpisodeTitle = itemView.findViewById(R.id.tvEpisodeTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvEpisodeDesc = itemView.findViewById(R.id.tvEpisodeDesc);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEpisodeClick(episodeList.get(position));
                }
            });
        }

        public void bind(Episode episode) {
            tvEpisodeTitle.setText(episode.getTitle());
            tvDuration.setText(episode.getDuration() + " minutes");
            tvEpisodeDesc.setText(episode.getOverview());
            // Glide.with(itemView.getContext())
            //     .load(episode.getThumbnailUrl())
            //     .placeholder(R.drawable.ic_movie_placeholder)
            //     .into(imgEpisodeThumb);
        }
    }
}