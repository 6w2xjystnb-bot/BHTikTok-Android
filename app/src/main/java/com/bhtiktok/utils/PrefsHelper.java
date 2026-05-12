package com.bhtiktok.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {
    private static final String PREFS_NAME = "bhtiktok_settings";
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    // Feature flags
    public static final String FEATURE_DOWNLOAD_BUTTON = "feature_download_button";
    public static final String FEATURE_REMOVE_WATERMARK = "feature_remove_watermark";
    public static final String FEATURE_UPLOAD_REGION = "feature_upload_region";
    public static final String FEATURE_SELECTED_REGION = "feature_selected_region";
    public static final String FEATURE_HIDE_ADS = "feature_hide_ads";
    public static final String FEATURE_HIDE_EMOJI = "feature_hide_emoji";
    public static final String FEATURE_HIDE_TOP_ITEMS = "feature_hide_top_items";
    public static final String FEATURE_PROGRESS_BAR = "feature_progress_bar";
    public static final String FEATURE_AUTO_PLAY = "feature_auto_play";
    public static final String FEATURE_FAKE_VERIFIED = "feature_fake_verified";
    public static final String FEATURE_FAKE_FOLLOWER_COUNT = "feature_fake_follower_count";
    public static final String FEATURE_FAKE_FOLLOWING_COUNT = "feature_fake_following_count";
    public static final String FEATURE_FAKE_LIKE_COUNT = "feature_fake_like_count";
    public static final String FEATURE_CONFIRM_LIKE = "feature_confirm_like";
    public static final String FEATURE_CONFIRM_FOLLOW = "feature_confirm_follow";
    public static final String FEATURE_ANONYMOUS_SEEN = "feature_anonymous_seen";
    public static final String FEATURE_DISABLE_LIVE = "feature_disable_live";
    public static final String FEATURE_DISABLE_SENSITIVE = "feature_disable_sensitive";

    public static void init(Context ctx) {
        if (prefs == null) {
            prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
        }
    }

    public static boolean isEnabled(String key) {
        return prefs != null && prefs.getBoolean(key, false);
    }

    public static boolean isEnabled(String key, boolean def) {
        return prefs != null && prefs.getBoolean(key, def);
    }

    public static String getString(String key, String def) {
        return prefs != null ? prefs.getString(key, def) : def;
    }

    public static void setBoolean(String key, boolean value) {
        if (editor != null) {
            editor.putBoolean(key, value).apply();
        }
    }

    public static void setString(String key, String value) {
        if (editor != null) {
            editor.putString(key, value).apply();
        }
    }
}
