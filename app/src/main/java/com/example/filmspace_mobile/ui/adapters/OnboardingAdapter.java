package com.example.filmspace_mobile.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.OnboardingItem;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingItem> items;
    private OnContinueClickListener listener;

    public interface OnContinueClickListener {
        void onContinueClick(int position);
    }

    public OnboardingAdapter(List<OnboardingItem> items, OnContinueClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnboardingItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private ImageView onboardingImage;
        private TextView onboardingTitle;
        private TextView onboardingSubtitle;
        private Button btnContinue;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            onboardingImage = itemView.findViewById(R.id.onboardingImage);
            onboardingTitle = itemView.findViewById(R.id.onboardingTitle);
            onboardingSubtitle = itemView.findViewById(R.id.onboardingSubtitle);
            btnContinue = itemView.findViewById(R.id.btnContinue);
        }

        public void bind(OnboardingItem item, int position) {
            onboardingImage.setImageResource(item.getImageResId());
            onboardingTitle.setText(item.getTitle());
            onboardingSubtitle.setText(item.getDescription());

            // Thay đổi text button ở trang cuối
            if (position == items.size() - 1) {
                btnContinue.setText("Get Started");
            } else {
                btnContinue.setText("Continue");
            }

            btnContinue.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContinueClick(position);
                }
            });
        }
    }
}