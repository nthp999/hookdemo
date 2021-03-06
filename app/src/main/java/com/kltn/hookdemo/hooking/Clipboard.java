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
    private static final String TAG = "KLTN2021";

    public static void starthook(XC_LoadPackage.LoadPackageParam lpparam) {
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
                            String DATA = clipData.getItemAt(0).coerceToText(c).toString();

                            Log.d (TAG, "CLIPBOARD: " + clipData.getItemAt(0).coerceToText(c));

                            Toast.makeText(c, "[XPOSED] [WARNING]: Access Clipboard!", Toast.LENGTH_SHORT).show();

                            MyBroadcastSender.brSender(GetTime.time(), "android.content.ClipboardManager",
                                    "getPrimaryClip", "CLIPBOARD: " + DATA);
                        }
            });
        } catch (Error e) {
            Log.e(TAG, "ERROR: " + e.getMessage());
        }
    }
}

