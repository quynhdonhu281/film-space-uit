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
                    Episode episode = episodeList.get(position);
                    
                    // Check if user can watch: episode is not premium OR user is premium
                    if (!episode.isPremium() || userIsPremium) {
                        // Play video
                        listener.onEpisodeClick(episode);
                    } else {
                        // Show premium popup
                        if (itemView.getContext() != null) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
                            builder.setTitle("Premium Content")
                                    .setMessage("This episode is only available for premium members. Please upgrade your account.")
                                    .setPositiveButton("Buy Premium", (dialog, which) -> {
                                        // TODO: Navigate to buy premium screen
                                        // Intent intent = new Intent(itemView.getContext(), BuyPremiumActivity.class);
                                        // itemView.getContext().startActivity(intent);
                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    }
                }
            });
        }

        public void bind(Episode episode) {
            tvEpisodeTitle.setText(episode.getTitle());
            tvDuration.setText(episode.getDuration() + " minutes");
            tvEpisodeDesc.setText(episode.getOverview());
            
            // Check if episode is locked: episode is premium AND user is not premium
            boolean isLocked = episode.isPremium() && !userIsPremium;
            itemView.setEnabled(!isLocked);
            itemView.setAlpha(isLocked ? 0.5f : 1.0f);
            
            // Show premium lock indicator
            if (isLocked) {
                tvEpisodeTitle.setText(episode.getTitle() + " (Premium)");
            }
            
            // Glide.with(itemView.getContext())
            //     .load(episode.getThumbnailUrl())
            //     .placeholder(R.drawable.ic_movie_placeholder)
            //     .into(imgEpisodeThumb);
        }
    }
}