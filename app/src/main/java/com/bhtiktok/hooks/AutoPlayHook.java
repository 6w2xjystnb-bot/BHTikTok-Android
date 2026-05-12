package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AutoPlayHook {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_AUTO_PLAY)) return;
        // Try common video player completion methods
        String[] classes = {
            "com.ss.android.ugc.aweme.feed.player.VideoPlayerController",
            "com.ss.android.ugc.aweme.feed.player.FeedPlayerController",
            "com.ss.android.ugc.aweme.video.player.VideoPlayer",
        };
        String[] methods = {"onCompletion", "onPlayerCompletion", "onCompletionListener"};

        for (String cls : classes) {
            for (String method : methods) {
                try {
                    XposedHelpers.findAndHookMethod(cls, lpparam.classLoader, method, new XC_MethodHook() {
                        @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try { autoAdvance(param); }
                            catch (Throwable t) { XposedBridge.log("[BHTikTok] AutoPlay error: " + t.getMessage()); }
                        }
                    });
                    XposedBridge.log("[BHTikTok] AutoPlayHook: hooked " + cls + "." + method);
                    return; // success, stop trying
                } catch (Throwable t) {}
            }
        }
        XposedBridge.log("[BHTikTok] AutoPlayHook: no suitable method found");
    }

    private static void autoAdvance(XC_MethodHook.MethodHookParam param) {
        // Try to find RecyclerView or ViewPager and advance
        try {
            Object rv = XposedHelpers.getObjectField(param.thisObject, "mRecyclerView");
            if (rv != null) {
                int pos = (int) XposedHelpers.callMethod(rv, "getCurrentPosition");
                XposedHelpers.callMethod(rv, "scrollToPosition", pos + 1);
                return;
            }
        } catch (Throwable t) {}
        try {
            Object vp = XposedHelpers.getObjectField(param.thisObject, "mViewPager");
            if (vp != null) {
                int pos = (int) XposedHelpers.callMethod(vp, "getCurrentItem");
                XposedHelpers.callMethod(vp, "setCurrentItem", pos + 1);
            }
        } catch (Throwable t) {}
    }
}
