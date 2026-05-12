package com.bhtiktok.utils;

import java.util.ArrayList;
import java.util.List;

public class VideoUrlExtractor {

    /**
     * Convert watermarked URLs to no-watermark URLs.
     * Strategy: replace playwm with play, remove watermark param, try API-H2 endpoint.
     */
    public static List<String> getNoWatermarkUrls(List<String> urls) {
        List<String> result = new ArrayList<>();
        if (urls == null) return result;

        for (String url : urls) {
            if (url == null) continue;
            String clean = url;
            // Replace playwm with play
            clean = clean.replace("playwm", "play");
            // Remove watermark param
            clean = clean.replaceAll("([&?])watermark=[^&]*", "$1watermark=0");
            if (!clean.contains("watermark=")) {
                clean = clean + (clean.contains("?") ? "&watermark=0" : "?watermark=0");
            }
            // Try API-H2 endpoint variant
            String h2 = clean.replace("api16-normal-c-useast1a.tiktokv.com", "api-h2.tiktokv.com");

            result.add(clean);
            if (!h2.equals(clean)) result.add(h2);
        }
        return result;
    }

    public static String bestUrl(List<String> urls) {
        List<String> noWm = getNoWatermarkUrls(urls);
        return noWm.isEmpty() ? (urls != null && !urls.isEmpty() ? urls.get(0) : null) : noWm.get(0);
    }
}
