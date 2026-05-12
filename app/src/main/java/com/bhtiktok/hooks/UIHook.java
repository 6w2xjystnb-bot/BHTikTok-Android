package com.bhtiktok.hooks;

import android.view.View;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class UIHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        boolean hideEmoji = PrefsHelper.isEnabled(PrefsHelper.FEATURE_HIDE_EMOJI);
        boolean hideTop = PrefsHelper.isEnabled(PrefsHelper.FEATURE_HIDE_TOP_ITEMS);
        boolean disableLive = PrefsHelper.isEnabled(PrefsHelper.FEATURE_DISABLE_LIVE);
        boolean disableSensitive = PrefsHelper.isEnabled(PrefsHelper.FEATURE_DISABLE_SENSITIVE);

        // Hide Emoji bar
        if (hideEmoji) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.comment.ui.CommentInputFragment",
                    lpparam.classLoader,
                    "onViewCreated",
                    android.view.View.class,
                    android.os.Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Object emojiView = XposedHelpers.getObjectField(param.thisObject, "emojiBar");
                                if (emojiView instanceof View) {
                                    ((View) emojiView).setVisibility(View.GONE);
                                }
                            } catch (Throwable t) { }
                        }
                    }
                );
            } catch (Throwable t) { }
        }

        // Hide top items (status bar overlay in feed)
        if (hideTop) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.feed.ui.FeedFragment",
                    lpparam.classLoader,
                    "onResume",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                Object topView = XposedHelpers.getObjectField(param.thisObject, "mTopView");
                                if (topView instanceof View) {
                                    ((View) topView).setVisibility(View.GONE);
                                }
                            } catch (Throwable t) { }
                        }
                    }
                );
            } catch (Throwable t) { }
        }

        // Disable Live
        if (disableLive) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.live.LiveFeedEntranceView",
                    lpparam.classLoader,
                    "setVisibility",
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.args[0] = View.GONE;
                        }
                    }
                );
            } catch (Throwable t) { }
        }

        // Disable sensitive content warnings
        if (disableSensitive) {
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.feed.model.Aweme",
                    lpparam.classLoader,
                    "isSensitive",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(false);
                        }
                    }
                );
            } catch (Throwable t) { }
            try {
                XposedHelpers.findAndHookMethod(
                    "com.ss.android.ugc.aweme.feed.model.Aweme",
                    lpparam.classLoader,
                    "isWarning",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(false);
                        }
                    }
                );
            } catch (Throwable t) { }
        }
    }
}
