package com.example.filmspace_mobile.data.api;

import com.example.filmspace_mobile.data.model.payment.PaymentRequest;
import com.example.filmspace_mobile.data.model.payment.PaymentResponse;
import com.example.filmspace_mobile.data.model.payment.SubscriptionCheckResponse;
import com.example.filmspace_mobile.data.model.payment.TransactionHistoryResponse;
import com.example.filmspace_mobile.data.model.payment.PaymentVerifyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentApiService {

    /**
     * Create payment URL
     * POST /payment/create
     */
    @POST("payment/create")
    Call<PaymentResponse> createPayment(@Body PaymentRequest request);

    /**
     * Verify payment status
     * GET /payment/verify?vnp_TxnRef=xxx
     */
    @GET("payment/verify")
    Call<PaymentVerifyResponse> verifyPayment(
            @Query("vnp_TxnRef") String txnRef
    );

    /**
     * Check subscription status
     * GET /payment/subscription/check?userId=xxx
     */
    @GET("payment/subscription/check")
    Call<SubscriptionCheckResponse> checkSubscription(
            @Query("userId") String userId
    );

    /**
     * Get transaction history
     * GET /payment/history?userId=xxx
     */
    @GET("payment/history")
    Call<TransactionHistoryResponse> getTransactionHistory(
            @Query("userId") String userId
    );
}