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
 * Created by shaohong on 2017-3-24.
 */

public class SetUserIconAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;

    public SetUserIconAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 3;
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
        view = inflater.inflate(R.layout.list_item, viewGroup,false);
        TextView textView = (TextView) view.findViewById(R.id.tv_text_item);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_item);
        imageView.setVisibility(View.GONE);
        if (i == 0) {
            textView.setText("从相册中选择");
        } else if (i == 1) {
            textView.setText("打开相机拍照");
        }else {
            textView.setText("取消");
        }

        return view;
    }
}
