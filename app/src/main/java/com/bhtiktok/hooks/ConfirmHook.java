package com.bhtiktok.hooks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ConfirmHook {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        hookLike(lpparam);
        hookFollow(lpparam);
    }

    private static void hookLike(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_CONFIRM_LIKE)) return;
        try {
            // Hook FullFeedVideoViewHolder.h1 as entry point to intercept like button clicks
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.adapter.FullFeedVideoViewHolder",
                lpparam.classLoader,
                "h1",
                "com.ss.android.ugc.aweme.feed.model.Aweme",
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try { interceptLikeClick(param); }
                        catch (Throwable t) { XposedBridge.log("[BHTikTok] Like intercept error: " + t.getMessage()); }
                    }
                }
            );
            XposedBridge.log("[BHTikTok] ConfirmHook: like intercept active");
        } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] ConfirmHook like error: " + t.getMessage());
        }
    }

    private static void interceptLikeClick(XC_MethodHook.MethodHookParam param) {
        try {
            Object likeView = XposedHelpers.getObjectField(param.thisObject, "mLikeView");
            if (likeView == null) likeView = XposedHelpers.getObjectField(param.thisObject, "likeView");
            if (likeView == null) return;

            android.view.View lv = (android.view.View) likeView;
            final Context ctx = lv.getContext();

            lv.setOnClickListener(v -> {
                new AlertDialog.Builder(ctx)
                    .setTitle("BHTikTok")
                    .setMessage("Like this video?")
                    .setPositiveButton("Yes", (d, w) -> {
                        // Call original like handler
                        try { XposedHelpers.callMethod(param.thisObject, "onLikeClick", v); }
                        catch (Throwable t) {}
                    })
                    .setNegativeButton("No", null)
                    .show();
            });
        } catch (Throwable t) {}
    }

    private static void hookFollow(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_CONFIRM_FOLLOW)) return;
        // Follow button is harder to intercept generically; skip for now to avoid crashes
        XposedBridge.log("[BHTikTok] ConfirmHook: follow confirm not yet implemented (safe skip)");
    }
}
