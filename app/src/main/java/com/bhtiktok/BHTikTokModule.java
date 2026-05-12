package com.bhtiktok;

import com.bhtiktok.hooks.AdsHook;
import com.bhtiktok.hooks.AnonymousSeenHook;
import com.bhtiktok.hooks.AutoPlayHook;
import com.bhtiktok.hooks.ConfirmHook;
import com.bhtiktok.hooks.DownloadHook;
import com.bhtiktok.hooks.FakeChangesHook;
import com.bhtiktok.hooks.ProgressBarHook;
import com.bhtiktok.hooks.RegionHook;
import com.bhtiktok.hooks.UIHook;
import com.bhtiktok.hooks.WatermarkHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BHTikTokModule implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"com.zhiliaoapp.musically".equals(lpparam.packageName)) return;
        XposedBridge.log("[BHTikTok] Loading module for TikTok...");

        // --- resolve known classes via reflection ---
        Class<?> awemeClass = safeClass(lpparam, "com.ss.android.ugc.aweme.feed.model.Aweme");
        Class<?> videoClass = safeClass(lpparam, "com.ss.android.ugc.aweme.feed.model.Video");
        Class<?> userClass = safeClass(lpparam, "com.ss.android.ugc.aweme.profile.model.User");
        Class<?> viewHolderClass = safeClass(lpparam, "com.ss.android.ugc.aweme.feed.adapter.FullFeedVideoViewHolder");
        Class<?> urlModelClass = safeClass(lpparam, "com.ss.android.ugc.aweme.base.model.UrlModel");

        // --- Ads ---
        if (awemeClass != null) AdsHook.hookAd(lpparam, awemeClass);

        // --- Watermark ---
        if (videoClass != null) WatermarkHook.hookVideo(lpparam, videoClass);

        // --- Fake Changes ---
        if (userClass != null) {
            FakeChangesHook.hookVerified(lpparam, userClass);
            FakeChangesHook.hookFollowers(lpparam, userClass);
        }

        // --- Download button injection ---
        if (viewHolderClass != null) DownloadHook.hookViewHolder(lpparam, viewHolderClass);

        // --- Confirm dialogs (like/follow) ---
        if (viewHolderClass != null) ConfirmHook.hookLike(lpparam, viewHolderClass);
        // profile follow button — try common profile fragment/activity names
        Class<?> profileFragment = safeClass(lpparam, "com.ss.android.ugc.aweme.profile.ui.ProfileFragment");
        if (profileFragment == null) profileFragment = safeClass(lpparam, "com.ss.android.ugc.aweme.profile.ui.UserProfileFragment");
        if (profileFragment != null) ConfirmHook.hookFollow(lpparam, profileFragment);

        // --- Progress bar ---
        // attempt to hook common player wrapper classes
        Class<?> playerWrapper = safeClass(lpparam, "com.ss.android.ugc.aweme.feed.player.FeedPlayerController");
        if (playerWrapper == null) playerWrapper = safeClass(lpparam, "com.ss.android.ugc.aweme.feed.player.VideoPlayerController");
        if (playerWrapper != null) ProgressBarHook.hookProgress(lpparam, playerWrapper);

        // --- Auto play ---
        if (playerWrapper != null) AutoPlayHook.hookPlayer(lpparam, playerWrapper);

        // --- Region ---
        Class<?> regionProvider = safeClass(lpparam, "com.ss.android.ugc.aweme.app.host.HostProvider");
        if (regionProvider == null) regionProvider = safeClass(lpparam, "com.ss.android.ugc.aweme.services.RegionService");
        if (regionProvider != null) RegionHook.hookRegion(lpparam, regionProvider);

        // --- Anonymous seen ---
        Class<?> chatController = safeClass(lpparam, "com.ss.android.ugc.aweme.im.sdk.chat.controller.ChatRoomController");
        if (chatController == null) chatController = safeClass(lpparam, "com.ss.android.ugc.aweme.im.sdk.chat.ChatController");
        if (chatController != null) AnonymousSeenHook.hookSeen(lpparam, chatController);

        Class<?> imService = safeClass(lpparam, "com.ss.android.ugc.aweme.im.sdk.service.IMService");
        if (imService != null) AnonymousSeenHook.hookTyping(lpparam, imService);

        // --- UI cleanup (hide like/comment/share/desc) ---
        if (viewHolderClass != null) UIHook.hideUI(lpparam, viewHolderClass);

        // --- legacy init calls (no-op now) ---
        new RegionHook().init(lpparam);
        new FakeChangesHook().init(lpparam);
        new WatermarkHook().init(lpparam);
        new AdsHook().init(lpparam);

        XposedBridge.log("[BHTikTok] All hooks initialized.");
    }

    private Class<?> safeClass(XC_LoadPackage.LoadPackageParam lpparam, String name) {
        try {
            return lpparam.classLoader.loadClass(name);
        } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] Class not found: " + name);
            return null;
        }
    }
}
