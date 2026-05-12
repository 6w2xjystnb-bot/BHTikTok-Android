package com.bhtiktok;

import android.app.Application;
import android.content.Context;

import com.bhtiktok.hooks.AdsHook;
import com.bhtiktok.hooks.AnonymousSeenHook;
import com.bhtiktok.hooks.AutoPlayHook;
import com.bhtiktok.hooks.ConfirmHook;
import com.bhtiktok.hooks.DownloadHook;
import com.bhtiktok.hooks.FakeChangesHook;
import com.bhtiktok.hooks.ProgressBarHook;
import com.bhtiktok.hooks.RegionHook;
import com.bhtiktok.hooks.UIHook;
import com.bhtiktok.hooks.WatermarkHook;
import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BHTikTokModule implements IXposedHookLoadPackage {

    public static final String[] TARGET_PACKAGES = {
        "com.zhiliaoapp.musically",
        "com.ss.android.ugc.aweme"
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        boolean isTarget = false;
        for (String pkg : TARGET_PACKAGES) {
            if (lpparam.packageName.equals(pkg)) {
                isTarget = true;
                break;
            }
        }
        if (!isTarget) return;

        // Hook Application.onCreate to get Context early
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Application app = (Application) param.thisObject;
                Context ctx = app.getApplicationContext();
                PrefsHelper.init(ctx);
            }
        });

        // Initialize all hooks
        new DownloadHook().init(lpparam);
        new WatermarkHook().init(lpparam);
        new RegionHook().init(lpparam);
        new AdsHook().init(lpparam);
        new FakeChangesHook().init(lpparam);
        new ConfirmHook().init(lpparam);
        new ProgressBarHook().init(lpparam);
        new UIHook().init(lpparam);
        new AutoPlayHook().init(lpparam);
        new AnonymousSeenHook().init(lpparam);
    }
}
