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
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DownloadHook {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_DOWNLOAD_BUTTON)) return;
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.adapter.FullFeedVideoViewHolder",
                lpparam.classLoader,
                "h1",
                "com.ss.android.ugc.aweme.feed.model.Aweme",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try { injectButton(param); }
                        catch (Throwable t) { XposedBridge.log("[BHTikTok] Download inject error: " + t.getMessage()); }
                    }
                }
            );
            XposedBridge.log("[BHTikTok] DownloadHook: hooked h1()");
        } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] DownloadHook h1 not found, trying constructor...");
            try {
                XposedHelpers.findAndHookConstructor(
                    "com.ss.android.ugc.aweme.feed.adapter.FullFeedVideoViewHolder",
                    lpparam.classLoader,
                    android.view.View.class,
                    new XC_MethodHook() {
                        @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[BHTikTok] DownloadHook: ViewHolder created");
                        }
                    }
                );
            } catch (Throwable t2) {
                XposedBridge.log("[BHTikTok] DownloadHook constructor failed: " + t2.getMessage());
            }
        }
    }

    private static void injectButton(XC_MethodHook.MethodHookParam param) {
        try {
            Object aweme = param.args[0];
            if (aweme == null) return;

            View itemView = null;
            try {
                itemView = (View) XposedHelpers.getObjectField(param.thisObject, "itemView");
            } catch (Throwable t) {
                // try getItemView() method
                try { itemView = (View) XposedHelpers.callMethod(param.thisObject, "getItemView"); }
                catch (Throwable t2) {}
            }
            if (itemView == null) return;

            Context ctx = itemView.getContext();
            if (ctx == null) return;

            // Find a FrameLayout to add the button
            ViewGroup container = findFrameLayout(itemView);
            if (container == null) return;

            String tag = "bhtiktok_dl_btn";
            if (container.findViewWithTag(tag) != null) return;

            Button btn = new Button(ctx);
            btn.setTag(tag);
            btn.setText("⬇");
            btn.setTextSize(14f);
            btn.setBackgroundColor(Color.parseColor("#FE2C55"));
            btn.setTextColor(Color.WHITE);
            btn.setPadding(20, 10, 20, 10);

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            );
            lp.gravity = Gravity.TOP | Gravity.END;
            lp.setMargins(0, 40, 40, 0);

            btn.setOnClickListener(v -> {
                try { handleDownload(ctx, aweme); }
                catch (Exception ex) { Toast.makeText(ctx, "DL err: " + ex.getMessage(), Toast.LENGTH_SHORT).show(); }
            });

            container.addView(btn, lp);
        } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] injectButton error: " + t.getMessage());
        }
    }

    private static ViewGroup findFrameLayout(View root) {
        if (root instanceof FrameLayout) return (FrameLayout) root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                if (child instanceof FrameLayout) return (FrameLayout) child;
                ViewGroup found = findFrameLayout(child);
                if (found != null) return found;
            }
            return vg; // fallback: any ViewGroup
        }
        return null;
    }

    private static void handleDownload(Context ctx, Object aweme) throws Exception {
        String url = null;
        String desc = null;

        // Try video
        try {
            Object video = XposedHelpers.callMethod(aweme, "getVideo");
            if (video != null) {
                Object playAddr = XposedHelpers.callMethod(video, "getPlayAddr");
                if (playAddr != null) {
                    List<String> urlList = (List<String>) XposedHelpers.callMethod(playAddr, "getUrlList");
                    url = VideoUrlExtractor.bestUrl(urlList);
                    desc = (String) XposedHelpers.callMethod(aweme, "getDesc");
                }
            }
        } catch (Throwable t) {}

        if (url != null) {
            BHDownloadManager.downloadVideo(ctx, url, desc, desc);
            return;
        }

        // Try music
        try {
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
        } catch (Throwable t) {}

        Toast.makeText(ctx, "No download URL found", Toast.LENGTH_SHORT).show();
    }
}
