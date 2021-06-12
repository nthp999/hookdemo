package com.kltn.hookdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kltn.hookdemo.hooking.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnShowlog;
    TextView tvLog;

    BroadcastReceiver exampleBroadcastReceiver;
    DatabaseSupport db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("KLTN2021_br", "onCreate");

        btnShowlog = (Button) findViewById(R.id.btn_showlog);
        tvLog = (TextView) findViewById(R.id.tv_log);
        tvLog.setMovementMethod(new ScrollingMovementMethod());

        exampleBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.kltn.CUSTOM_INTENT");
        registerReceiver(exampleBroadcastReceiver, filter);

        btnShowlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLog.setText(null);
                db = new DatabaseSupport(MainActivity.this);
                ArrayList<String> list = db.getLog();
                for (int i = 0; i < list.size(); i++)
                    tvLog.append(list.get(i));

                //Log.e("KLTN2021", "DEMO: " + list.toString());
/*
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("WARNING");
                builder1.setMessage("Your SMS contains your password value. Do you want to send SMS continuously?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                tvLog.setText(null);
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();*/

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("KLTN2021_br", "onDestroy");
        if (exampleBroadcastReceiver != null)
            unregisterReceiver(exampleBroadcastReceiver);
    }

}