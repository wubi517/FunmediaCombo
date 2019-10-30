package com.gold.kds517.funmedia_new.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gold.kds517.funmedia_new.models.CategoryModel;
import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.apps.Constants;
import com.gold.kds517.funmedia_new.apps.MyApp;
import com.gold.kds517.funmedia_new.utils.Utils;

import java.util.List;

public class CategoryListAdapter extends BaseAdapter {
    Context context;
    private List<CategoryModel> datas;
    private LayoutInflater inflater;
    private int selected_pos;
    private TextView title;
    private LinearLayout main_lay;
    public CategoryListAdapter(Context context, List<CategoryModel> datas) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.datas = datas;
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category_list, parent, false);
        }
        main_lay = (LinearLayout)convertView.findViewById(R.id.main_lay);
        title = (TextView)convertView.findViewById(R.id.categry_list_txt);
        title.setText(datas.get(position).getName());
        if (selected_pos == position) {
            title.setTextColor(Color.BLACK);
            main_lay.setBackgroundResource(R.drawable.main_list_bg);
        } else {
            title.setTextColor(Color.WHITE);
            main_lay.setBackgroundResource(R.color.trans_parent);
        }
        if(MyApp.instance.getPreference().get(Constants.IS_PHONE)!=null){
            main_lay.setPadding(Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5));
        }else {
            main_lay.setPadding(Utils.dp2px(context, 10), Utils.dp2px(context, 10), Utils.dp2px(context, 10), Utils.dp2px(context, 10));
        }
        return convertView;
    }
    public void selectItem(int pos) {
        selected_pos = pos;
        notifyDataSetChanged();
    }
}
