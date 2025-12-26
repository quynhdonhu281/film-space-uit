package com.example.filmspace_mobile.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.movie.Cast;
import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> castList;
    private OnCastClickListener listener;

    public interface OnCastClickListener {
        void onCastClick(Cast cast);
    }

    public CastAdapter(List<Cast> castList, OnCastClickListener listener) {
        this.castList = castList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = castList.get(position);
        holder.bind(cast);
    }

    @Override
    public int getItemCount() {
        return castList != null ? castList.size() : 0;
    }

    public void updateData(List<Cast> newCastList) {
        this.castList = newCastList;
        notifyDataSetChanged();
    }

    class CastViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgCast;
        private TextView tvCastName;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCast = itemView.findViewById(R.id.imgCast);
            tvCastName = itemView.findViewById(R.id.tvCastName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCastClick(castList.get(position));
                }
            });
        }

        public void bind(Cast cast) {
            tvCastName.setText(cast.getName());
            // Glide.with(itemView.getContext())
            //     .load(cast.getProfileUrl())
            //     .placeholder(R.drawable.ic_profile)
            //     .into(imgCast);
        }
    }
}