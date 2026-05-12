package com.bhtiktok.hooks;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bhtiktok.utils.BHDownloadManager;
import com.bhtiktok.utils.PrefsHelper;
import com.bhtiktok.utils.VideoUrlExtractor;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DownloadHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_DOWNLOAD_BUTTON)) return;

        // Hook VideoViewHolder.bind(Aweme) to inject download button
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.feed.adapter.VideoViewHolder",
                lpparam.classLoader,
                "bind",
                "com.ss.android.ugc.aweme.feed.model.Aweme",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object aweme = param.args[0];
                        if (aweme == null) return;

                        View itemView = (View) XposedHelpers.getObjectField(param.thisObject, "itemView");
                        if (itemView == null) return;

                        Context ctx = itemView.getContext();
                        ViewGroup container = findContainer(itemView);
                        if (container == null) return;

                        // Avoid duplicate buttons
                        String tag = "bhtiktok_download_btn";
                        if (container.findViewWithTag(tag) != null) return;

                        Button downloadBtn = new Button(ctx);
                        downloadBtn.setTag(tag);
                        downloadBtn.setText("⬇");
                        downloadBtn.setTextSize(12f);
                        downloadBtn.setBackgroundColor(Color.parseColor("#FE2C55"));
                        downloadBtn.setTextColor(Color.WHITE);

                        // Position: top-right corner
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        );
                        lp.gravity = Gravity.TOP | Gravity.END;
                        lp.setMargins(0, 20, 20, 0);

                        downloadBtn.setOnClickListener(v -> {
                            try {
                                handleDownload(ctx, aweme);
                            } catch (Exception ex) {
                                Toast.makeText(ctx, "Download error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        container.addView(downloadBtn, lp);
                    }
                }
            );
        } catch (Exception e) { }
    }

    private ViewGroup findContainer(View root) {
        if (root instanceof FrameLayout) return (FrameLayout) root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                if (child instanceof FrameLayout) return (FrameLayout) child;
            }
        }
        return root instanceof ViewGroup ? (ViewGroup) root : null;
    }

    private void handleDownload(Context ctx, Object aweme) throws Exception {
        // Get video
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

        // Try music
        Object music = (Object) XposedHelpers.callMethod(aweme, "getMusic");
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

        Toast.makeText(ctx, "No downloadable URL found", Toast.LENGTH_SHORT).show();
    }
}
