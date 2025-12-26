package com.example.filmspace_mobile.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.OnboardingItem;
import com.example.filmspace_mobile.ui.adapters.OnboardingAdapter;
import com.example.filmspace_mobile.ui.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private List<OnboardingItem> onboardingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);

        setupOnboardingItems();
        setupViewPager();
    }

    private void setupOnboardingItems() {
        onboardingItems = new ArrayList<>();

        // Page 1: Walk Alone
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding1, // Bạn cần thêm ảnh này vào drawable
                "Watching can be from anywhere",
                "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incididunt sed do eiusmod tempor incididunt"
        ));

        // Page 2: Archer
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding2, // Bạn cần thêm ảnh này vào drawable
                "Complete list of movies",
                "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incididunt sed do eiusmod tempor incididunt"
        ));

        // Page 3: Home Screen
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding3, // Bạn cần thêm ảnh này vào drawable
                "Spent Time with Loved Ones!",
                "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incididunt sed do eiusmod tempor incididunt"
        ));
    }

    private void setupViewPager() {
        adapter = new OnboardingAdapter(onboardingItems, position -> {
            // Nếu là trang cuối, chuyển đến MainActivity
            if (position == onboardingItems.size() - 1) {
                navigateToMain();
            } else {
                // Chuyển sang trang tiếp theo
                viewPager.setCurrentItem(position + 1, true);
            }
        });

        viewPager.setAdapter(adapter);

        // Optional: Add page change callback
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Bạn có thể thêm indicator dots ở đây nếu muốn
            }
        });
    }

    private void navigateToMain() {
        // TODO: Save onboarding completed flag to SharedPreferences
        Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
