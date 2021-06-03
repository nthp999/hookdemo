package com.kltn.hookdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.kltn.hookdemo.hooking.LoginActivity;
import com.kltn.hookdemo.hooking.RegisterInfo;
import com.kltn.hookdemo.hooking.SendMsg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XModule implements IXposedHookLoadPackage {
    private LoginActivity loginActivity = new LoginActivity();
    private RegisterInfo registerInfo = new RegisterInfo();
    private SendMsg sendMsg = new SendMsg();

    public void handleLoadPackage (final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Load module successfull");

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO
                try {
                    slog.save("123");

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
        */


        if (!lpparam.packageName.equals("com.example.demoappkltn")) {
            return;
        }

        XposedBridge.log("Load /data/data/com.example.demoappkltn");

        loginActivity.starthook(lpparam);
        registerInfo.starthook(lpparam);
        sendMsg.starthook(lpparam);

    }
}