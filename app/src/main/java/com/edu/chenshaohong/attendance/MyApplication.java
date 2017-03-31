package com.edu.chenshaohong.attendance;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by shaohong on 2017-3-19.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    private String appId;
    private String phoneNumbere;

    public static MyApplication getInstance() {
        return instance;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPhoneNumbere() {
        return phoneNumbere;
    }

    public void setPhoneNumbere(String phoneNumbere) {
        this.phoneNumbere = phoneNumbere;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        SDKInitializer.initialize(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        } else {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            appId = tm.getDeviceId() + "0";
            if (appId.length() < 16) {
                appId = appId + "00000000000000000000";
                appId = appId.substring(0, 16);
            }
        }
    }
}
