package com.kltn.hookdemo.hooking;

import android.content.Intent;
import android.util.Log;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class TakePhoto_Video {
    private MyBroadcastSender mybrSender = new MyBroadcastSender();
    private static String TAG = "KLTN2021";


    public void starthook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            final Class <?> intent = findClass("android.content.Intent",lpparam.classLoader);

            hookAllConstructors(intent, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (param.args.length != 1)
                        return;
                    if (param.args[0].equals("android.media.action.IMAGE_CAPTURE"))
                        Log.i(TAG, "android.media.action.IMAGE_CAPTURE");
                    else if (param.args[0].equals("android.media.action.VIDEO_CAPTURE"))
                        Log.i(TAG, "android.media.action.VIDEO_CAPTURE");
                    else if (param.args[0].equals("android.intent.action.SET_ALARM"))
                        Log.i(TAG, "android.intent.action.SET_ALARM");
                    //else if
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "android.content.Intent: " + " ERROR: " + e.getMessage());
        }
    }
}
