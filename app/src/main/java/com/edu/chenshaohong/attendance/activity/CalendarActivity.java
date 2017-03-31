package com.edu.chenshaohong.attendance.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.chenshaohong.attendance.R;
import com.edu.chenshaohong.attendance.entity.DayRecordEntity;
import com.edu.chenshaohong.attendance.entity.MonthEntity;
import com.edu.chenshaohong.attendance.entity.MonthRecordEntity;
import com.edu.chenshaohong.attendance.entity.TimeStampEntity;
import com.edu.chenshaohong.attendance.nextwork.AttendanceApi;
import com.edu.chenshaohong.attendance.utils.MyView;
import com.google.gson.Gson;
import com.joybar.librarycalendar.data.CalendarDate;
import com.joybar.librarycalendar.fragment.CalendarViewFragment;
import com.joybar.librarycalendar.fragment.CalendarViewPagerFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shaohong on 2017-3-23.
 */

public class CalendarActivity extends FragmentActivity implements
        CalendarViewPagerFragment.OnPageChangeListener
        ,
        CalendarViewFragment.OnDateClickListener,
        CalendarViewFragment.OnDateCancelListener {

    @BindView(R.id.normal_day)
    TextView mNormalDay;
    @BindView(R.id.late_day)
    TextView mLateDay;
    @BindView(R.id.absenteeism_day)
    TextView mAbsenteeismDay;
    @BindView(R.id.data_yes)
    LinearLayout mDataYes;
    @BindView(R.id.data_no)
    LinearLayout mDataNo;
    @BindView(R.id.username)
    TextView mUsername;
    @BindView(R.id.myView)
    MyView mMyView1;
    @BindView(R.id.fl_daydata)
    FrameLayout mFlDaydata;
    @BindView(R.id.ll_no_data)
    LinearLayout mLlNoData;

    private TextView tv_date;
    private boolean isChoiceModelSingle = true;
    private List<CalendarDate> mListDate = new ArrayList<>();
    private Unbinder mBind;
    private AttendanceApi.Api mApi;
    private int mYear;
    private int mMonth;
    private String mUserId;
    private String mMachineId;
    private String mUserName;
    private MyView mMyView;
    private List<String> entryTimeList;
    private List<String> leaveTimeList;
    private int num = 0;

    private static String listToString(List<CalendarDate> list) {
        StringBuffer stringBuffer = new StringBuffer();
        for (CalendarDate date : list) {
            stringBuffer.append(date.getSolar().solarYear + "-" + date.getSolar().solarMonth
                    + "-" + date.getSolar().solarDay).append(" ");
        }
        return stringBuffer.toString();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mBind = ButterKnife.bind(this);
        mApi = AttendanceApi.createApi();
        tv_date = (TextView) findViewById(R.id.tv_date);
        //获取某天记录
//        getDayRecord();
        initFragment();
        mMyView = new MyView(this);
    }

    private void initFragment() {

        Intent intent = getIntent();
        mUserId = intent.getStringExtra("userId");
        mMachineId = intent.getStringExtra("machineId");
        mUserName = intent.getStringExtra("userName");
        mUsername.setText(mUserName);
        //获取当月记录
        getMonthRecord();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        Fragment fragment = CalendarViewPagerFragment.newInstance(isChoiceModelSingle);

        tx.replace(R.id.fl_content, fragment);
        tx.commit();
    }

    private void getDayRecord() {
        mApi.getDayRecord(mUserId, mMachineId, tv_date.getText().toString()).enqueue(
                new Callback<DayRecordEntity>() {
                    @Override
                    public void onResponse(Call<DayRecordEntity> call, Response<DayRecordEntity> response) {
                        DayRecordEntity body = response.body();

                        if (body.getStatus().equals("1")) {
                            mFlDaydata.setVisibility(View.VISIBLE);
                            mLlNoData.setVisibility(View.GONE);
                            mMyView.setAngle();
                            List<TimeStampEntity> mData = body.getData();
                            mMyView1.setAngle();
                            SharedPreferences sf = getSharedPreferences("TimeStampList", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sf.edit();
                            Gson gson = new Gson();
                            editor.remove("timeList");
                            editor.putString("timeList", gson.toJson(mData));
                            editor.commit();
                        } else {
                            mFlDaydata.setVisibility(View.GONE);
                            mLlNoData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<DayRecordEntity> call, Throwable t) {

                        Toast.makeText(CalendarActivity.this, "访问网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getMonthRecord() {
        Calendar calendar = Calendar.getInstance();
        if (mYear == 0) {
            mYear = calendar.get(Calendar.YEAR);
        }
        if (mMonth == 0) {
            mMonth = calendar.get(Calendar.MONTH) + 1;
        }

        mApi.getMonthRecord(mUserId, mMachineId, mYear + "-" + mMonth).enqueue(new Callback<MonthRecordEntity>() {
            @Override
            public void onResponse(Call<MonthRecordEntity> call, Response<MonthRecordEntity> response) {
                List<MonthEntity> data = response.body().getData();
                if (response.body().getStatus().equals("1")) {
                    int normalDay = 0;
                    int lateDay = 0;
                    int absenteeismDay = 0;
                    SharedPreferences sf = getSharedPreferences("thisMonthData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    Gson gson = new Gson();
                    editor.remove("monthdata");

                    if (data == null) {
                        mDataNo.setVisibility(View.VISIBLE);
                        mNormalDay.setText(normalDay + " 天");
                        mLateDay.setText(lateDay + " 天");
                        mAbsenteeismDay.setText(absenteeismDay + " 天");
                        editor.commit();
                        return;
                    } else {
                        mDataNo.setVisibility(View.GONE);
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).getState().equals("0")) {
                                normalDay++;
                            } else if (data.get(i).getState().equals("1")) {
                                lateDay++;
                            } else if (data.get(i).getState().equals("2")) {
                                absenteeismDay++;
                            }
                        }
                        mNormalDay.setText(normalDay + " 天");
                        mLateDay.setText(lateDay + " 天");
                        mAbsenteeismDay.setText(absenteeismDay + " 天");

                        editor.putString("monthdata", gson.toJson(data));
                        editor.commit();
                    }

                }
            }

            @Override
            public void onFailure(Call<MonthRecordEntity> call, Throwable t) {
//                getMonthRecord();
                Log.e("CalendarActivityFailure", t.getMessage());
                Toast.makeText(CalendarActivity.this, "访问网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDateClick(CalendarDate calendarDate) {

        int year = calendarDate.getSolar().solarYear;
        int month = calendarDate.getSolar().solarMonth;
        int day = calendarDate.getSolar().solarDay;
        if (isChoiceModelSingle) {

//            if (num < 1) {
            if (month < 10 && day < 10) {
                tv_date.setText(year + "-" + "0" + month
                        + "-" + "0" + day
                );
            } else if (month < 10) {
                tv_date.setText(year + "-" + "0" + month
                        + "-" + day
                );
            } else if (day < 10) {
                tv_date.setText(year + "-" + month
                        + "-" + "0" + day
                );
            } else {
                tv_date.setText(year + "-" + month
                        + "-" + day
                );
            }
//            }
            getDayRecord();
//            num ++;

        } else {
            //System.out.println(calendarDate.getSolar().solarDay);
//            mListDate.add(calendarDate);
//            tv_date.setText(listToString(mListDate));
        }

    }

    @Override
    public void onDateCancel(CalendarDate calendarDate) {
        int count = mListDate.size();
        for (int i = 0; i < count; i++) {
            CalendarDate date = mListDate.get(i);
            if (date.getSolar().solarDay == calendarDate.getSolar().solarDay) {
                mListDate.remove(i);
                break;
            }
        }
        tv_date.setText(listToString(mListDate));
    }

    @Override
    public void onPageChange(int year, int month) {
        if (month < 10) {
            tv_date.setText(year + "-" + "0" + month);
            mYear = year;
            mMonth = month;
            getMonthRecord();
        } else {
            tv_date.setText(year + "-" + month);
            mYear = year;
            mMonth = month;
            getMonthRecord();
        }

        mListDate.clear();
    }

    @OnClick(R.id.back)
    public void setback() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }
}
