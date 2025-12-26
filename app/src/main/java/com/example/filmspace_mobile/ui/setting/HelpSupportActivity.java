package com.example.filmspace_mobile.ui.setting;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.FAQItem;
import com.example.filmspace_mobile.ui.adapters.FAQAdapter;
import java.util.ArrayList;
import java.util.List;

public class HelpSupportActivity extends AppCompatActivity {

    private ImageView backIcon;
    private RecyclerView rvFAQ;
    private FAQAdapter adapter;
    private List<FAQItem> faqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        initViews();
        setupRecyclerView();
        loadFAQs();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        rvFAQ = findViewById(R.id.rvFAQ);
    }

    private void setupRecyclerView() {
        rvFAQ.setLayoutManager(new LinearLayoutManager(this));
        faqList = new ArrayList<>();
        adapter = new FAQAdapter(faqList);
        rvFAQ.setAdapter(adapter);
    }

    private void loadFAQs() {
        faqList.clear();

        faqList.add(new FAQItem(
                getString(R.string.faq_question_1),
                getString(R.string.faq_answer_1)
        ));

        faqList.add(new FAQItem(
                getString(R.string.faq_question_2),
                getString(R.string.faq_answer_2)
        ));

        faqList.add(new FAQItem(
                getString(R.string.faq_question_3),
                getString(R.string.faq_answer_3)
        ));

        faqList.add(new FAQItem(
                getString(R.string.faq_question_4),
                getString(R.string.faq_answer_4)
        ));

        faqList.add(new FAQItem(
                getString(R.string.faq_question_5),
                getString(R.string.faq_answer_5)
        ));

        adapter.notifyDataSetChanged();

        backIcon.setOnClickListener(v -> finish());
    }
}