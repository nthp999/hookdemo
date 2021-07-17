package com.kltn.hookdemo.hooking;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.kltn.hookdemo.FileUtils;
import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;
import com.kltn.hookdemo.XModule;

import java.io.IOException;
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
    public static final String PACKAGE_NAME = "com.kltn.hookdemo";
    private MyBroadcastSender mybrSender = new MyBroadcastSender();
    private static String TAG = "KLTN2021";
    private static String url = new String();
    private static boolean warning = true;

    public static void starthook(final XC_LoadPackage.LoadPackageParam lpparam, final String[] blacklist) throws NoSuchMethodException {
        Log.e(TAG, "WARNING STRING: PHUC: " + blacklist.length + blacklist[1]);

        /*final Class<?> clazz = XposedHelpers.findClass("java.net.URL", lpparam.classLoader);

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
            Log.e(TAG, "java.net.URL: " + " ERROR: " + e.getMessage());
        }*/

        // ================= Hook HTTP Request ==================
        try {
            final Class<?> httpUrlConnection = findClass("java.net.HttpURLConnection", lpparam.classLoader);

            //hook các constructor của class HttpURLConnection để lấy httpURLConnection
            hookAllConstructors(httpUrlConnection, new XC_MethodHook() {
                @Override
                //trước khi getoutputstream
                protected void beforeHookedMethod(MethodHookParam param) {

                    // Return if not URL class
                    if (param.args.length != 1 || param.args[0].getClass() != URL.class)
                        return;
                    //url = (String) param.args[0];
                    Log.w(TAG, "param.arg: " + param.args[0]);
                    Log.w(TAG, "param.thisobj: " + param.thisObject);
                    Log.w(TAG, "param.result: " + param.getResult());

                    //Log.e(TAG, "BLACKLIST[0]: " + blacklist.size());

                    ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                    "[XPOSED] [WARNING]: HttpConnection", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Return if it not contains sensitive data
                    /*for (int i=0; i < blacklist.size(); i++) {
                        Log.e(TAG, "In for: " + param.args[0]);
                        if (param.args[0] == blacklist.get(i))
                        {
                            Log.e(TAG, "IN search ArrayList: Co thong tin nhay cam can xem xet");
                            return;
                        }
                    }
*/

                    Log.e(TAG, "Warning Status: " + warning);
                    if (warning){
                        try {
                            param.setResult(replaceHookedMethod(param));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    MyBroadcastSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                            "HttpURLConnection", "HTTP connection: java.net.HttpURLConnection");
                    MyBroadcastSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                            "HttpURLConnection", "HTTP connection: URL: " + param.args[0]);

                    warning = true;
                }

                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.e(TAG, "In Replace");

                            //Show AlertDialog
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityContext.getCurrentActivity());
                            builder1.setTitle("Warning");
                            builder1.setMessage("Your application making HTTP connection. Do you want to continue?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Allow",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                                    "[XPOSED] [ALLOW]: HttpURLConnection", Toast.LENGTH_SHORT).show();
                                            MyBroadcastSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                                                    "HttpURLConnection", "[ALLOW]");
                                            warning = false;
                                            //new openStreamAsync().execute(url);

                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Block",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                                    "[XPOSED] [BLOCK]: HttpURLConnection", Toast.LENGTH_SHORT).show();
                                            MyBroadcastSender.brSender(GetTime.time(), "java.net.HttpURLConnection",
                                                    "HttpURLConnection", "[BLOCK]");
                                            warning = true;
                                            Log.e(TAG, "[BLOCK] Warning Status: " + warning);
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
        } catch (Exception e){
            XposedBridge.log(e.getMessage());
        }
    }
    private class openStreamAsync extends AsyncTask<String, Void, Bitmap> {

        /* access modifiers changed from: protected */
        public Bitmap doInBackground(String... strings) {
            try {
                Log.w(TAG, "IN doInBackground");
                return BitmapFactory.decodeStream(new URL(strings[0]).openStream());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Bitmap bitmap) {
            //LoadImgActivity.this.ivResult.setImageBitmap(bitmap);
        }
    }
}