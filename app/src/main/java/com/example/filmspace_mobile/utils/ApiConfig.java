package com.example.filmspace_mobile.utils;

public class ApiConfig {

    // Backend API URLs - Unified to use emulator IP
    public static final String API_BASE_URL_EMULATOR = "http://10.0.2.2:8080/api/";
    public static final String API_BASE_URL_LOCAL = "http://10.0.2.2:8080/api/";
    public static final String API_BASE_URL_PRODUCTION = "http://10.0.2.2:8080/api/";

    // Active URL - All using same emulator IP
    public static final String API_BASE_URL = API_BASE_URL_EMULATOR;

    // Timeout
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
}