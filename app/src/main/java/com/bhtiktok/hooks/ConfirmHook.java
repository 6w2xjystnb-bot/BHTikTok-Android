package com.bhtiktok.hooks;

import android.app.AlertDialog;
import android.content.Context;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ConfirmHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        boolean confirmLike = PrefsHelper.isEnabled(PrefsHelper.FEATURE_CONFIRM_LIKE);
        boolean confirmFollow = PrefsHelper.isEnabled(PrefsHelper.FEATURE_CONFIRM_FOLLOW);

        if (!confirmLike && !confirmFollow) return;

        // Confirm Like
        if (confirmLike) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.feed.adapter.VideoViewHolder",
                    lpparam.classLoader,
                    "onLikeClick",
                    android.view.View.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Context ctx = ((android.view.View) param.args[0]).getContext();
                            showConfirmDialog(ctx, "Like this video?", () -> {
                                try {
                                    XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                                } catch (Exception e) { }
                            });
                            param.setResult(null);
                        }
                    }
                );
            } catch (Exception e) { }
        }

        // Confirm Follow
        if (confirmFollow) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.profile.ui.UserProfileActivity",
                    lpparam.classLoader,
                    "onFollowClick",
                    android.view.View.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Context ctx = ((android.view.View) param.args[0]).getContext();
                            showConfirmDialog(ctx, "Follow this user?", () -> {
                                try {
                                    XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                                } catch (Exception e) { }
                            });
                            param.setResult(null);
                        }
                    }
                );
            } catch (Exception e) { }
        }
    }

    private void showConfirmDialog(Context ctx, String message, Runnable onConfirm) {
        try {
            new AlertDialog.Builder(ctx)
                .setTitle("BHTikTok Confirm")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> onConfirm.run())
                .setNegativeButton("No", null)
                .show();
        } catch (Exception e) { }
    }
}
