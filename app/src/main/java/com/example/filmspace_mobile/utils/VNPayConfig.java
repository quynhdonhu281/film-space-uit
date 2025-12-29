package com.example.filmspace_mobile.utils;

public class VNPayConfig {
    // Backend API URL - Thay đổi khi backend ready
    // Development: localhost/ngrok
    // Production: https://your-backend.onrender.com/api

    // Để test với localhost qua emulator
    public static final String API_BASE_URL_EMULATOR = "http://10.0.2.2:3000/api/";

    // Để test với localhost qua thiết bị thật
    public static final String API_BASE_URL_LOCAL = "http://192.168.1.XXX:3000/api/";

    // Ngrok URL (để test trên thiết bị thật khi backend chạy localhost)
    public static final String API_BASE_URL_NGROK = "https://xxxx-xx-xxx-xxx-xxx.ngrok-free.app/api/";

    // Production URL (khi backend đã deploy)
    public static final String API_BASE_URL_PRODUCTION = "https://your-backend.onrender.com/api/";

    // Active URL - Thay đổi theo môi trường test
    public static final String API_BASE_URL = API_BASE_URL_EMULATOR; // Đổi theo nhu cầu

    // Endpoints
    public static final String ENDPOINT_CREATE_PAYMENT = "payment/create";
    public static final String ENDPOINT_VERIFY_PAYMENT = "payment/verify";
    public static final String ENDPOINT_CHECK_SUBSCRIPTION = "payment/subscription/check";
    public static final String ENDPOINT_TRANSACTION_HISTORY = "payment/history";

    // Deep link for VNPay return
    public static final String VNP_RETURN_URL = "filmspace://payment/return";

    // Timeout settings
    public static final int CONNECT_TIMEOUT = 30; // seconds
    public static final int READ_TIMEOUT = 30; // seconds
    public static final int WRITE_TIMEOUT = 30; // seconds
}