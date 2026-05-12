package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AdsHook {

    public static void hookAd(XC_LoadPackage.LoadPackageParam lpparam, Class<?> awemeClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_HIDE_ADS)) return;
        try {
            XposedHelpers.findAndHookMethod(awemeClass, "isAd",
                XC_MethodReplacement.returnConstant(false));
        } catch (Throwable t) { }
    }

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        // legacy fallback
    }
}
