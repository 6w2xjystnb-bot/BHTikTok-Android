package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AutoPlayHook {

    public static void hookPlayer(XC_LoadPackage.LoadPackageParam lpparam, Class<?> playerClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_AUTO_PLAY)) return;
        try {
            XposedHelpers.findAndHookMethod(playerClass, "onCompletion", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object feed = XposedHelpers.getObjectField(param.thisObject, "mFeedFragment");
                        if (feed != null) { XposedHelpers.callMethod(feed, "moveToNext"); return; }
                    } catch (Throwable t) { }
                    try {
                        Object rv = XposedHelpers.getObjectField(param.thisObject, "mRecyclerView");
                        if (rv != null) {
                            int cur = (int) XposedHelpers.callMethod(rv, "getCurrentPosition");
                            XposedHelpers.callMethod(rv, "scrollToPosition", cur + 1);
                            return;
                        }
                    } catch (Throwable t) { }
                    try {
                        Object vp = XposedHelpers.getObjectField(param.thisObject, "mViewPager");
                        if (vp != null) {
                            int cur = (int) XposedHelpers.callMethod(vp, "getCurrentItem");
                            XposedHelpers.callMethod(vp, "setCurrentItem", cur + 1);
                        }
                    } catch (Throwable t) { }
                }
            });
        } catch (Throwable t) { }
    }
}
