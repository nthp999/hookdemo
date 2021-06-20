package com.kltn.hookdemo.hooking;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ActivityContext {
    private static String TAG = "KLTN2021";
    private static Activity mCurrentActivity;

    Context context = (Context) AndroidAppHelper.currentApplication();

    public void start (XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> instrumentation = XposedHelpers.findClass(
                "android.app.Instrumentation", lpparam.classLoader);

        XposedBridge.hookAllMethods(instrumentation, "newActivity", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                mCurrentActivity = (Activity) param.getResult();
                //Log.d(TAG, "Current Activity : " + mCurrentActivity.getClass().getName());
                //Context ctx = (Context) mCurrentActivity;
            }
        });
    }

    public static Activity getCurrentActivity() {
        Log.d(TAG, "Current Activity : " + mCurrentActivity.getClass().getName());
        return mCurrentActivity;
    }

}
