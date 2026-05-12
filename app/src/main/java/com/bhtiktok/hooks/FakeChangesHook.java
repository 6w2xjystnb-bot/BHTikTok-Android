package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FakeChangesHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        hookVerified(lpparam);
        hookFollowers(lpparam);
    }

    private static void hookVerified(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_FAKE_VERIFIED)) return;
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.profile.model.User",
                lpparam.classLoader,
                "getIsVerifiedTako",
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try { param.setResult(1); } catch (Throwable t) {}
                    }
                }
            );
            XposedBridge.log("[BHTikTok] FakeChangesHook: getIsVerifiedTako() → 1");
        } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] FakeChangesHook verified error: " + t.getMessage());
        }
    }

    private static void hookFollowers(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_FAKE_FOLLOWER_COUNT)) return;
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.profile.model.User",
                lpparam.classLoader,
                "getFollowerCount",
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try { param.setResult(999999); } catch (Throwable t) {}
                    }
                }
            );
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.profile.model.User",
                lpparam.classLoader,
                "getFollowingCount",
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try { param.setResult(999); } catch (Throwable t) {}
                    }
                }
            );
            XposedBridge.log("[BHTikTok] FakeChangesHook: follower counts faked");
        } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] FakeChangesHook followers error: " + t.getMessage());
        }
    }
}
