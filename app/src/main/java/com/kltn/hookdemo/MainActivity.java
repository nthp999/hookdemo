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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kltn.hookdemo.hooking.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnShowlog;
    Button btnSaveConfig;
    ListView listView;

    String TAG = "KLTN2021";
    BroadcastReceiver exampleBroadcastReceiver;
    DatabaseSupport db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        btnShowlog = (Button) findViewById(R.id.btn_showlog);
        btnSaveConfig = (Button) findViewById(R.id.btn_save);
        listView = (ListView) findViewById(R.id.lv_singleops);

        // ==================== REGISTER BROADCAST RECEIVER =====================
        // Create br_recv object
        // and register to BroadcastReceiver
        exampleBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.kltn.CUSTOM_INTENT");
        registerReceiver(exampleBroadcastReceiver, filter);

        // ===================== CONFIG LISTVIEW =============================
        // Add item to Listview
        ArrayList<String> lv_item = new ArrayList<>();

        lv_item.add("SMS");
        lv_item.add("HTTP Connection");
        lv_item.add("Clipboard Access");
        lv_item.add("Intent Calling");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,lv_item);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "CLicked" + lv_item.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        // ============================ BUTTON ==============================
        // Button Show Log: Call ShowLog.class
        btnShowlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowLogActivity.class);
                startActivity(intent);
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

        // Button Save config: Save the selection of user
        btnSaveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (exampleBroadcastReceiver != null)
            unregisterReceiver(exampleBroadcastReceiver);
    }

}