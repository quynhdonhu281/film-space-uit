package com.example.filmspace_mobile.ui.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.model.SubscriptionPlan;

public class SubscriptionDescriptionActivity extends AppCompatActivity {

    private ImageView backIcon;
    private Button btnContinue;
    private SubscriptionPlan lifetimePlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_description);

        initViews();
        setupLifetimePlan();
        setupListeners();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupLifetimePlan() {
        // Chỉ có 1 plan: Lifetime Purchase
        lifetimePlan = new SubscriptionPlan(
                "lifetime",
                "Lifetime Premium",
                "lifetime",
                99000,
                "One-time payment",
                0
        );
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        btnContinue.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, PaymentMethodActivity.class);
                intent.putExtra("plan_id", lifetimePlan.getId());
                intent.putExtra("plan_name", lifetimePlan.getName());
                intent.putExtra("plan_price", lifetimePlan.getPrice());
                intent.putExtra("plan_type", lifetimePlan.getType());
                intent.putExtra("plan_savings", lifetimePlan.getSavingsPercent());
                
                Log.d("SubscriptionActivity", "Starting PaymentMethodActivity with plan: " + lifetimePlan.getName());
                startActivity(intent);
            } catch (Exception e) {
                Log.e("SubscriptionActivity", "Error starting PaymentMethodActivity", e);
                Toast.makeText(this, "Error opening payment page: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}