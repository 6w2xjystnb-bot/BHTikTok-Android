package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RegionHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_UPLOAD_REGION)) return;

        final String selectedRegion = PrefsHelper.getString(PrefsHelper.FEATURE_SELECTED_REGION, "US");

        // Hook 1: HostProvider.getRegion()
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.app.host.HostProvider",
                lpparam.classLoader,
                "getRegion",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(selectedRegion);
                    }
                }
            );
        } catch (Throwable t) { }

        // Hook 2: RegionService
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.setting.country.RegionService",
                lpparam.classLoader,
                "getCurrentRegion",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(selectedRegion);
                    }
                }
            );
        } catch (Throwable t) { }

        // Hook 3: StoreRegionModel / RegionModel
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.setting.country.RegionModel",
                lpparam.classLoader,
                "getRegion",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(selectedRegion);
                    }
                }
            );
        } catch (Throwable t) { }

        // Hook 4: CommonHttpRequest / device registration region
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.app.host.HostProvider",
                lpparam.classLoader,
                "getStoreRegion",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(selectedRegion);
                    }
                }
            );
        } catch (Throwable t) { }

        // Hook 5: Anti-addiction / sim region
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.setting.country.CountryChangeActivity",
                lpparam.classLoader,
                "getCurrentRegion",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(selectedRegion);
                    }
                }
            );
        } catch (Throwable t) { }
    }
}
