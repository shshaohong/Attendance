package com.edu.chenshaohong.attendance.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.chenshaohong.attendance.MainActivity;
import com.edu.chenshaohong.attendance.MyApplication;
import com.edu.chenshaohong.attendance.R;
import com.edu.chenshaohong.attendance.entity.LoginEntity;
import com.edu.chenshaohong.attendance.entity.MessageEntity;
import com.edu.chenshaohong.attendance.nextwork.AttendanceApi;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登陆界面
 */
public class LoginActivity extends Activity {

    private static final int REQUEST_CODE = 100;
    @BindView(R.id.et_phoneNumber)
    EditText mEtPhoneNumber;
    @BindView(R.id.et_yanZheng)
    EditText mEtYanZheng;
    @BindView(R.id.tv_getYanZheng)
    TextView mTvGetYanZheng;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    private Unbinder mBind;
    private String appId;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SMSSDK.initSDK(this, "1c3530eeca38d", "0bc9a59bd680bf8ef400aed9517f7887");
        mBind = ButterKnife.bind(this);
        mDialog = new ProgressDialog(this);
        initAppId();
        init();
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

    private void init() {

        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                mDialog.cancel();
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
//                        goMainActivity();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }

                } else {
                    ((Throwable) data).printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "验证失败", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        };
        //注册信息回调
        SMSSDK.registerEventHandler(eventHandler);

    }

    private void goMainActivity() {
        Log.e("appid", appId);
        AttendanceApi.Api api = AttendanceApi.createApi();
        api.login(appId, mEtPhoneNumber.getText().toString().trim())
                .enqueue(new Callback<LoginEntity>() {
                    @Override
                    public void onResponse(Call<LoginEntity> call, Response<LoginEntity> response) {
                        mDialog.cancel();
                        if (response.body().getStatus().equals("1")) {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences sf = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sf.edit();
                            editor.putString("number", mEtPhoneNumber.getText().toString());

                            List<MessageEntity> message = response.body().getMessage();
                            Gson gson = new Gson();
                            editor.putString("messagelist", gson.toJson(message));
                            editor.commit();
                            MyApplication.getInstance().setPhoneNumbere(mEtPhoneNumber.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginEntity> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "访问网络异常", Toast.LENGTH_SHORT).show();
                        mDialog.cancel();
                    }
                });
    }

    @OnClick(R.id.tv_getYanZheng)
    public void setEtYanZheng() {
        String number = mEtPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (number.length() != 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "验证码已发送，请注意查看！", Toast.LENGTH_SHORT).show();
        SMSSDK.getVerificationCode("86", number);
    }

    @OnClick(R.id.btn_login)
    public void setBtnLogin() {
        mDialog.setMessage("正在登录...");
        mDialog.show();
//        if (check()) {
//            SMSSDK.submitVerificationCode("86",mEtPhoneNumber.getText().toString().trim(),
//                    mEtYanZheng.getText().toString().trim());
//        }
        if (mEtPhoneNumber.getText().toString().length() == 0) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            mDialog.cancel();
            return;
        }
        goMainActivity();
    }

    private boolean check() {
        if (mEtPhoneNumber.getText().length() == 0) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            mDialog.cancel();
            return false;
        }
        if (mEtYanZheng.getText().length() == 0) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            mDialog.cancel();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
        mBind.unbind();
    }
}
