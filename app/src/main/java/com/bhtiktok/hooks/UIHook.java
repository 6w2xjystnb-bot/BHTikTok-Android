package com.bhtiktok.hooks;

import android.view.View;
import android.view.ViewGroup;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class UIHook {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_HIDE_EMOJI)
            && !PrefsHelper.isEnabled(PrefsHelper.FEATURE_HIDE_TOP_ITEMS)) return;

        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.adapter.FullFeedVideoViewHolder",
                lpparam.classLoader,
                "h1",
                "com.ss.android.ugc.aweme.feed.model.Aweme",
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try { hideViews(param); }
                        catch (Throwable t) { XposedBridge.log("[BHTikTok] UIHook error: " + t.getMessage()); }
                    }
                }
            );
            XposedBridge.log("[BHTikTok] UIHook active");
        } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] UIHook failed: " + t.getMessage());
        }
    }

    private static void hideViews(XC_MethodHook.MethodHookParam param) {
        try {
            View itemView = (View) XposedHelpers.getObjectField(param.thisObject, "itemView");
            if (itemView == null) return;
            hideByTags(itemView, "share", "comment", "like", "desc");
        } catch (Throwable t) {}
    }

    private static void hideByTags(View root, String... tags) {
        if (!(root instanceof ViewGroup)) return;
        ViewGroup vg = (ViewGroup) root;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            Object tag = child.getTag();
            if (tag != null) {
                String ts = tag.toString().toLowerCase();
                for (String t : tags) {
                    if (ts.contains(t)) { child.setVisibility(View.GONE); break; }
                }
            }
            if (child instanceof ViewGroup) hideByTags(child, tags);
        }
    }
}
