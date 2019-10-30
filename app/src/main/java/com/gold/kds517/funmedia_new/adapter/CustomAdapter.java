package com.gold.kds517.funmedia_new.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.gold.kds517.funmedia_new.R;

import java.util.List;

public class CustomAdapter extends ArrayAdapter {
    Context context;
    List<String> maindatas;
    LayoutInflater inflter;
    int selected_pos;

    public CustomAdapter(@NonNull Context context, int resource, List<String> maindatas) {
        super(context,resource,maindatas);
        this.maindatas = maindatas;
        this.context = context;
        this.inflter =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }
    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.item_spinner, parent, false);
        }
        TextView sub = convertView.findViewById(R.id.spinner_list);
        sub.setText(maindatas.get(position));
        return convertView;
    }


}
