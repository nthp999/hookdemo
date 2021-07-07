package com.kltn.hookdemo.hooking;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class LoadImgURL {
    private MyBroadcastSender mybrSender = new MyBroadcastSender();
    private static String TAG = "KLTN2021";
    private static boolean SAFE = true;

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam) throws NoSuchMethodException {
        final Class<?> clazz = XposedHelpers.findClass(
                "java.net.URL", lpparam.classLoader);

        try {
            XposedHelpers.findAndHookMethod(
                    clazz,
                    "openStream",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            MyBroadcastSender.brSender(GetTime.time(), "java.net.URL",
                                    "openStream", "Detect HTTP connection: java.net.URL");
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "java.net.URL: " + " ERROR: " + e.getMessage());
        }

        // ================= Hook HTTP Request ==================
        final Class <?> httpUrlConnection = findClass("java.net.HttpURLConnection",lpparam.classLoader);

        //hook các constructor của class HttpURLConnection để lấy httpURLConnection
        hookAllConstructors(httpUrlConnection, new XC_MethodHook() {
            @Override
            //trước khi getoutputstream
            protected void beforeHookedMethod(MethodHookParam param) {
                // Return if not URL class
                if (param.args.length != 1 || param.args[0].getClass() != URL.class)
                    return;

                Log.d(TAG, "HttpURLConnection: " + param.args[0] + "");

                ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                "[XPOSED] [WARNING]: HttpConnection", Toast.LENGTH_SHORT).show();
                    }
                });

                param.setResult(null);

                mybrSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                        "HttpURLConnection", "HTTP connection: java.net.HttpURLConnection");

                mybrSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                        "HttpURLConnection", "HTTP connection: URL: " + param.args[0]);

            }
        });
    }
}