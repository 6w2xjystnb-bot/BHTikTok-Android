package com.bhtiktok;

import com.bhtiktok.hooks.AdsHook;
import com.bhtiktok.hooks.AnonymousSeenHook;
import com.bhtiktok.hooks.AutoPlayHook;
import com.bhtiktok.hooks.ConfirmHook;
import com.bhtiktok.hooks.DownloadHook;
import com.bhtiktok.hooks.FakeChangesHook;
import com.bhtiktok.hooks.ProgressBarHook;
import com.bhtiktok.hooks.RegionHook;
import com.bhtiktok.hooks.UIHook;
import com.bhtiktok.hooks.WatermarkHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BHTikTokModule implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"com.zhiliaoapp.musically".equals(lpparam.packageName)) return;
        XposedBridge.log("[BHTikTok] Module loading...");

        try { AdsHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] AdsHook failed: " + t.getMessage()); }

        try { WatermarkHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] WatermarkHook failed: " + t.getMessage()); }

        try { FakeChangesHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] FakeChangesHook failed: " + t.getMessage()); }

        try { DownloadHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] DownloadHook failed: " + t.getMessage()); }

        try { ConfirmHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] ConfirmHook failed: " + t.getMessage()); }

        try { AutoPlayHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] AutoPlayHook failed: " + t.getMessage()); }

        try { ProgressBarHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] ProgressBarHook failed: " + t.getMessage()); }

        try { RegionHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] RegionHook failed: " + t.getMessage()); }

        try { AnonymousSeenHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] AnonymousSeenHook failed: " + t.getMessage()); }

        try { UIHook.hook(lpparam); } catch (Throwable t) {
            XposedBridge.log("[BHTikTok] UIHook failed: " + t.getMessage()); }

        XposedBridge.log("[BHTikTok] All hooks initialized.");
    }
}
