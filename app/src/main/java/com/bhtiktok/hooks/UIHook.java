package com.bhtiktok.hooks;

import android.view.View;
import android.view.ViewGroup;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class UIHook {

    public static void hideUI(XC_LoadPackage.LoadPackageParam lpparam, Class<?> viewHolderClass) {
        try {
            XposedHelpers.findAndHookMethod(viewHolderClass, "h1",
                XposedHelpers.findClass("com.ss.android.ugc.aweme.feed.model.Aweme", lpparam.classLoader),
                new XC_MethodHook() {
                    @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        View itemView = (View) XposedHelpers.getObjectField(param.thisObject, "itemView");
                        if (itemView == null) return;
                        hideByTags(itemView, "share", "comment", "like", "desc");
                    }
                }
            );
        } catch (Throwable t) {
            try {
                XposedHelpers.findAndHookMethod(viewHolderClass, "bind",
                    XposedHelpers.findClass("com.ss.android.ugc.aweme.feed.model.Aweme", lpparam.classLoader),
                    new XC_MethodHook() {
                        @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            View itemView = (View) XposedHelpers.getObjectField(param.thisObject, "itemView");
                            if (itemView == null) return;
                            hideByTags(itemView, "share", "comment", "like", "desc");
                        }
                    }
                );
            } catch (Throwable t2) { }
        }
    }

    private static void hideByTags(View root, String... tags) {
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                Object tag = child.getTag();
                if (tag != null) {
                    String ts = tag.toString().toLowerCase();
                    for (String t : tags) {
                        if (ts.contains(t)) {
                            child.setVisibility(View.GONE);
                            break;
                        }
                    }
                }
                if (child instanceof ViewGroup) hideByTags(child, tags);
            }
        }
    }
}
