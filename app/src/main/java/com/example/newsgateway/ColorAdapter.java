package com.example.newsgateway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ColorAdapter extends BaseAdapter {
    ArrayList<ContentDrawer> local_list;
    Context context;

    public ColorAdapter(Context context, ArrayList<ContentDrawer> list) {
        this.context = context;
        local_list = list;
    }

    @Override
    public int getCount() {
        return local_list.size();
    }

    @Override
    public Object getItem(int position) {
        return local_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = (LayoutInflater.from(context).inflate(R.layout.drawer_list, parent, false));
        }
        ContentDrawer drawerContent = local_list.get(position);
        TextView textView = view.findViewById(R.id.textview_draw);
        textView.setTextColor(drawerContent.getColor());
        textView.setText(drawerContent.getName());
        return view;
    }
}
