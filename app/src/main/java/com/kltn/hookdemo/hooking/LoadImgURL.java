package com.kltn.hookdemo.hooking;

import android.util.Log;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;

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

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam) throws NoSuchMethodException {
        try {
            XposedHelpers.findAndHookMethod(
                    "java.net.URL",
                    lpparam.classLoader,
                    "openStream",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            Log.d("KLTN2021", "java.net.URL: Detect HTTP connection");
                            ArrayList<String> loginfo = new ArrayList<String>();
                            loginfo.add(GetTime.time());
                            loginfo.add("openStream");
                            loginfo.add("Detect HTTP connection: java.net.URL");
                            mybrSender.brSender(loginfo);
                        }
                    });
        } catch (Exception e) {
            Log.e("KLTN2021", "java.net.URL: " + " ERROR: " + e.getMessage());
        }

        // ================= Hook HTTP Request ==================
        final Class <?> httpUrlConnection = findClass("java.net.HttpURLConnection",lpparam.classLoader);

        //hook các constructor của class HttpURLConnection để lấy httpURLConnection
        hookAllConstructors(httpUrlConnection, new XC_MethodHook() {
            @Override //trước khi getoutputstream
            protected void beforeHookedMethod(MethodHookParam param) {
                //nếu không dúng class và len=1 thì thoát
                if (param.args.length != 1 || param.args[0].getClass() != URL.class)
                    return;
                //log lại url mà app gửi ra ngoài
                Log.d("KLTN2021 ", "HttpURLConnection: " + param.args[0] + "");

                ArrayList<String> loginfo = new ArrayList<String>();
                loginfo.add(GetTime.time());
                loginfo.add("HttpURLConnection");
                loginfo.add("Detect HTTP connection: java.net.HttpURLConnection");
                mybrSender.brSender(loginfo);

                loginfo.clear();
                loginfo.add(GetTime.time());
                loginfo.add("HttpURLConnection");
                loginfo.add("Detect HTTP connection: URL: " + param.args[0]);
                mybrSender.brSender(loginfo);

            }
        });
    }
}
