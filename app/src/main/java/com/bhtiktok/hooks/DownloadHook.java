package com.bhtiktok.hooks;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bhtiktok.utils.BHDownloadManager;
import com.bhtiktok.utils.PrefsHelper;
import com.bhtiktok.utils.VideoUrlExtractor;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DownloadHook {

    public static void hookViewHolder(XC_LoadPackage.LoadPackageParam lpparam, Class<?> viewHolderClass) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_DOWNLOAD_BUTTON)) return;
        try {
            XposedHelpers.findAndHookMethod(viewHolderClass, "h1",
                XposedHelpers.findClass("com.ss.android.ugc.aweme.feed.model.Aweme", lpparam.classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        injectButton(param);
                    }
                }
            );
        } catch (Throwable t) {
            // fallback: try "bind"
            try {
                XposedHelpers.findAndHookMethod(viewHolderClass, "bind",
                    XposedHelpers.findClass("com.ss.android.ugc.aweme.feed.model.Aweme", lpparam.classLoader),
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            injectButton(param);
                        }
                    }
                );
            } catch (Throwable t2) { }
        }
    }

    private static void injectButton(XC_MethodHook.MethodHookParam param) {
        try {
            Object aweme = param.args[0];
            if (aweme == null) return;
            View itemView = (View) XposedHelpers.getObjectField(param.thisObject, "itemView");
            if (itemView == null) return;
            Context ctx = itemView.getContext();
            ViewGroup container = findContainer(itemView);
            if (container == null) return;
            String tag = "bhtiktok_download_btn";
            if (container.findViewWithTag(tag) != null) return;

            Button btn = new Button(ctx);
            btn.setTag(tag);
            btn.setText("\u2B07");
            btn.setTextSize(12f);
            btn.setBackgroundColor(Color.parseColor("#FE2C55"));
            btn.setTextColor(Color.WHITE);

            ViewGroup.LayoutParams lp;
            if (container instanceof FrameLayout) {
                FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                flp.gravity = Gravity.TOP | Gravity.END;
                flp.setMargins(0, 20, 20, 0);
                lp = flp;
            } else {
                lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            btn.setOnClickListener(v -> {
                try { handleDownload(ctx, aweme); }
                catch (Exception ex) { Toast.makeText(ctx, "Error: "+ex.getMessage(), Toast.LENGTH_SHORT).show(); }
            });
            container.addView(btn, lp);
        } catch (Throwable t) { }
    }

    private static ViewGroup findContainer(View root) {
        if (root instanceof FrameLayout) return (FrameLayout) root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i=0; i<vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                if (child instanceof FrameLayout) return (FrameLayout) child;
                if (child instanceof ViewGroup) return (ViewGroup) child;
            }
            return vg;
        }
        return null;
    }

    private static void handleDownload(android.content.Context ctx, Object aweme) throws Exception {
        Object video = XposedHelpers.callMethod(aweme, "getVideo");
        if (video != null) {
            Object playAddr = XposedHelpers.callMethod(video, "getPlayAddr");
            if (playAddr != null) {
                List<String> urlList = (List<String>) XposedHelpers.callMethod(playAddr, "getUrlList");
                String bestUrl = VideoUrlExtractor.bestUrl(urlList);
                if (bestUrl != null) {
                    String desc = (String) XposedHelpers.callMethod(aweme, "getDesc");
                    BHDownloadManager.downloadVideo(ctx, bestUrl, desc, desc);
                    return;
                }
            }
        }
        Object music = XposedHelpers.callMethod(aweme, "getMusic");
        if (music != null) {
            Object playUrl = XposedHelpers.callMethod(music, "getPlayUrl");
            if (playUrl != null) {
                List<String> urlList = (List<String>) XposedHelpers.callMethod(playUrl, "getUrlList");
                if (urlList != null && !urlList.isEmpty()) {
                    String title = (String) XposedHelpers.callMethod(music, "getTitle");
                    BHDownloadManager.downloadMusic(ctx, urlList.get(0), title);
                    return;
                }
            }
        }
        Toast.makeText(ctx, "No URL found", Toast.LENGTH_SHORT).show();
    }
}
