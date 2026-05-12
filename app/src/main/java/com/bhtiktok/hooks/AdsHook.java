package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AdsHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_HIDE_ADS)) return;
        XposedHelpers.findAndHookMethod(
            "com.ss.android.ugc.aweme.feed.model.Aweme",
            lpparam.classLoader,
            "isAd",
            XC_MethodReplacement.returnConstant(false)
        );
        XposedBridge.log("[BHTikTok] AdsHook: isAd() → false");
    }
}
