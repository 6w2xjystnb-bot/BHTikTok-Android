package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AnonymousSeenHook {

    public static void hookSeen(XC_LoadPackage.LoadPackageParam lpparam, Class<?> chatClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_ANONYMOUS_SEEN)) return;
        try {
            XposedHelpers.findAndHookMethod(chatClass, "markMessageAsRead", new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(null);
                }
            });
        } catch (Throwable t) { }
    }

    public static void hookTyping(XC_LoadPackage.LoadPackageParam lpparam, Class<?> imClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_ANONYMOUS_SEEN)) return;
        try {
            XposedHelpers.findAndHookMethod(imClass, "sendTypingStatus", new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(null);
                }
            });
        } catch (Throwable t) { }
    }
}
