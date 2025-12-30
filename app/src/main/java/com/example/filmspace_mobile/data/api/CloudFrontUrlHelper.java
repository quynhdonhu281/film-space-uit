package com.example.filmspace_mobile.data.api;

public class CloudFrontUrlHelper {
    private static final String CLOUDFRONT_BASE_URL = "https://d58vokudzsdux.cloudfront.net/";

    /**
     * Prepends CloudFront base URL to the given path if it's not null, not empty,
     * and doesn't already contain a full URL (http:// or https://)
     */
    public static String prependCloudFrontUrl(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        
        // If it's already a full URL, return as is
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        
        // Remove leading slash if present to avoid double slashes
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        return CLOUDFRONT_BASE_URL + path;
    }
}
