package com.example.filmspace_mobile.ui.subscription;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.utils.AdminUtils;

public class PaymentReturnActivity extends AppCompatActivity {

    private static final String TAG = "PaymentReturn";
    private WebView webView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        // Get data from intent
        String paymentUrl = getIntent().getStringExtra("payment_url");
        userId = getIntent().getStringExtra("user_id");

        Log.d(TAG, "Opening payment URL: " + paymentUrl);
        Log.d(TAG, "User ID: " + userId);

        if (paymentUrl == null) {
            Toast.makeText(this, "Invalid payment URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupWebView();
        webView.loadUrl(paymentUrl);
    }

    private void setupWebView() {
        webView = findViewById(R.id.webView);
        
        // Setup back button
        findViewById(R.id.backIcon).setOnClickListener(v -> finish());
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "WebView loading URL: " + url);
                
                // Detect VNPay return URL
                if (url.contains("/api/vnpay/return") || url.contains("vnpay/return")) {
                    Log.d(TAG, "Detected VNPay return URL");
                    handleVNPayReturn(url);
                    return true;
                }
                
                return false;
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading: " + url);
            }
        });
    }

    private void handleVNPayReturn(String url) {
        try {
            Uri uri = Uri.parse(url);
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");
            String txnRef = uri.getQueryParameter("vnp_TxnRef");
            
            Log.d(TAG, "VNPay return - ResponseCode: " + responseCode + ", TxnRef: " + txnRef);
            
            if ("00".equals(responseCode)) {
                // Payment success
                handlePaymentSuccess();
            } else {
                // Payment failed
                handlePaymentFailed(responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing return URL", e);
            Toast.makeText(this, "Error processing payment result", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handlePaymentSuccess() {
        // Log userId cho admin - QUAN TRỌNG
        Log.i(TAG, "=== PAYMENT SUCCESS ===");
        Log.i(TAG, "User ID: " + userId);
        Log.i(TAG, "Plan: Lifetime Premium");
        Log.i(TAG, "======================");
        
        // Log for admin tracking
        AdminUtils.logPaymentForAdmin(userId, "lifetime", 108900.0, "vnpay_success");

        // Save premium status
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("is_premium", true)
                .putString("premium_plan_id", "lifetime")
                .putString("premium_plan_name", "Lifetime Premium")
                .putString("premium_user_id", userId)
                .putLong("premium_activated_at", System.currentTimeMillis())
                .apply();

        Toast.makeText(this, "Payment successful! Welcome to Premium!", Toast.LENGTH_LONG).show();

        // Go to success screen
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("plan_name", "Lifetime Premium");
        intent.putExtra("plan_price", 108900);
        intent.putExtra("user_id", userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    private void handlePaymentFailed(String responseCode) {
        Log.e(TAG, "Payment failed with code: " + responseCode);
        
        String message = "Payment failed";
        if ("24".equals(responseCode)) {
            message = "Payment cancelled by user";
        }
        
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}