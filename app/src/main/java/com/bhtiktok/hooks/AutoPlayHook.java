package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AutoPlayHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_AUTO_PLAY)) return;

        // Hook on video completion to auto-skip to next
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.AwemeVideoPlayerController",
                lpparam.classLoader,
                "onCompletion",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Object feedFragment = XposedHelpers.getObjectField(param.thisObject, "mFeedFragment");
                            if (feedFragment != null) {
                                XposedHelpers.callMethod(feedFragment, "moveToNext");
                            }
                        } catch (Exception e) {
                            // fallback: try scroll or trigger swipe
                            try {
                                Object recyclerView = XposedHelpers.getObjectField(param.thisObject, "mRecyclerView");
                                if (recyclerView != null) {
                                    XposedHelpers.callMethod(recyclerView, "scrollToPosition",
                                        (int) XposedHelpers.callMethod(recyclerView, "getCurrentPosition") + 1);
                                }
                            } catch (Exception e2) { }
                        }
                    }
                }
            );
        } catch (Exception e) { }
    }
}
