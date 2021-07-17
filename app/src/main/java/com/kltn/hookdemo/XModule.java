package com.kltn.hookdemo;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.kltn.hookdemo.hooking.ActivityContext;
import com.kltn.hookdemo.hooking.Clipboard;
import com.kltn.hookdemo.hooking.LoadImgURL;
import com.kltn.hookdemo.hooking.LoginActivity;
import com.kltn.hookdemo.hooking.RegisterInfo;
import com.kltn.hookdemo.hooking.SendMsg;
import com.kltn.hookdemo.hooking.CallIntent;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/*
public class XModule implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private LoginActivity loginActivity = new LoginActivity();
    private RegisterInfo registerInfo = new RegisterInfo();
    private SendMsg sendMsg = new SendMsg();
    private LoadImgURL loadImgURL = new LoadImgURL();
    private CallIntent callIntent = new CallIntent();
    private Clipboard clipboard = new Clipboard();
    private ActivityContext activityContext = new ActivityContext();


    private static String [] blacklist = new String[100];
    public static final String PACKAGE_NAME = XModule.class.getPackage().getName();
    private static XSharedPreferences prefs;

    */
/*@Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if (Build.VERSION.SDK_INT != 30) {
            XposedBridge.log("!!! GravityBox you are running is not designed for "
                    + "Android SDK " + Build.VERSION.SDK_INT + " !!!");
            return;
        }

        Log.e("KLTN2021", "In zygote");
        if (FileUtils.shouldScan("com.kltn.hookdemo")) {
            blacklist =  FileUtils.saveStringToResultsFile("com.kltn.hookdemo");
            Log.e("KLTN2021", "In Zygote: Get in array: " + blacklist.get(0));
        }

        *//*
*/
/*try{
            String file="res/raw/test.txt";
            InputStream in=ActivityContext.getCurrentActivity().getClass().getClassLoader().getResourceAsStream(file);
            InputStreamReader isr= new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while((line=br.readLine()) != null )
            {
                Log.e("KLTN2021: ", line);
            }
        } catch (IOException e){
            Log.e("KLTN2021: ", e.getMessage());
        }

        if (XposedBridge.getXposedVersion() < 93) {
            prefs = new XSharedPreferences(prefsFile);
        } else {
            prefs = new XSharedPreferences("com.kltn.hookdemo","user_setting");
            prefs.makeWorldReadable();
            prefs.reload();
            String setting_value = prefs.getString("Clipboard Access", "");

            Log.e(TAG, "setting value: " + setting_value);

        }*//*
*/
/*
    }*//*


    public void handleLoadPackage (final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.e("KLTN2021", "In handle: Get in array");

        if (Build.VERSION.SDK_INT != 30) {
            return;
        }

        if (!lpparam.packageName.equals("com.example.demoappkltn")) {
            return;
        }


        if (FileUtils.shouldScan("com.kltn.hookdemo")) {
            blacklist =  FileUtils.saveStringToResultsFile("com.kltn.hookdemo");
            Log.e("KLTN2021", "Get String in class module: " + blacklist[2]);
            //loadImgURL.starthook(lpparam, blacklist);
        }

        Log.e("KLTN2021", blacklist[0]);

        loginActivity.starthook(lpparam);
        registerInfo.starthook(lpparam);
        sendMsg.starthook(lpparam, prefs);
        //loadImgURL.starthook(lpparam, blacklist);
        //callIntent.starthookCam_Vid(lpparam);
        clipboard.starthook(lpparam);
        activityContext.start(lpparam);

    }

}*/

public class XModule implements IXposedHookLoadPackage{
    public void handleLoadPackage (final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (Build.VERSION.SDK_INT != 30) {
            return;
        }

        if (FileUtils.shouldScan("com.kltn.hookdemo")) {
            String [] blacklist =  FileUtils.saveStringToResultsFile("com.kltn.hookdemo");
            Log.e("KLTN2021", "Get String in class module: " + blacklist[2]);

            if (lpparam.packageName.equals(LoadImgURL.PACKAGE_NAME)) {
                Log.e("KLTN2021_", "After return" + blacklist[2]);
                LoadImgURL.starthook(lpparam, blacklist);
            }


        }
    }
}
