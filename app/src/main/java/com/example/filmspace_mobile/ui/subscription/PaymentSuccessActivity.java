package com.example.filmspace_mobile.ui.subscription;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.ui.main.MainActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView tvPlanName;
    private TextView tvSavings;
    private TextView tvSubscriptionFee;
    private TextView tvTaxFee;
    private TextView tvTotal;
    private Button btnWatchNow;

    private String planName;
    private double planPrice;
    private int planSavings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        getIntentData();
        initViews();
        displayPaymentInfo();
        setupListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        planName = intent.getStringExtra("plan_name");
        planPrice = intent.getDoubleExtra("plan_price", 39000);
        planSavings = intent.getIntExtra("plan_savings", 0);
    }

    private void initViews() {
        tvPlanName = findViewById(R.id.tvPlanName);
        tvSavings = findViewById(R.id.tvSavings);
        tvSubscriptionFee = findViewById(R.id.tvSubscriptionFee);
        tvTaxFee = findViewById(R.id.tvTaxFee);
        tvTotal = findViewById(R.id.tvTotal);
        btnWatchNow = findViewById(R.id.btnWatchNow);
    }

    private void displayPaymentInfo() {
        tvPlanName.setText("Movies " + planName);

        if (planSavings > 0) {
            tvSavings.setText("You save over " + planSavings + "%");
        } else {
            tvSavings.setText("Billed monthly");
        }

        double taxFee = planPrice * 0.1;
        double total = planPrice + taxFee;

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvSubscriptionFee.setText(formatter.format(planPrice) + " ₫");
        tvTaxFee.setText(formatter.format(taxFee) + " ₫");
        tvTotal.setText(formatter.format(total) + " ₫");

        // Save premium status to SharedPreferences
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("is_premium", true)
                .putString("subscription_plan", planName)
                .putLong("subscription_date", System.currentTimeMillis())
                .apply();
    }

    private void setupListeners() {
        btnWatchNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent back navigation to payment screen
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}