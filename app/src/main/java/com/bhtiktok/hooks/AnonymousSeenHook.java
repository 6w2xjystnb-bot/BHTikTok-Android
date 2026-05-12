package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AnonymousSeenHook {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        hookSeen(lpparam);
        hookTyping(lpparam);
    }

    private static void hookSeen(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_ANONYMOUS_SEEN)) return;
        String[] classes = {
            "com.ss.android.ugc.aweme.im.sdk.chat.controller.ChatRoomController",
            "com.ss.android.ugc.aweme.im.sdk.chat.ChatController",
        };
        for (String cls : classes) {
            try {
                XposedHelpers.findAndHookMethod(cls, lpparam.classLoader, "markMessageAsRead", new XC_MethodHook() {
                    @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try { param.setResult(null); } catch (Throwable t) {}
                    }
                });
                XposedBridge.log("[BHTikTok] AnonymousSeenHook: hooked " + cls + ".markMessageAsRead()");
                return;
            } catch (Throwable t) {}
        }
        XposedBridge.log("[BHTikTok] AnonymousSeenHook: no chat controller found");
    }

    private static void hookTyping(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_ANONYMOUS_SEEN)) return;
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.im.sdk.service.IMService",
                lpparam.classLoader,
                "sendTypingStatus",
                new XC_MethodHook() {
                    @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try { param.setResult(null); } catch (Throwable t) {}
                    }
                }
            );
            XposedBridge.log("[BHTikTok] AnonymousSeenHook: sendTypingStatus blocked");
        } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] AnonymousSeenHook typing error: " + t.getMessage());
        }
    }
}
