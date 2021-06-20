package com.kltn.hookdemo;

import com.kltn.hookdemo.hooking.ActivityContext;
import com.kltn.hookdemo.hooking.Clipboard;
import com.kltn.hookdemo.hooking.LoadImgURL;
import com.kltn.hookdemo.hooking.LoginActivity;
import com.kltn.hookdemo.hooking.RegisterInfo;
import com.kltn.hookdemo.hooking.SendMsg;
import com.kltn.hookdemo.hooking.CallIntent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XModule implements IXposedHookLoadPackage {
    private LoginActivity loginActivity = new LoginActivity();
    private RegisterInfo registerInfo = new RegisterInfo();
    private SendMsg sendMsg = new SendMsg();
    private LoadImgURL loadImgURL = new LoadImgURL();
    private CallIntent callIntent = new CallIntent();
    private Clipboard clipboard = new Clipboard();
    private ActivityContext activityContext = new ActivityContext();

    public void handleLoadPackage (final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Load module successfull");

        if (!lpparam.packageName.equals("com.example.demoappkltn")) {
            return;
        }

        XposedBridge.log("Load /data/data/com.example.demoappkltn");

        loginActivity.starthook(lpparam);
        registerInfo.starthook(lpparam);
        sendMsg.starthook(lpparam);
        //loadImgURL.starthook(lpparam);
        //callIntent.starthookCam_Vid(lpparam);
        //clipboard.starthook(lpparam);
        //activityContext.start(lpparam);

    }
}