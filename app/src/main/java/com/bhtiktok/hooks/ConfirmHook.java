package com.bhtiktok.hooks;

import android.app.AlertDialog;
import android.content.Context;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ConfirmHook {

    public static void hookLike(XC_LoadPackage.LoadPackageParam lpparam, Class<?> viewHolderClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_CONFIRM_LIKE)) return;
        try {
            XposedHelpers.findAndHookMethod(viewHolderClass, "onLikeClick", android.view.View.class, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Context ctx = ((android.view.View) param.args[0]).getContext();
                    showDialog(ctx, "Like this video?", () -> {
                        try { XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args); }
                        catch (Exception e) { }
                    });
                    param.setResult(null);
                }
            });
        } catch (Throwable t) { }
    }

    public static void hookFollow(XC_LoadPackage.LoadPackageParam lpparam, Class<?> profileClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_CONFIRM_FOLLOW)) return;
        try {
            XposedHelpers.findAndHookMethod(profileClass, "onFollowClick", android.view.View.class, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Context ctx = ((android.view.View) param.args[0]).getContext();
                    showDialog(ctx, "Follow this user?", () -> {
                        try { XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args); }
                        catch (Exception e) { }
                    });
                    param.setResult(null);
                }
            });
        } catch (Throwable t) { }
    }

    private static void showDialog(Context ctx, String msg, Runnable onYes) {
        try {
            new AlertDialog.Builder(ctx).setTitle("BHTikTok").setMessage(msg)
                .setPositiveButton("Yes", (d,w)->onYes.run())
                .setNegativeButton("No", null).show();
        } catch (Exception e) { }
    }
}
