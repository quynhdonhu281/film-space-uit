package com.example.filmspace_mobile.ui.subscription;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.api.PaymentApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.model.payment.PaymentRequest;
import com.example.filmspace_mobile.data.model.payment.PaymentResponse;
import java.text.NumberFormat;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodActivity extends AppCompatActivity {

    private static final String TAG = "PaymentMethodActivity";

    private ImageView backIcon;
    private TextView tvSelectedPlan;
    private TextView tvPlanDetails;
    private TextView tvSubscriptionFee;
    private TextView tvTaxFee;
    private TextView tvTotal;

    private CardView cardVNPayQR;
    private RadioButton radioVNPayQR;
    private Button btnPay;

    private String planId;
    private String planName;
    private double planPrice;
    private String planType;
    private int planSavings;
    private String selectedPaymentMethod = "vnpay_qr";

    private PaymentApiService paymentApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_payment_method);

            paymentApiService = RetrofitClient.getPaymentApiService();

            getIntentData();
            initViews();
            displayPlanInfo();
            setupListeners();
            
            Log.d(TAG, "PaymentMethodActivity created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error loading payment page: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void getIntentData() {
        try {
            Intent intent = getIntent();
            planId = intent.getStringExtra("plan_id");
            planName = intent.getStringExtra("plan_name");
            planPrice = intent.getDoubleExtra("plan_price", 99000); // Default 99k
            planType = intent.getStringExtra("plan_type");
            planSavings = intent.getIntExtra("plan_savings", 0);
            
            // Set defaults if null
            if (planId == null) planId = "lifetime";
            if (planName == null) planName = "Lifetime Premium";
            if (planType == null) planType = "lifetime";
            
            Log.d(TAG, "Intent data - Plan: " + planName + ", Price: " + planPrice);
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data", e);
            // Set default values
            planId = "lifetime";
            planName = "Lifetime Premium";
            planPrice = 99000;
            planType = "lifetime";
            planSavings = 0;
        }
    }

    private void initViews() {
        try {
            backIcon = findViewById(R.id.backIcon);
            tvSelectedPlan = findViewById(R.id.tvSelectedPlan);
            tvPlanDetails = findViewById(R.id.tvPlanDetails);
            tvSubscriptionFee = findViewById(R.id.tvSubscriptionFee);
            tvTaxFee = findViewById(R.id.tvTaxFee);
            tvTotal = findViewById(R.id.tvTotal);

            cardVNPayQR = findViewById(R.id.cardVNPayQR);
            radioVNPayQR = findViewById(R.id.radioVNPayQR);
            btnPay = findViewById(R.id.btnPay);
            
            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error loading payment page", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayPlanInfo() {
        try {
            if (planName == null || planName.isEmpty()) {
                planName = "Lifetime Premium";
            }
            
            tvSelectedPlan.setText(planName);
            tvPlanDetails.setText("One-time payment - Lifetime access");

            double taxFee = planPrice * 0.1;
            double total = planPrice + taxFee;

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

            tvSubscriptionFee.setText(formatter.format(planPrice) + " ₫");
            tvTaxFee.setText(formatter.format(taxFee) + " ₫");
            tvTotal.setText(formatter.format(total) + " ₫");
            
            Log.d(TAG, "Plan info displayed: " + planName + ", Price: " + planPrice);
        } catch (Exception e) {
            Log.e(TAG, "Error displaying plan info", e);
            Toast.makeText(this, "Error loading plan information", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        cardVNPayQR.setOnClickListener(v -> {
            radioVNPayQR.setChecked(true);
            selectedPaymentMethod = "vnpay_qr";
        });

        radioVNPayQR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedPaymentMethod = "vnpay_qr";
        });

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        btnPay.setEnabled(false);
        btnPay.setText("Processing...");

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "guest_" + System.currentTimeMillis());

        double taxFee = planPrice * 0.1;
        double totalAmount = planPrice + taxFee;

        String orderInfo = planId + "|" + planName;
        PaymentRequest request = new PaymentRequest(
                userId,
                planId,
                planName,
                (int) Math.round(totalAmount), // Cast to int
                selectedPaymentMethod,
                orderInfo
        );

        Log.d(TAG, "Creating payment for user: " + userId + ", plan: " + planName);
        createVNPayPayment(request);
    }

    private void createVNPayPayment(PaymentRequest request) {
        Call<PaymentResponse> call = paymentApiService.createPayment(request);

        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                btnPay.setEnabled(true);
                btnPay.setText("Pay Now");

                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse paymentResponse = response.body();

                    if (paymentResponse.isSuccess()) {
                        // Save transaction info
                        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        prefs.edit()
                                .putString("pending_txn_ref", paymentResponse.getTxnRef())
                                .putString("pending_plan_id", planId)
                                .putString("pending_plan_name", planName)
                                .putInt("pending_amount", request.getAmount()) // Change to putInt
                                .apply();

                        Log.d(TAG, "Opening VNPay URL: " + paymentResponse.getPaymentUrl());

                        // Mở VNPay URL trong browser
                        openVNPayUrl(paymentResponse.getPaymentUrl());
                    } else {
                        Toast.makeText(PaymentMethodActivity.this,
                                "Payment error: " + paymentResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PaymentMethodActivity.this,
                            "Failed to create payment. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                btnPay.setEnabled(true);
                btnPay.setText("Pay Now");

                Log.e(TAG, "Payment API error", t);
                Toast.makeText(PaymentMethodActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openVNPayUrl(String paymentUrl) {
        try {
            // Mở PaymentReturnActivity với WebView thay vì browser
            Intent intent = new Intent(this, PaymentReturnActivity.class);
            intent.putExtra("payment_url", paymentUrl);
            intent.putExtra("user_id", getSharedPreferences("user_prefs", MODE_PRIVATE).getString("user_id", ""));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening VNPay WebView", e);
            Toast.makeText(this, "Cannot open payment page", Toast.LENGTH_SHORT).show();
        }
    }
}