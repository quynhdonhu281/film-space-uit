package com.example.filmspace_mobile.ui.subscription;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.api.PaymentApiService;
import com.example.filmspace_mobile.data.api.RetrofitClient;
import com.example.filmspace_mobile.data.model.payment.PaymentVerifyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentReturnActivity extends AppCompatActivity {

    private static final String TAG = "PaymentReturn";
    private PaymentApiService paymentApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_loading);

        paymentApiService = RetrofitClient.getPaymentApiService();

        // Handle deep link từ VNPay
        Intent intent = getIntent();
        Uri data = intent.getData();

        Log.d(TAG, "Received intent with data: " + (data != null ? data.toString() : "null"));

        if (data != null) {
            handleVNPayReturn(data);
        } else {
            Toast.makeText(this, "Invalid payment return", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleVNPayReturn(Uri data) {
        // Parse VNPay response từ deep link
        String responseCode = data.getQueryParameter("vnp_ResponseCode");
        String txnRef = data.getQueryParameter("vnp_TxnRef");
        String transactionNo = data.getQueryParameter("vnp_TransactionNo");
        String amount = data.getQueryParameter("vnp_Amount");

        Log.d(TAG, "VNPay return - ResponseCode: " + responseCode +
                ", TxnRef: " + txnRef +
                ", TransactionNo: " + transactionNo);

        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            verifyPayment(txnRef);
        } else {
            // Thanh toán thất bại hoặc bị hủy
            handlePaymentFailed(responseCode);
        }
    }

    /**
     * Verify payment với backend
     */
    private void verifyPayment(String txnRef) {
        Log.d(TAG, "Verifying payment with txnRef: " + txnRef);

        Call<PaymentVerifyResponse> call = paymentApiService.verifyPayment(txnRef);

        call.enqueue(new Callback<PaymentVerifyResponse>() {
            @Override
            public void onResponse(Call<PaymentVerifyResponse> call,
                                   Response<PaymentVerifyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentVerifyResponse verifyResponse = response.body();

                    Log.d(TAG, "Verify response status: " + verifyResponse.getStatus());

                    if (verifyResponse.isSuccess() && "success".equals(verifyResponse.getStatus())) {
                        handlePaymentSuccess(verifyResponse);
                    } else {
                        Log.e(TAG, "Payment verification failed: " + verifyResponse.getMessage());
                        Toast.makeText(PaymentReturnActivity.this,
                                "Payment verification failed",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e(TAG, "Verify API failed with code: " + response.code());
                    Toast.makeText(PaymentReturnActivity.this,
                            "Cannot verify payment",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PaymentVerifyResponse> call, Throwable t) {
                Log.e(TAG, "Verify payment error", t);
                Toast.makeText(PaymentReturnActivity.this,
                        "Network error while verifying payment",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    /**
     * Xử lý khi payment thành công
     */
    private void handlePaymentSuccess(PaymentVerifyResponse response) {
//        Log.d(TAG, "Payment success for user: " + response.getUserId());

        // Lưu thông tin premium vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("is_premium", true)
                .putString("premium_plan_id", response.getPlanId())
                .putString("premium_plan_name", response.getPlanName())
                .putString("premium_user_id", response.getUserId())
                .putLong("premium_activated_at", System.currentTimeMillis())
                // Clear pending data
                .remove("pending_txn_ref")
                .remove("pending_plan_id")
                .remove("pending_plan_name")
                .remove("pending_amount")
                .apply();

        Toast.makeText(this,
                "Welcome to Lifetime Premium!",
                Toast.LENGTH_LONG).show();

        // Chuyển đến màn hình success
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("plan_name", response.getPlanName());
        intent.putExtra("plan_price", response.getAmount());
        intent.putExtra("transaction_id", response.getTransactionId());
        intent.putExtra("plan_savings", 0); // Lifetime không có savings
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    /**
     * Xử lý khi payment thất bại
     */
    private void handlePaymentFailed(String responseCode) {
        Log.e(TAG, "Payment failed with VNPay response code: " + responseCode);

        String message;
        switch (responseCode) {
            case "24":
                message = "Payment cancelled by user";
                break;
            case "07":
                message = "Transaction failed - please try again";
                break;
            case "09":
                message = "Card not registered for online payment";
                break;
            default:
                message = "Payment failed (Code: " + responseCode + ")";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Quay lại màn hình payment method
        Intent intent = new Intent(this, PaymentMethodActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Handle deep link nếu activity đang chạy
        Uri data = intent.getData();
        if (data != null) {
            handleVNPayReturn(data);
        }
    }
}