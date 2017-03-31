package com.edu.chenshaohong.attendance.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.edu.chenshaohong.attendance.MainActivity;
import com.edu.chenshaohong.attendance.MyApplication;
import com.edu.chenshaohong.attendance.R;
import com.edu.chenshaohong.attendance.entity.LoginEntity;
import com.edu.chenshaohong.attendance.nextwork.AttendanceApi;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 公司logo界面
 */
public class LogoActivity extends FragmentActivity {

    private static final int REQUEST_CODE = 100;

    private String appId;
    private String mNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        ButterKnife.bind(this);
        initAppId();
        setAnimation();
    }

    private void setAnimation() {
        SharedPreferences sf = getSharedPreferences("user", Context.MODE_PRIVATE);
        mNumber = sf.getString("number", "");
        if (TextUtils.isEmpty(mNumber)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LogoActivity.this, LoginActivity.class));
                    finish();
                }
            },2000);
        } else {
        MyApplication.getInstance().setPhoneNumbere(mNumber);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goMainActivity();

                }
            },1000);
        }
    }

    private void goMainActivity() {
        Log.e("appid", appId);
        AttendanceApi.Api api = AttendanceApi.createApi();
        api.login(appId, mNumber)
                .enqueue(new Callback<LoginEntity>() {
                    @Override
                    public void onResponse(Call<LoginEntity> call, Response<LoginEntity> response) {
                        if (response.body().getStatus().equals("1")) {
                            startActivity(new Intent(LogoActivity.this, MainActivity.class));

                            SharedPreferences sf = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sf.edit();
                            editor.putString("number", mNumber);
                            editor.commit();

                            finish();
                        } else {
                            Toast.makeText(LogoActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginEntity> call, Throwable t) {
                        startActivity(new Intent(LogoActivity.this, MainActivity.class));
                        Toast.makeText(LogoActivity.this, "访问网络异常", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void initAppId() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        } else {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            appId = tm.getDeviceId() + "0";
            if (appId.length() < 16) {
                appId = appId + "00000000000000000000";
                appId = appId.substring(0, 16);
                MyApplication.getInstance().setAppId(appId);
            }
        }
    }
}
