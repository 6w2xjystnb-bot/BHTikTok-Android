package com.bhtiktok.hooks;

import android.view.View;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ProgressBarHook {

    public static void hookProgress(XC_LoadPackage.LoadPackageParam lpparam, Class<?> progressClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_PROGRESS_BAR)) return;
        try {
            XposedHelpers.findAndHookMethod(progressClass, "onProgressChanged", int.class, int.class, new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object pb = XposedHelpers.getObjectField(param.thisObject, "progressBar");
                        if (pb instanceof View) ((View) pb).setVisibility(View.VISIBLE);
                    } catch (Throwable t) { }
                    try {
                        Object sb = XposedHelpers.getObjectField(param.thisObject, "seekBar");
                        if (sb instanceof View) ((View) sb).setVisibility(View.VISIBLE);
                    } catch (Throwable t) { }
                }
            });
        } catch (Throwable t) { }
    }
}
