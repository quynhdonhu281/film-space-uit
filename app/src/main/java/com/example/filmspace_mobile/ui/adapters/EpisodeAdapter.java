package com.example.filmspace_mobile.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Episode;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private List<Episode> episodeList;
    private OnEpisodeClickListener listener;
    private boolean userIsPremium;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(Episode episode);
    }

    public EpisodeAdapter(List<Episode> episodeList, OnEpisodeClickListener listener, boolean userIsPremium) {
        this.episodeList = episodeList;
        this.listener = listener;
        this.userIsPremium = userIsPremium;
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
        Episode episode = episodeList.get(position);
        holder.bind(episode);
    }

    @Override
    public int getItemCount() {
        return episodeList != null ? episodeList.size() : 0;
    }

    public void updateData(List<Episode> newEpisodeList) {
        // this.episodeList = newEpisodeList;
        // notifyDataSetChanged();
        final List<Episode> finalNewList = newEpisodeList != null ? newEpisodeList : new java.util.ArrayList<>();
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return episodeList.size();
            }

            @Override
            public int getNewListSize() {
                return finalNewList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                Episode oldEpisode = episodeList.get(oldItemPosition);
                Episode newEpisode = finalNewList.get(newItemPosition);
                return oldEpisode.getId() == newEpisode.getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Episode oldEpisode = episodeList.get(oldItemPosition);
                Episode newEpisode = finalNewList.get(newItemPosition);
                return oldEpisode.getId() == newEpisode.getId() &&
                        oldEpisode.getTitle().equals(newEpisode.getTitle()) &&
                        oldEpisode.getDuration() == newEpisode.getDuration() &&
                        oldEpisode.isPremium() == newEpisode.isPremium();
            }
        });
        
        this.episodeList = finalNewList;
        diffResult.dispatchUpdatesTo(this);
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

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Episode episode = episodeList.get(position);
                    listener.onEpisodeClick(episode);
                }
            });
        }

        public void bind(Episode episode) {
            tvEpisodeNumber.setText(String.valueOf(episode.getEpisodeNumber()));
            tvEpisodeTitle.setText(episode.getTitle());
            tvEpisodeDuration.setText(episode.getFormattedDuration());
            
            // Hide playing indicator (only used in video player)
            ivPlayingIndicator.setVisibility(View.GONE);
            
            // Show/hide premium badge
            ivPremiumBadge.setVisibility(episode.isPremium() ? View.VISIBLE : View.GONE);
            
            // Check if episode is locked: episode is premium AND user is not premium
            boolean isLocked = episode.isPremium() && !userIsPremium;
            itemView.setEnabled(!isLocked);
            itemView.setAlpha(isLocked ? 0.5f : 1.0f);
        }
    }
}