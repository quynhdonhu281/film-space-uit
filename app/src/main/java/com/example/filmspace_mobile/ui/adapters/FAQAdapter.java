package com.example.filmspace_mobile.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.FAQItem;
import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private List<FAQItem> faqList;

    public FAQAdapter(List<FAQItem> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        FAQItem item = faqList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return faqList != null ? faqList.size() : 0;
    }

    class FAQViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout faqHeader;
        private TextView tvQuestion;
        private TextView tvAnswer;
        private ImageView ivExpandIcon;

        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            faqHeader = itemView.findViewById(R.id.faqHeader);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            ivExpandIcon = itemView.findViewById(R.id.ivExpandIcon);
        }

        public void bind(FAQItem item) {
            tvQuestion.setText(item.getQuestion());
            tvAnswer.setText(item.getAnswer());

            // Set initial state
            if (item.isExpanded()) {
                tvAnswer.setVisibility(View.VISIBLE);
                ivExpandIcon.setRotation(180);
            } else {
                tvAnswer.setVisibility(View.GONE);
                ivExpandIcon.setRotation(0);
            }

            faqHeader.setOnClickListener(v -> {
                item.setExpanded(!item.isExpanded());

                if (item.isExpanded()) {
                    tvAnswer.setVisibility(View.VISIBLE);
                    rotateIcon(ivExpandIcon, 0, 180);
                } else {
                    tvAnswer.setVisibility(View.GONE);
                    rotateIcon(ivExpandIcon, 180, 0);
                }
            });
        }

        private void rotateIcon(ImageView icon, float fromDegrees, float toDegrees) {
            RotateAnimation rotate = new RotateAnimation(
                    fromDegrees, toDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            rotate.setDuration(200);
            rotate.setFillAfter(true);
            icon.startAnimation(rotate);
        }
    }
}