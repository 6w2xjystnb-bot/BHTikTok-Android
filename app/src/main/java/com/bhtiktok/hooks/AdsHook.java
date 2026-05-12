package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AdsHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_HIDE_ADS)) return;

        // Hook 1: Aweme.isAd() → always false
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.model.Aweme",
                lpparam.classLoader,
                "isAd",
                XC_MethodReplacement.returnConstant(false)
            );
        } catch (Exception e) { /* class not found or obfuscated */ }

        // Hook 2: Aweme.isAds() variant
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.model.Aweme",
                lpparam.classLoader,
                "isAds",
                XC_MethodReplacement.returnConstant(false)
            );
        } catch (Exception e) { }

        // Hook 3: FeedCellViewHolder — skip bind for ads (fallback if isAd not used)
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.adapter.VideoViewHolder",
                lpparam.classLoader,
                "bind",
                "com.ss.android.ugc.aweme.feed.model.Aweme",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Object aweme = param.args[0];
                        if (aweme != null) {
                            boolean isAd = (boolean) XposedHelpers.callMethod(aweme, "isAd");
                            if (isAd) {
                                return null; // skip rendering ad
                            }
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }
                }
            );
        } catch (Exception e) { }
    }
}
