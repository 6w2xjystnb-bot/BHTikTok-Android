package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WatermarkHook {

    public static void hookVideo(XC_LoadPackage.LoadPackageParam lpparam, Class<?> videoClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_REMOVE_WATERMARK)) return;
        try {
            XposedHelpers.findAndHookMethod(videoClass, "getDownloadNoWatermarkAddr", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object noWater = param.getResult();
                    if (noWater != null) return;
                    try {
                        Object playAddr = XposedHelpers.callMethod(param.thisObject, "getPlayAddr");
                        if (playAddr == null) return;
                        List<String> urlList = (List<String>) XposedHelpers.callMethod(playAddr, "getUrlList");
                        if (urlList == null || urlList.isEmpty()) return;
                        String url = urlList.get(0).replace("playwm", "play").replaceAll("\\?.*", "");
                        // recreate UrlModel without watermark params
                        Object urlModel = XposedHelpers.newInstance(
                            XposedHelpers.findClass("com.ss.android.ugc.aweme.base.model.UrlModel", lpparam.classLoader));
                        XposedHelpers.callMethod(urlModel, "setUrlList", java.util.Collections.singletonList(url));
                        param.setResult(urlModel);
                    } catch (Throwable t) { }
                }
            });
        } catch (Throwable t) { }
    }

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        // legacy fallback
    }
}
