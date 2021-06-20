package com.kltn.hookdemo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyCustomListview extends ArrayAdapter<String> {
    private final Activity context;
    private String [] maintitle;
    private String subtitle;

    public MyCustomListview(Activity context, String[] maintitle, String subtitle) {
        super(context, R.layout.list_item_layout, maintitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.maintitle = maintitle;
        this.subtitle = subtitle;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item_layout, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.textView_main);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.textView_sub);

        titleText.setText(maintitle[position]);
        subtitleText.setText(subtitle);

        return rowView;

    };
}
