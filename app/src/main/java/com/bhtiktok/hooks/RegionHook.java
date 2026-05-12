package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RegionHook {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_UPLOAD_REGION)) return;
        final String region = PrefsHelper.getString(PrefsHelper.FEATURE_SELECTED_REGION, "US");

        String[] classes = {
            "com.ss.android.ugc.aweme.app.host.HostProvider",
            "com.ss.android.ugc.aweme.services.RegionService",
            "com.ss.android.ugc.aweme.IRegionService",
        };

        for (String cls : classes) {
            try {
                XposedHelpers.findAndHookMethod(cls, lpparam.classLoader, "getRegion", new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try { param.setResult(region); } catch (Throwable t) {}
                    }
                });
                XposedBridge.log("[BHTikTok] RegionHook: hooked " + cls + ".getRegion() → " + region);
                return;
            } catch (Throwable t) {}
        }
        XposedBridge.log("[BHTikTok] RegionHook: no suitable class found");
    }
}
