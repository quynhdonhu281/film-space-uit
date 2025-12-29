package com.example.filmspace_mobile.utils;

public class ApiConfig {

    // Backend API URLs
    public static final String API_BASE_URL_EMULATOR = "http://192.168.1.221:8080/api/";
    public static final String API_BASE_URL_LOCAL = "http://192.168.1.221:8080/api/";
    public static final String API_BASE_URL_PRODUCTION = "http://192.168.1.221:8080/api/";

    // Active URL
    public static final String API_BASE_URL = API_BASE_URL_EMULATOR; // Change this

    // Timeout
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
}