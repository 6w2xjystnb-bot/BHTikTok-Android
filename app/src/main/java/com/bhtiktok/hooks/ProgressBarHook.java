package com.bhtiktok.hooks;

import android.view.View;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ProgressBarHook {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_PROGRESS_BAR)) return;
        // Progress bar hook is fragile due to obfuscation; skip to avoid crashes
        XposedBridge.log("[BHTikTok] ProgressBarHook: skipped (obfuscation too aggressive)");
    }
}
