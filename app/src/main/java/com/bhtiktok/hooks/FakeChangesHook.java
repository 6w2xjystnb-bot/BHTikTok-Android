package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FakeChangesHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        boolean fakeVerified = PrefsHelper.isEnabled(PrefsHelper.FEATURE_FAKE_VERIFIED);
        boolean fakeFollowers = PrefsHelper.isEnabled(PrefsHelper.FEATURE_FAKE_FOLLOWER_COUNT);
        boolean fakeFollowing = PrefsHelper.isEnabled(PrefsHelper.FEATURE_FAKE_FOLLOWING_COUNT);
        boolean fakeLikes = PrefsHelper.isEnabled(PrefsHelper.FEATURE_FAKE_LIKE_COUNT);

        if (!fakeVerified && !fakeFollowers && !fakeFollowing && !fakeLikes) return;

        // User.isVerified() → true
        if (fakeVerified) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.profile.model.User",
                    lpparam.classLoader,
                    "isVerified",
                    XC_MethodReplacement.returnConstant(true)
                );
            } catch (Throwable t) { }
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.profile.model.User",
                    lpparam.classLoader,
                    "getVerificationType",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(1); // verified type
                        }
                    }
                );
            } catch (Throwable t) { }
        }

        // User.getFollowerCount() → 999999
        if (fakeFollowers) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.profile.model.User",
                    lpparam.classLoader,
                    "getFollowerCount",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(999999L);
                        }
                    }
                );
            } catch (Throwable t) { }
        }

        // User.getFollowingCount() → 999999
        if (fakeFollowing) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.profile.model.User",
                    lpparam.classLoader,
                    "getFollowingCount",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(999999L);
                        }
                    }
                );
            } catch (Throwable t) { }
        }

        // AwemeStatistics.getDiggCount() / getShareCount() etc
        if (fakeLikes) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.feed.model.AwemeStatistics",
                    lpparam.classLoader,
                    "getDiggCount",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(999999L);
                        }
                    }
                );
            } catch (Throwable t) { }
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.feed.model.AwemeStatistics",
                    lpparam.classLoader,
                    "getShareCount",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(999999L);
                        }
                    }
                );
            } catch (Throwable t) { }
        }
    }
}
