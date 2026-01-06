package com.example.filmspace_mobile.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Episode;
import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    // Tag để debug trong Logcat
    private static final String TAG = "EpisodeAdapter";

    private List<Episode> episodeList;
    private OnEpisodeClickListener listener;
    private boolean userIsPremium;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(Episode episode);
    }

    public EpisodeAdapter(List<Episode> episodeList, OnEpisodeClickListener listener, boolean userIsPremium) {
        this.episodeList = episodeList != null ? episodeList : new ArrayList<>();
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

        // DEBUG: Kiểm tra dữ liệu từng dòng xem có đúng là Premium không
        if (episode.isPremium()) {
            Log.d(TAG, "Item " + position + ": " + episode.getTitle() + " LÀ PREMIUM. User VIP: " + userIsPremium);
        }

        holder.bind(episode);
    }

    @Override
    public int getItemCount() {
        return episodeList != null ? episodeList.size() : 0;
    }

    // 1. Cập nhật danh sách phim (Dùng notifyDataSetChanged cho chắc chắn)
    public void updateData(List<Episode> newEpisodeList) {
        this.episodeList = newEpisodeList != null ? newEpisodeList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // 2. [QUAN TRỌNG] Hàm mới để cập nhật trạng thái User mà không cần tạo lại Adapter
    public void setUserIsPremium(boolean isPremium) {
        this.userIsPremium = isPremium;
        notifyDataSetChanged(); // Vẽ lại giao diện ngay lập tức
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
                    // Luôn cho phép click để Fragment xử lý logic (hiện Dialog mua)
                    listener.onEpisodeClick(episode);
                }
            });
        }

        public void bind(Episode episode) {
            tvEpisodeNumber.setText(String.valueOf(episode.getEpisodeNumber()));
            tvEpisodeTitle.setText(episode.getTitle());
            tvEpisodeDuration.setText(episode.getFormattedDuration());

            // 1. Reset trạng thái mặc định (tránh lỗi hiển thị sai do tái sử dụng View)
            ivPlayingIndicator.setVisibility(View.GONE);
            ivPremiumBadge.setVisibility(View.GONE);
            itemView.setAlpha(1.0f);

            // 2. LOGIC HIỂN THỊ PREMIUM
            if (episode.isPremium()) {
                // Luôn hiện icon nếu là tập Premium
                ivPremiumBadge.setVisibility(View.VISIBLE);

                // Gán cứng icon ổ khóa (đảm bảo hiện kể cả khi XML quên set src)
                ivPremiumBadge.setImageResource(R.drawable.ic_lock);

                // Kiểm tra xem User đã mua gói chưa
                boolean isLocked = !userIsPremium; // Premium + Chưa mua = Khóa

                if (isLocked) {
                    // Nếu bị khóa: Làm mờ item 50%
                    itemView.setAlpha(0.5f);
                } else {
                    // Nếu ĐÃ MUA: Sáng rõ 100%
                    itemView.setAlpha(1.0f);

                    // (Tùy chọn) Nếu muốn đổi icon ổ khóa thành icon khác khi đã mua:
                    // ivPremiumBadge.setImageResource(R.drawable.ic_check);
                }
            }
            // Nếu không phải Premium thì code đã chạy ở phần Reset (GONE, Alpha 1.0) rồi.
        }
    }
}