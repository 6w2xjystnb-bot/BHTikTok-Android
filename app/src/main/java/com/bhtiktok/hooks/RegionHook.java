package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RegionHook {

    public static void hookRegion(XC_LoadPackage.LoadPackageParam lpparam, Class<?> regionClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_UPLOAD_REGION)) return;
        final String region = PrefsHelper.getString(PrefsHelper.FEATURE_SELECTED_REGION, "US");
        try {
            XposedHelpers.findAndHookMethod(regionClass, "getRegion", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(region);
                }
            });
        } catch (Throwable t) { }
    }

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        // legacy fallback
    }
}
