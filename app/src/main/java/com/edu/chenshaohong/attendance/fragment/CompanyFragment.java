package com.edu.chenshaohong.attendance.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.edu.chenshaohong.attendance.R;
import com.edu.chenshaohong.attendance.activity.CalendarActivity;
import com.edu.chenshaohong.attendance.activity.MapViewActivity;
import com.edu.chenshaohong.attendance.adapter.MenuAdapter;
import com.edu.chenshaohong.attendance.adapter.ViewPagerAdapter;
import com.edu.chenshaohong.attendance.entity.MessageEntity;
import com.edu.chenshaohong.attendance.entity.UserDataEntity;
import com.edu.chenshaohong.attendance.nextwork.AttendanceApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by shaohong on 2017-3-20.
 * 团队列表界面
 */

public class CompanyFragment extends DialogFragment {

    public static final String USER_DATA = "CompanyFragment";
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.department)
    TextView mDepartment;
    @BindView(R.id.phoneNumber)
    TextView mPhoneNumber;
    @BindView(R.id.mail)
    TextView mMail;
    @BindView(R.id.addshow)
    TextView mAddshow;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private View mView;
    private PopupWindow mPopupWindow;
    private MenuAdapter mMenuAdapter;
    private ViewPagerAdapter mPagerAdapter;
    private Unbinder mBind;
    private List<View> mViewList;
    private int mListnumber;
    private AttendanceApi.Api mApi;
    private List<MessageEntity> mList;
    private List<UserDataEntity> mList1;
    private ImageView mUserIcon;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String department = intent.getStringExtra("department");
            String mail = intent.getStringExtra("mail");
            String username = intent.getStringExtra("username");
            if (mName.getText().toString().equals(username)) {
                mDepartment.setText(department);
                mMail.setText(mail);
            }
//            File dir = new File(Environment.getExternalStorageDirectory()
//                    + "/head_back/");
//            if (dir.exists()) {
//                String f = dir.getPath() + PersonalFragment.SAVE_AVATORNAME;
//                File file = new File(f);
//                if (file.exists()) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(f);
//                    mUserIcon.setImageBitmap(bitmap);
//                    mPagerAdapter.notifyDataSetChanged();
//                }
//            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_company, container, false);
        }
        mBind = ButterKnife.bind(this, mView);
        IntentFilter intentFilter = new IntentFilter(USER_DATA);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, intentFilter);
        init();
        return mView;
    }

    private void init() {
        getUserMassage();
        SharedPreferences sf = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        mListnumber = sf.getInt("listnumber", 0);
        String usermessage = sf.getString("usermessage", "");
        Gson gson = new Gson();
        mList1 = gson.fromJson(usermessage, new TypeToken<List<UserDataEntity>>() {
        }.getType());

        mViewList = new ArrayList<>();

        for (int i = 0; i < mList1.size(); i++) {

            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.pager_view, null);
            mUserIcon = (ImageView) view.findViewById(R.id.userImageView);
            TextView userName = (TextView) view.findViewById(R.id.userName);
            Log.e("headurl", mList1.get(i).getHeadurl() + "----" + i);

            Picasso.with(getContext()).load("http://121.40.70.170" + mList1.get(i).getHeadurl())
                    .placeholder(R.mipmap.userportrait)
                    .into(mUserIcon);
            userName.setText(mList1.get(i).getUsername());
            mViewList.add(view);
        }
        mPagerAdapter = new ViewPagerAdapter(mViewList);
        mViewPager.setCurrentItem(mViewList.size());
        mViewPager.setOffscreenPageLimit(2);

        String username = mList1.get(0).getUsername();
        String phonenumber = mList1.get(0).getPhonenumber();
        String email = mList1.get(0).getEmail();
        String department = mList1.get(0).getDepartment();
        mName.setText(username);
        mPhoneNumber.setText(phonenumber);
        mDepartment.setText(department);
        mMail.setText(email);

        mViewPager.setAdapter(mPagerAdapter);

        mViewList.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalendarActivity.class);
                intent.putExtra("userId", mList1.get(0).getId());
                intent.putExtra("userName", mList1.get(0).getUsername());
                intent.putExtra("machineId", mList.get(mListnumber).getMachineId());
                startActivity(intent);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 日历考勤情况
                getCalendar(position);
                //设置成员详细资料
                String username = mList1.get(position).getUsername();
                String phonenumber = mList1.get(position).getPhonenumber();
                String email = mList1.get(position).getEmail();
                String department = mList1.get(position).getDepartment();
                mName.setText(username);
                mPhoneNumber.setText(phonenumber);
                mDepartment.setText(department);
                mMail.setText(email);
                Log.e("position", position + "");

                SharedPreferences sf = getActivity().getSharedPreferences("num", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.remove("position");
                editor.putInt("position", position);
                editor.commit();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void getCalendar(final int position) {
        mViewList.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CalendarActivity.class);
                intent.putExtra("userId", mList1.get(position).getId());
                intent.putExtra("userName", mList1.get(position).getUsername());
                intent.putExtra("machineId", mList.get(mListnumber).getMachineId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void getUserMassage() {
        SharedPreferences sf = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        mList = gson.fromJson(sf.getString("messagelist", "[]"),
                new TypeToken<List<MessageEntity>>() {
                }.getType());
    }


    @OnClick(R.id.back)
    public void setback() {
        getDialog().cancel();
    }

    @OnClick(R.id.addshow)
    public void setshowPopupWindow() {
        showPopupWindow();
    }

    //显示弹框
    private void showPopupWindow() {

        mPopupWindow = new PopupWindow(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.listview, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        mMenuAdapter = new MenuAdapter(getContext());
        listView.setAdapter(mMenuAdapter);
        mPopupWindow.setHeight(300);
        mPopupWindow.setWidth(430);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(view);
        mPopupWindow.showAsDropDown(mAddshow, 0, 35);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    PersonalFragment fragment = new PersonalFragment();

                    fragment.show(getFragmentManager(), "PersonalFragment");
                } else {
                    Intent intent = new Intent(getContext(), MapViewActivity.class);
                    intent.putExtra("location", mList.get(mListnumber).getLocation());
                    startActivity(intent);
                }
                mPopupWindow.dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
        mBind.unbind();
    }
}
