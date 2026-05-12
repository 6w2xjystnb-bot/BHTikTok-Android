package com.bhtiktok.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class BHDownloadManager {

    public static void downloadVideo(Context ctx, String url, String fileName, String desc) {
        if (ctx == null || url == null || url.isEmpty()) return;

        try {
            String safeName = fileName != null ? fileName.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "_") : "video";
            if (safeName.length() > 50) safeName = safeName.substring(0, 50);

            String finalName = "BHTikTok_" + safeName + "_" + System.currentTimeMillis() + ".mp4";

            android.app.DownloadManager.Request request =
                new android.app.DownloadManager.Request(Uri.parse(url));
            request.setTitle("BHTikTok Download");
            request.setDescription(desc != null ? desc : "Downloading video...");
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, finalName);
            request.addRequestHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)");

            android.app.DownloadManager dm = (android.app.DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
                Toast.makeText(ctx, "Download started: " + finalName, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(ctx, "Download error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void downloadMusic(Context ctx, String url, String musicTitle) {
        if (ctx == null || url == null || url.isEmpty()) return;

        try {
            String safeName = musicTitle != null ? musicTitle.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "_") : "music";
            String finalName = "BHTikTok_Music_" + safeName + "_" + System.currentTimeMillis() + ".mp3";

            android.app.DownloadManager.Request request =
                new android.app.DownloadManager.Request(Uri.parse(url));
            request.setTitle("BHTikTok Music Download");
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, finalName);
            request.addRequestHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)");

            android.app.DownloadManager dm = (android.app.DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
                Toast.makeText(ctx, "Music download started", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(ctx, "Music download error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void downloadPhoto(Context ctx, String url, int index) {
        if (ctx == null || url == null || url.isEmpty()) return;

        try {
            String finalName = "BHTikTok_Photo_" + index + "_" + System.currentTimeMillis() + ".jpg";

            android.app.DownloadManager.Request request =
                new android.app.DownloadManager.Request(Uri.parse(url));
            request.setTitle("BHTikTok Photo Download");
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, finalName);
            request.addRequestHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)");

            android.app.DownloadManager dm = (android.app.DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
            }
        } catch (Exception e) {
            Toast.makeText(ctx, "Photo download error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
