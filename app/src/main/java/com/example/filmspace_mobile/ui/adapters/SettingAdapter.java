package com.example.filmspace_mobile.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.SettingItem;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder> {

    private List<SettingItem> items;
    private OnSettingClickListener listener;

    public interface OnSettingClickListener {
        void onSettingClick(SettingItem item, int position);
    }

    public SettingAdapter(List<SettingItem> items, OnSettingClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_setting, parent, false);
        return new SettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        SettingItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class SettingViewHolder extends RecyclerView.ViewHolder {
        private ImageView settingIcon;
        private TextView settingTitle;
        private TextView settingValue;
        private ImageView arrowIcon;

        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            settingIcon = itemView.findViewById(R.id.settingIcon);
            settingTitle = itemView.findViewById(R.id.settingTitle);
            settingValue = itemView.findViewById(R.id.settingValue);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
        }

        public void bind(SettingItem item, int position) {
            settingIcon.setImageResource(item.getIconResId());
            settingTitle.setText(item.getTitle());

            if (item.getValue() != null && !item.getValue().isEmpty()) {
                settingValue.setVisibility(View.VISIBLE);
                settingValue.setText(item.getValue());
            } else {
                settingValue.setVisibility(View.GONE);
            }

            if (item.getType() == SettingItem.SettingType.LOGOUT) {
                settingTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                settingIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                arrowIcon.setVisibility(View.GONE);
            } else {
                settingTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
                settingIcon.clearColorFilter();
                arrowIcon.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSettingClick(item, position);
                }
            });
        }
    }
}