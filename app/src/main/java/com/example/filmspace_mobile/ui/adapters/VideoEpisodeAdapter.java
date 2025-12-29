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

public class VideoEpisodeAdapter extends RecyclerView.Adapter<VideoEpisodeAdapter.EpisodeViewHolder> {

    private List<Episode> episodes;
    private OnEpisodeClickListener listener;
    private int currentPlayingPosition = -1;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(Episode episode, int position);
    }

    public VideoEpisodeAdapter(List<Episode> episodes, OnEpisodeClickListener listener) {
        this.episodes = episodes;
        this.listener = listener;
    }

    public void setCurrentPlayingPosition(int position) {
        int previousPosition = currentPlayingPosition;
        currentPlayingPosition = position;

        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(currentPlayingPosition);
    }

    public void updateEpisodes(List<Episode> newEpisodes) {
        this.episodes = newEpisodes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.bind(episode, position == currentPlayingPosition);
    }

    @Override
    public int getItemCount() {
        return episodes != null ? episodes.size() : 0;
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEpisodeNumber;
        private TextView tvEpisodeTitle;
        private TextView tvEpisodeDuration;
        private ImageView ivPlayingIndicator;
        private ImageView ivPremiumBadge;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEpisodeNumber = itemView.findViewById(R.id.tvEpisodeNumber);
            tvEpisodeTitle = itemView.findViewById(R.id.tvEpisodeTitle);
            tvEpisodeDuration = itemView.findViewById(R.id.tvEpisodeDuration);
            ivPlayingIndicator = itemView.findViewById(R.id.ivPlayingIndicator);
            ivPremiumBadge = itemView.findViewById(R.id.ivPremiumBadge);
        }

        public void bind(Episode episode, boolean isPlaying) {
            tvEpisodeNumber.setText(String.valueOf(episode.getEpisodeNumber()));
            tvEpisodeTitle.setText(episode.getTitle());
            tvEpisodeDuration.setText(episode.getFormattedDuration());

            // Show/hide playing indicator
            ivPlayingIndicator.setVisibility(isPlaying ? View.VISIBLE : View.GONE);

            // Show/hide premium badge
            ivPremiumBadge.setVisibility(episode.isPremium() ? View.VISIBLE : View.GONE);


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEpisodeClick(episode, getAdapterPosition());
                }
            });
        }
    }
}