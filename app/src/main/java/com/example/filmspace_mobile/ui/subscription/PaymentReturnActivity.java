package com.example.filmspace_mobile.ui.subscription;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.utils.AdminUtils;

public class PaymentReturnActivity extends AppCompatActivity {

    private static final String TAG = "PaymentReturn";
    private WebView webView;
    private String userId;

    // Biến cờ để đánh dấu giao dịch đã xong chưa
    private boolean isTransactionCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        String paymentUrl = getIntent().getStringExtra("payment_url");
        userId = getIntent().getStringExtra("user_id");

        Log.d(TAG, "Opening payment URL: " + paymentUrl);

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

        // Xử lý nút Back trên Toolbar (Góc trái trên)
        findViewById(R.id.backIcon).setOnClickListener(v -> handleBackAction());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 1. Bắt link Return từ Server
                if (url.contains("/api/vnpay/return") || url.contains("vnpay/return")) {
                    handleVNPayReturn(url);
                    // Return false để WebView vẫn load trang này (để hiện thông báo thành công)
                    return false;
                }

                // 2. Chặn lỗi Intent lạ (App ngân hàng)
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Backup check
                if (!isTransactionCompleted && (url.contains("/api/vnpay/return") || url.contains("vnpay/return"))) {
                    handleVNPayReturn(url);
                }
            }

            // Ẩn trang lỗi mặc định của WebView nếu có lỗi mạng xảy ra
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "WebView Error: " + error.getDescription());
            }
        });
    }

    private void handleVNPayReturn(String url) {
        // Nếu đã xử lý rồi thì không làm lại (tránh bị gọi 2 lần do redirect)
        if (isTransactionCompleted) return;

        try {
            Uri uri = Uri.parse(url);
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");

            Log.d(TAG, "VNPay return Code: " + responseCode);

            if ("00".equals(responseCode)) {
                handlePaymentSuccess();
            } else {
                handlePaymentFailed(responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing return URL", e);
        }
    }

    private void handlePaymentSuccess() {
        Log.i(TAG, "=== PAYMENT SUCCESS ===");

        // 1. Lưu dữ liệu NGAY LẬP TỨC (An toàn dữ liệu)
        // Lưu lúc này để dù user có tắt app ngang thì lần sau mở lên vẫn là VIP
        AdminUtils.logPaymentForAdmin(userId, "lifetime", 108900.0, "vnpay_success");
        UserSessionManager sessionManager = new UserSessionManager(this);
        sessionManager.setPremium(true);

        // 2. Đánh dấu đã xong
        isTransactionCompleted = true;

        // 3. Thông báo nhẹ
        Toast.makeText(this, "Thanh toán thành công! Bấm Back để quay lại.", Toast.LENGTH_SHORT).show();

        // [QUAN TRỌNG]: KHÔNG GỌI finish() Ở ĐÂY
        // Để WebView tiếp tục hiển thị trang web "Giao dịch thành công" của backend trả về.
    }

    private void handlePaymentFailed(String responseCode) {
        Log.e(TAG, "Payment failed: " + responseCode);
        String msg = "24".equals(responseCode) ? "Bạn đã hủy thanh toán" : "Thanh toán thất bại";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        // Nếu thất bại thì cho thoát luôn hoặc giữ lại tùy ý
        // Ở đây ta thoát luôn để user thử lại
        finish();
    }

    // Hàm xử lý chung cho nút Back
    private void handleBackAction() {
        if (isTransactionCompleted) {
            Intent intent = new Intent(this, PaymentSuccessActivity.class);
            intent.putExtra("plan_name", "Lifetime Premium");
            intent.putExtra("plan_price", 108900);
            intent.putExtra("user_id", userId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            // Nếu đã thanh toán xong -> Đóng Activity -> Quay về MovieDetail -> Reload Data
            finish();
        } else {
            // Nếu đang lướt web bình thường -> Check history
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish(); // Hoặc super.onBackPressed();
            }
        }
    }

    // Xử lý nút Back cứng trên điện thoại
    @Override
    public void onBackPressed() {
        handleBackAction();
    }
}