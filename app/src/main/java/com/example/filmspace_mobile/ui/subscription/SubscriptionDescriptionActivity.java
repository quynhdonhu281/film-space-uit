package com.example.filmspace_mobile.ui.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.SubscriptionPlan;

public class SubscriptionDescriptionActivity extends AppCompatActivity {

    private ImageView backIcon;
    private CardView cardMonthlyPlan;
    private CardView cardYearlyPlan;
    private RadioButton radioMonthly;
    private RadioButton radioYearly;
    private Button btnContinue;

    private SubscriptionPlan selectedPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_description);

        initViews();
        setupPlans();
        setupListeners();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        cardMonthlyPlan = findViewById(R.id.cardMonthlyPlan);
        cardYearlyPlan = findViewById(R.id.cardYearlyPlan);
        radioMonthly = findViewById(R.id.radioMonthly);
        radioYearly = findViewById(R.id.radioYearly);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupPlans() {
        // Default: Monthly plan selected
        selectedPlan = new SubscriptionPlan(
                "monthly",
                "Monthly Subscription",
                "monthly",
                39000,
                "Billed monthly",
                0
        );
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        cardMonthlyPlan.setOnClickListener(v -> selectMonthlyPlan());
        cardYearlyPlan.setOnClickListener(v -> selectYearlyPlan());

        radioMonthly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectMonthlyPlan();
        });

        radioYearly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectYearlyPlan();
        });

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentMethodActivity.class);
            intent.putExtra("plan_id", selectedPlan.getId());
            intent.putExtra("plan_name", selectedPlan.getName());
            intent.putExtra("plan_price", selectedPlan.getPrice());
            intent.putExtra("plan_type", selectedPlan.getType());
            intent.putExtra("plan_savings", selectedPlan.getSavingsPercent());
            startActivity(intent);
        });
    }

    private void selectMonthlyPlan() {
        radioMonthly.setChecked(true);
        radioYearly.setChecked(false);

        selectedPlan = new SubscriptionPlan(
                "monthly",
                "Monthly Subscription",
                "monthly",
                39000,
                "Billed monthly",
                0
        );
    }

    private void selectYearlyPlan() {
        radioYearly.setChecked(true);
        radioMonthly.setChecked(false);

        selectedPlan = new SubscriptionPlan(
                "yearly",
                "Yearly Subscription",
                "yearly",
                360000,
                "Billed annually",
                15
        );
    }
}