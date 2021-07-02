package com.kltn.hookdemo.hooking;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class CallIntent {
    private String TAG = "KLTN2021";

    public void starthookCam_Vid(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            final Class <?> intent = findClass("android.content.Intent",lpparam.classLoader);

            hookAllConstructors(intent, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (param.args.length != 1)
                        return;

                    if (param.args[0].equals("android.media.action.IMAGE_CAPTURE")) {
                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                "[XPOSED] [WARNING]: Open Camera (Image Capture)", Toast.LENGTH_SHORT).show();
                        MyBroadcastSender.brSender(GetTime.time(), "android.content.Intent",
                                "android.media.action.IMAGE_CAPTURE", "Open Camera (Image Capture)");
                        Log.d(TAG, "android.media.action.IMAGE_CAPTURE");
                    }

                    else if (param.args[0].equals("android.media.action.VIDEO_CAPTURE")) {
                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                "[XPOSED] [WARNING]: Open Camera (Video Capture)", Toast.LENGTH_SHORT).show();
                        MyBroadcastSender.brSender(GetTime.time(), "android.content.Intent",
                                "android.media.action.VIDEO_CAPTURE", "Open Camera (Video Capture)");
                        Log.d(TAG, "android.media.action.VIDEO_CAPTURE");
                    }

                    else if (param.args[0].equals("android.intent.action.SET_ALARM")) {
                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                "[XPOSED] [WARNING]: Set Alarm", Toast.LENGTH_SHORT).show();
                        MyBroadcastSender.brSender(GetTime.time(), "android.content.Intent",
                                "android.intent.action.SET_ALARM" , "Set Alarm");
                        Log.d(TAG, "android.intent.action.SET_ALARM");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "android.content.Intent: " + " ERROR: " + e);
        }
    }
}
