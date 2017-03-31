package com.edu.chenshaohong.attendance.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * Created by shaohong on 2017-3-21.
 */

public class MyViewPager extends ViewPager {
    private int pos = 0;
    private int maxPos = 0;

    /**
     * 设置viewpager最大项数
     */
    public void setMaxPage(int position)
    {
        maxPos = position;
    }

    /**
     * 设置viewpager滑动到的当前项
     */
    public void setCurrentPos(int position)
    {
        pos = position;
    }

    public MyViewPager(Context context) {
        super(context);
    }

    @Override
    protected void onPageScrolled(int arg0, float arg1, int arg2) {
        Log.e("suo", "arg0:" + arg0 +"|arg1:" + arg1 +"|arg2:" + arg2);
        if(pos == 0){
            if(arg2 == 0){
                Log.e("suo", "已经是第一页了");
            }
        }else if(pos == maxPos-1)
        {
            if(arg2 == 0){
                Log.e("suo", "已经是最后一页了");
            }
        }

        super.onPageScrolled(arg0, arg1, arg2);

    }
}
