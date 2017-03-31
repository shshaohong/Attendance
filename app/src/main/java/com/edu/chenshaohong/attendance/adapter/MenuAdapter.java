package com.edu.chenshaohong.attendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.chenshaohong.attendance.R;

/**
 * Created by shaohong on 2017-3-20.
 */

public class MenuAdapter extends BaseAdapter {

    private Context mContext;

    public MenuAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.list_item, viewGroup, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_item);
        TextView textView = (TextView) view.findViewById(R.id.tv_text_item);
        if (i == 0) {
            imageView.setImageResource(R.mipmap.personal_information);
            textView.setText("修改个人信息");
        }else {
            imageView.setImageResource(R.mipmap.map);
            textView.setText("查看考勤地点");
        }

        return view;
    }
}
