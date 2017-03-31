package com.edu.chenshaohong.attendance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.edu.chenshaohong.attendance.entity.TimeStampEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by shaohong on 2017-3-27.
 */

public class MyView extends View {

    private Paint mPaint;
    private int width, height;
    private SimpleDateFormat sdf;
    private List<TimeStampEntity> mList;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context) {
        super(context);
        sdf = new SimpleDateFormat("HH:mm");
    }

    //组件宽高度
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("onSizeChanged", "width :" + w + " == " + "height :" + h);
        width = w;
        height = h;
    }

    public void setAngle() {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Log.e("mywidth", "width :" + width + " == " + "height :" + height);
        mPaint = new Paint();
        mPaint.setTextSize(35);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(0xffff0000);
        SharedPreferences sf = getContext().getSharedPreferences("TimeStampList", Context.BIND_ABOVE_CLIENT);
        String timeList = sf.getString("timeList", "[]");
        Gson gson = new Gson();
        mList = gson.fromJson(timeList, new TypeToken<List<TimeStampEntity>>() {
        }.getType());

        if (mList == null || mList.equals("[]") || mList.size() == 0) {
            postInvalidate();
            return;
        }
        long entrytime, lavetime;
        for (int i = 0; i < mList.size(); i++) {
            if (sdf == null) {
                sdf = new SimpleDateFormat("HH:mm");
            }
            entrytime = Long.parseLong(mList.get(i).getEntrytime());
            lavetime = Long.parseLong(mList.get(i).getLeavetime());

            Date date = new Date(entrytime * 1000L);
            Date date2 = new Date(lavetime * 1000L);

            String format = sdf.format(date);
            String format2 = sdf.format(date2);
                //画线
                //上
                canvas.drawLine((width / (mList.size() * 2)) * i * 2 + 40, 28,

                        (width / (mList.size() * 2)) * i * 2 + 40, height / 2, mPaint);
                //下
                if (!format2.equals("")) {
                    canvas.drawLine(width / (mList.size() * 2) * i * 2 + 40 +
                                    width / (mList.size() * 2 * 2), height / 2,

                            width / (mList.size() * 2) * i * 2 + 40 +
                                    width / (mList.size() * 2 * 2), height - 28, mPaint);
                }

                //画文本
                //上
                canvas.drawText(format, (width / (mList.size() * 2)) * i * 2, 25, mPaint);
                //下
                canvas.drawText(format2, (width / (mList.size() * 2)) * i * 2 +
                        width / (mList.size() * 2*2), height, mPaint);
        }


    }
}
