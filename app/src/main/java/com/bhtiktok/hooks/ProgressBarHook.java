package com.bhtiktok.hooks;

import android.view.View;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ProgressBarHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_PROGRESS_BAR)) return;

        // Hook video player progress to force show progress bar
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.player.VideoPlayerView",
                lpparam.classLoader,
                "onProgressChanged",
                int.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object progressBar = XposedHelpers.getObjectField(param.thisObject, "progressBar");
                            if (progressBar instanceof View) {
                                ((View) progressBar).setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) { }
                        try {
                            Object seekBar = XposedHelpers.getObjectField(param.thisObject, "seekBar");
                            if (seekBar instanceof View) {
                                ((View) seekBar).setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) { }
                    }
                }
            );
        } catch (Exception e) { }

        // Alternative: hook AwemeVideoPlayerController.onProgressUpdate
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.AwemeVideoPlayerController",
                lpparam.classLoader,
                "onProgressUpdate",
                long.class, long.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object progressView = XposedHelpers.getObjectField(param.thisObject, "mProgressBar");
                            if (progressView instanceof View) {
                                ((View) progressView).setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) { }
                    }
                }
            );
        } catch (Exception e) { }
    }
}
