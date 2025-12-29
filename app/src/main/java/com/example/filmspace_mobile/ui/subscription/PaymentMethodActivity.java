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
    private CardView cardVNPayWallet;
    private CardView cardATM;
    private CardView cardCreditCard;

    private RadioButton radioVNPayQR;
    private RadioButton radioVNPayWallet;
    private RadioButton radioATM;
    private RadioButton radioCreditCard;

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
        setContentView(R.layout.activity_payment_method);

        // Initialize Retrofit API Service
        paymentApiService = RetrofitClient.getPaymentApiService();

        getIntentData();
        initViews();
        displayPlanInfo();
        setupListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        planId = intent.getStringExtra("plan_id");
        planName = intent.getStringExtra("plan_name");
        planPrice = intent.getDoubleExtra("plan_price", 39000);
        planType = intent.getStringExtra("plan_type");
        planSavings = intent.getIntExtra("plan_savings", 0);
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        tvSelectedPlan = findViewById(R.id.tvSelectedPlan);
        tvPlanDetails = findViewById(R.id.tvPlanDetails);
        tvSubscriptionFee = findViewById(R.id.tvSubscriptionFee);
        tvTaxFee = findViewById(R.id.tvTaxFee);
        tvTotal = findViewById(R.id.tvTotal);

        cardVNPayQR = findViewById(R.id.cardVNPayQR);

        radioVNPayQR = findViewById(R.id.radioVNPayQR);

        btnPay = findViewById(R.id.btnPay);
    }

    private void displayPlanInfo() {
        tvSelectedPlan.setText(planName);

        if (planSavings > 0) {
            tvPlanDetails.setText("You save over " + planSavings + "%");
        } else {
            tvPlanDetails.setText("Billed monthly");
        }

        double taxFee = planPrice * 0.1;
        double total = planPrice + taxFee;

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvSubscriptionFee.setText(formatter.format(planPrice) + " ₫");
        tvTaxFee.setText(formatter.format(taxFee) + " ₫");
        tvTotal.setText(formatter.format(total) + " ₫");
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        cardVNPayQR.setOnClickListener(v -> selectPaymentMethod("vnpay_qr", radioVNPayQR));
        cardVNPayWallet.setOnClickListener(v -> selectPaymentMethod("vnpay_wallet", radioVNPayWallet));
        cardATM.setOnClickListener(v -> selectPaymentMethod("atm", radioATM));
        cardCreditCard.setOnClickListener(v -> selectPaymentMethod("credit_card", radioCreditCard));

        radioVNPayQR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectPaymentMethod("vnpay_qr", radioVNPayQR);
        });

        radioVNPayWallet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectPaymentMethod("vnpay_wallet", radioVNPayWallet);
        });

        radioATM.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectPaymentMethod("atm", radioATM);
        });

        radioCreditCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectPaymentMethod("credit_card", radioCreditCard);
        });

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void selectPaymentMethod(String method, RadioButton selectedRadio) {
        selectedPaymentMethod = method;

        radioVNPayQR.setChecked(false);
        radioVNPayWallet.setChecked(false);
        radioATM.setChecked(false);
        radioCreditCard.setChecked(false);

        selectedRadio.setChecked(true);
    }

    private void processPayment() {
        btnPay.setEnabled(false);
        btnPay.setText("Processing...");

        // Get user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "guest_" + System.currentTimeMillis());

        // Calculate total amount
        double taxFee = planPrice * 0.1;
        double totalAmount = planPrice + taxFee;

        // Create payment request
        String orderInfo = "Thanh toan goi " + planName;
        PaymentRequest request = new PaymentRequest(
                userId,
                planId,
                planName,
                totalAmount,
                selectedPaymentMethod,
                orderInfo
        );

        // Call API to create payment
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

                Log.d(TAG, "API Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse paymentResponse = response.body();

                    Log.d(TAG, "Payment Response: " + paymentResponse.getMessage());

                    if (paymentResponse.isSuccess()) {
                        // Save transaction reference
                        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        prefs.edit()
                                .putString("pending_txn_ref", paymentResponse.getTxnRef())
                                .putString("pending_plan_id", planId)
                                .putString("pending_plan_name", planName)
                                .putFloat("pending_amount", (float)request.getAmount())
                                .putInt("pending_savings", planSavings)
                                .apply();

                        Log.d(TAG, "Opening VNPay URL: " + paymentResponse.getPaymentUrl());

                        // Open VNPay payment URL
                        openVNPayUrl(paymentResponse.getPaymentUrl());
                    } else {
                        Log.e(TAG, "Payment creation failed: " + paymentResponse.getMessage());
                        Toast.makeText(PaymentMethodActivity.this,
                                "Payment error: " + paymentResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "API call failed with code: " + response.code());
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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening VNPay URL", e);
            Toast.makeText(this, "Cannot open payment page", Toast.LENGTH_SHORT).show();
        }
    }
}