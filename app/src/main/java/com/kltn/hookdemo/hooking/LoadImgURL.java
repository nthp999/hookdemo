package com.kltn.hookdemo.hooking;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.kltn.hookdemo.FileUtils;
import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
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
    //private static final String PACKAGE_NAME = "com.kltn.hookdemo";
    private static final String TAG = "KLTN2021";
    private static boolean warning = true;
    private static int count = 0;
    private static String url = new String();

    public static void starthook(XC_LoadPackage.LoadPackageParam lpparam) throws NoSuchMethodException {

        final Class<?> clazz = XposedHelpers.findClass("java.net.URL", lpparam.classLoader);
        try {
            XposedHelpers.findAndHookMethod(clazz, "openStream", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    MyBroadcastSender.brSender(GetTime.time(), "java.net.URL",
                            "openStream", "Detect HTTP connection: java.net.URL");
                }
            });
        } catch (Exception e) {
            XposedBridge.log("java.net.URL: " + " ERROR: " + e.getMessage());
        }


        // ================= Hook HTTP Constructor ==================
        final Class httpUrlConnection = findClass("java.net.HttpURLConnection",lpparam.classLoader);
        hookAllConstructors(httpUrlConnection, new XC_MethodHook() {
            @Override
            //trước khi getoutputstream
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (param.args.length != 1 || param.args[0].getClass() != URL.class)
                    return;

                Log.e(TAG, "HttpURLConnection: " + param.args[0]);

                url = "" + param.args[0];
                // check blacklist
                if (FileUtils.shouldScan(lpparam.packageName)
                        && !FileUtils.saveStringToResultsFile(lpparam.packageName, url)) {
                    ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                    "[XPOSED] [WARNING]: HttpConnection", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }


                count++;
                if (count == 3 && warning == true) {
                    Log.w(TAG, "Count: " + count);
                    count = 0;
                    return;
                }

                // Alert if not allow
                Log.e(TAG, "Warning Status: " + warning);
                if (warning){
                    param.setResult(replaceHookedMethod(param));
                }

                warning = true;
            }

            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                MyBroadcastSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                        "HttpURLConnection", "HTTP connection: java.net.HttpURLConnection");

                MyBroadcastSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                        "HttpURLConnection", "HTTP connection: URL: " + param.args[0]);

                ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Show AlertDialog
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityContext.getCurrentActivity());
                        builder1.setTitle("Warning");
                        builder1.setMessage("Your link is in blacklist. Do you want to continue?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Allow",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                                "[XPOSED] [ALLOW] HTTPConnection", Toast.LENGTH_SHORT).show();
                                        MyBroadcastSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                                                "HttpURLConnection Constructor", "[ALLOW]");
                                        warning = false;
                                    }
                                });

                        builder1.setNegativeButton(
                                "Block",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                                "[XPOSED] [BLOCK]: HTTPConnection", Toast.LENGTH_SHORT).show();
                                        MyBroadcastSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                                                "HttpURLConnection Constructor", "[BLOCK]");
                                        warning = true;
                                        count = 0;
                                        param.setResult(null);
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });
                return true;
            }
        });


        /*// ============================================================================================
        findAndHookMethod("com.android.okhttp.internal.huc.HttpURLConnectionImpl", lpparam.classLoader, "getOutputStream", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.w(TAG, "INNNNNN");
                //lấy object HttpURLConnection để tìm địa chỉ đã gửi ra
                HttpURLConnection urlConn = (HttpURLConnection)param.thisObject;
                String url1 = "" + urlConn.getURL();
                Log.w(TAG, "HttpURLConnection: " + url1);
                if(urlConn!=null)
                {

                }
            }

            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return true;
            }
        });*/
    }
}