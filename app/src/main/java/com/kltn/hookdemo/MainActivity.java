package com.kltn.hookdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver exampleBroadcastReceiver = new MyBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("KLTN2021_br", "onCreate");

        IntentFilter filter = new IntentFilter("com.kltn.CUSTOM_INTENT");
        registerReceiver(exampleBroadcastReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("KLTN2021_br", "onDestroy");
        if (exampleBroadcastReceiver != null)
            unregisterReceiver(exampleBroadcastReceiver);
    }
}