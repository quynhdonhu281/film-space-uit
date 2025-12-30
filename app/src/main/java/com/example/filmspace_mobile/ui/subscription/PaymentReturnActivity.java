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

        // Handle deep link
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            handleVNPayReturn(data);
        } else {
            Toast.makeText(this, "Invalid payment return", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleVNPayReturn(Uri data) {
        String responseCode = data.getQueryParameter("vnp_ResponseCode");
        String transactionNo = data.getQueryParameter("vnp_TransactionNo");
        String txnRef = data.getQueryParameter("vnp_TxnRef");

        Log.d(TAG, "VNPay return - ResponseCode: " + responseCode + ", TxnRef: " + txnRef);

        if ("00".equals(responseCode)) {
            // Payment success - verify with backend
            verifyPayment(txnRef);
        } else {
            // Payment failed
            handlePaymentFailed(responseCode);
        }
    }

    private void verifyPayment(String txnRef) {
        Call<PaymentVerifyResponse> call = paymentApiService.verifyPayment(txnRef);

        call.enqueue(new Callback<PaymentVerifyResponse>() {
            @Override
            public void onResponse(Call<PaymentVerifyResponse> call,
                                   Response<PaymentVerifyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentVerifyResponse verifyResponse = response.body();

                    Log.d(TAG, "Verify response: " + verifyResponse.getStatus());

                    if (verifyResponse.isSuccess() && "success".equals(verifyResponse.getStatus())) {
                        // Payment verified successfully
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
     * Handle successful payment verification
     */
    private void handlePaymentSuccess(PaymentVerifyResponse response) {
        // Clear pending payment data
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit()
                .remove("pending_txn_ref")
                .remove("pending_plan_id")
                .remove("pending_plan_name")
                .remove("pending_amount")
                .remove("pending_savings")
                .apply();

        // (Optional) Save subscription status locally
        prefs.edit()
                .putBoolean("is_premium", true)
                .putString("premium_plan_id", response.getPlanId())
                .putString("premium_plan_name", response.getPlanName())
                .apply();

        Toast.makeText(this,
                "Payment successful! Welcome to Premium 🎉",
                Toast.LENGTH_LONG).show();

        // Redirect to success screen (or main screen)
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("plan_name", response.getPlanName());
        intent.putExtra("amount", response.getAmount());
        intent.putExtra("transaction_id", response.getTransactionId());
        startActivity(intent);

        finish();
    }

    /**
     * Handle failed payment
     */
    private void handlePaymentFailed(String responseCode) {
        Log.e(TAG, "Payment failed with VNPay response code: " + responseCode);

        Toast.makeText(this,
                "Payment failed or cancelled",
                Toast.LENGTH_LONG).show();

        // Go back to payment method screen
        Intent intent = new Intent(this, PaymentMethodActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }
}
