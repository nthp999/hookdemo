package com.kltn.hookdemo.hooking;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;

import java.lang.reflect.Field;
import java.sql.Time;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;

public class Clipboard {

    private MyBroadcastSender mybrSender = new MyBroadcastSender();
    private static String TAG = "KLTN2021";

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            final Class<?> clazz = XposedHelpers.findClass(
                    "android.content.ClipboardManager", lpparam.classLoader);

            XposedHelpers.findAndHookMethod(
                    clazz, "getPrimaryClip", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                            Field contextf = XposedHelpers.findField(clazz,
                                    "mContext");

                            ClipData clipData = (ClipData) param.getResult();
                            Context c = (Context) contextf.get(param.thisObject);
                            Log.d (TAG, "CLIPBOARD: " + clipData.getItemAt(0).coerceToText(c));

                            Toast.makeText(c, "WARNING: Access Clipboard!", Toast.LENGTH_LONG)
                                    .show();

                            mybrSender.brSender(
                                    GetTime.time(),
                                    "getPrimaryClip",
                                    "Access Clipboard");
                        }
            });
        } catch (Exception e) {
            Log.e(TAG, "android.content.ClipboardManager: " + " ERROR: " + e);
        }
    }
}

