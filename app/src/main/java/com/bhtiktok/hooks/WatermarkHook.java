package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WatermarkHook {
    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_REMOVE_WATERMARK)) return;
        XposedHelpers.findAndHookMethod(
            "com.ss.android.ugc.aweme.feed.model.Video",
            lpparam.classLoader,
            "getDownloadNoWatermarkAddr",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object result = param.getResult();
                        if (result != null) return; // already has no-watermark URL
                        // fallback: return playAddr (usually cleaner)
                        Object playAddr = XposedHelpers.callMethod(param.thisObject, "getPlayAddr");
                        if (playAddr != null) {
                            List<String> urls = (List<String>) XposedHelpers.callMethod(playAddr, "getUrlList");
                            if (urls != null && !urls.isEmpty()) {
                                String url = urls.get(0).replace("playwm", "play");
                                XposedBridge.log("[BHTikTok] WatermarkHook: using playAddr instead");
                            }
                            param.setResult(playAddr);
                        }
                    } catch (Throwable t) {
                        XposedBridge.log("[BHTikTok] WatermarkHook error: " + t.getMessage());
                    }
                }
            }
        );
        XposedBridge.log("[BHTikTok] WatermarkHook active");
    }
}
