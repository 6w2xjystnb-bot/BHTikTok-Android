package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FakeChangesHook {

    public static void hookVerified(XC_LoadPackage.LoadPackageParam lpparam, Class<?> userClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_FAKE_VERIFIED)) return;
        try {
            XposedHelpers.findAndHookMethod(userClass, "getIsVerifiedTako", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(1);
                }
            });
        } catch (Throwable t) { }
        try {
            XposedHelpers.findAndHookMethod(userClass, "isEmailVerified", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(true);
                }
            });
        } catch (Throwable t) { }
        try {
            XposedHelpers.findAndHookMethod(userClass, "isVerified", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(true);
                }
            });
        } catch (Throwable t) { }
    }

    public static void hookFollowers(XC_LoadPackage.LoadPackageParam lpparam, Class<?> userClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_FAKE_FOLLOWER_COUNT)) return;
        try {
            XposedHelpers.findAndHookMethod(userClass, "getFollowerCount", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(999999);
                }
            });
            XposedHelpers.findAndHookMethod(userClass, "getFollowingCount", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(999);
                }
            });
        } catch (Throwable t) { }
    }

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        // legacy fallback
    }
}
