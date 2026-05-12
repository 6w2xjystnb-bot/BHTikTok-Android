package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;
import com.bhtiktok.utils.VideoUrlExtractor;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WatermarkHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_REMOVE_WATERMARK)) return;

        // Hook 1: Video.getPlayAddr() → modify URLs on-the-fly
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.model.Video",
                lpparam.classLoader,
                "getPlayAddr",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object urlModel = param.getResult();
                        if (urlModel == null) return;

                        List<String> urls = (List<String>) XposedHelpers.callMethod(urlModel, "getUrlList");
                        if (urls == null || urls.isEmpty()) return;

                        List<String> noWm = VideoUrlExtractor.getNoWatermarkUrls(urls);
                        XposedHelpers.callMethod(urlModel, "setUrlList", noWm);
                        param.setResult(urlModel);
                    }
                }
            );
        } catch (Throwable t) { }

        // Hook 2: Video.getDownloadAddr() → also clean
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.model.Video",
                lpparam.classLoader,
                "getDownloadAddr",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object urlModel = param.getResult();
                        if (urlModel == null) return;

                        List<String> urls = (List<String>) XposedHelpers.callMethod(urlModel, "getUrlList");
                        if (urls == null || urls.isEmpty()) return;

                        List<String> noWm = VideoUrlExtractor.getNoWatermarkUrls(urls);
                        XposedHelpers.callMethod(urlModel, "setUrlList", noWm);
                        param.setResult(urlModel);
                    }
                }
            );
        } catch (Throwable t) { }

        // Hook 3: Watermark builder (if exists)
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.watermark.WaterMarkBuilder",
                lpparam.classLoader,
                "build",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                }
            );
        } catch (Throwable t) { }

        // Hook 4: Aweme.getWatermarkInfo → null
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.model.Aweme",
                lpparam.classLoader,
                "getWatermarkInfo",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                }
            );
        } catch (Throwable t) { }
    }
}
