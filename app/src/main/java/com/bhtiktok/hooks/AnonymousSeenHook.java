package com.bhtiktok.hooks;

import com.bhtiktok.utils.PrefsHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AnonymousSeenHook {

    public void init(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PrefsHelper.isEnabled(PrefsHelper.FEATURE_ANONYMOUS_SEEN)) return;

        // Hook markMessageAsRead in chat controller
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.im.sdk.chat.controller.ChatRoomController",
                lpparam.classLoader,
                "markMessageAsRead",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                }
            );
        } catch (Throwable t) { }

        // Alternative: hook MessageReadManager
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.im.sdk.chat.data.manager.MessageReadManager",
                lpparam.classLoader,
                "sendReadReceipt",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                }
            );
        } catch (Throwable t) { }

        // Alternative: hook setMessageReadStatus
        try {
            XposedHelpers.findAndHookMethod(
                "com.ss.android.ugc.aweme.im.sdk.chat.data.manager.MessageReadManager",
                lpparam.classLoader,
                "setMessageReadStatus",
                String.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                }
            );
        } catch (Throwable t) { }
    }
}
