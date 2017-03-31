package com.edu.chenshaohong.attendance.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by shaohong on 2017-3-21.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> mList;

    public ViewPagerAdapter(List<View> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        //设置成最大，使用户看不到边界
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    //移除视图
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = mList.get(position);
        //Warning：不要在这里调用removeView
        container.removeView(view);
    }

    //添加视图
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException
        View view = mList.get(position);
        container.addView(view);
        return view;
    }
}
