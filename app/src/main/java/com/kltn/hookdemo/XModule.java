package com.kltn.hookdemo;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.kltn.hookdemo.hooking.ActivityContext;
import com.kltn.hookdemo.hooking.Clipboard;
import com.kltn.hookdemo.hooking.LoadImgURL;
import com.kltn.hookdemo.hooking.LoginActivity;
import com.kltn.hookdemo.hooking.RegisterInfo;
import com.kltn.hookdemo.hooking.SendMsg;
import com.kltn.hookdemo.hooking.CallIntent;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XModule implements /*IXposedHookZygoteInit,*/ IXposedHookLoadPackage {
    private static final String TAG = "KLTN2021";
    private LoginActivity loginActivity = new LoginActivity();
    private RegisterInfo registerInfo = new RegisterInfo();
    private SendMsg sendMsg = new SendMsg();
    private LoadImgURL loadImgURL = new LoadImgURL();
    private CallIntent callIntent = new CallIntent();
    private Clipboard clipboard = new Clipboard();
    private ActivityContext activityContext = new ActivityContext();

    public static final String PACKAGE_NAME = XModule.class.getPackage().getName();
    private static XSharedPreferences prefs;

    // Use for Xposed version < 93
    private static final File prefsFile = new File("/data/data/com.kltn.hookdemo/shared_prefs/user_setting.xml");

    public void handleLoadPackage (final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (Build.VERSION.SDK_INT != 30) {
            return;
        }

        if (!lpparam.packageName.equals("com.example.demoappkltn")) {
            return;
        }


        loginActivity.starthook(lpparam);
        registerInfo.starthook(lpparam);
        sendMsg.starthook(lpparam, prefs);
        loadImgURL.starthook(lpparam);
        callIntent.starthookCam_Vid(lpparam);
        clipboard.starthook(lpparam);
        activityContext.start(lpparam);

    }

   /* @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if (Build.VERSION.SDK_INT != 30) {
            XposedBridge.log("!!! GravityBox you are running is not designed for "
                    + "Android SDK " + Build.VERSION.SDK_INT + " !!!");
            return;
        }

        if (XposedBridge.getXposedVersion() < 93) {
            prefs = new XSharedPreferences(prefsFile);
        } else {
            prefs = new XSharedPreferences("com.kltn.hookdemo","user_setting");
            prefs.makeWorldReadable();
            prefs.reload();
            String setting_value = prefs.getString("Clipboard Access", "");

            Log.e(TAG, "setting value: " + setting_value);

        }
    }*/
}