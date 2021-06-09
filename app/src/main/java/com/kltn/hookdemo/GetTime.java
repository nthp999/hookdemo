package com.kltn.hookdemo;


import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetTime {
    public static String time() {
        String timeNow;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        Time hms = new Time();
        hms.setToNow();

        timeNow = simpleDateFormat.format(calendar.getTime()) + " " +
                    hms.hour + ":" + hms.minute + ":" + hms.second;
        return timeNow;
    } 
}

