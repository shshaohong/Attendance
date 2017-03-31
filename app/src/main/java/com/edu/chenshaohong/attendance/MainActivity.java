package com.edu.chenshaohong.attendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.edu.chenshaohong.attendance.activity.LoginActivity;
import com.edu.chenshaohong.attendance.entity.AllUserDataEntity;
import com.edu.chenshaohong.attendance.entity.MessageEntity;
import com.edu.chenshaohong.attendance.entity.UserDataEntity;
import com.edu.chenshaohong.attendance.fragment.CompanyFragment;
import com.edu.chenshaohong.attendance.nextwork.AttendanceApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主界面
 */
public class MainActivity extends FragmentActivity {

    @BindView(R.id.listView)
    ListView listView;
    private Unbinder mBind;
    private AlertDialog.Builder dialog;
    private List<String> data;
    private AttendanceApi.Api mApi;

    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBind = ButterKnife.bind(this);
        init();
    }

    private void init() {
        data = new ArrayList<>();
        SharedPreferences sf = getSharedPreferences("user", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        final List<MessageEntity> list = gson.fromJson(sf.getString("messagelist", "[]"),
                new TypeToken<List<MessageEntity>>(){}.getType());

        for (int i = 0; i < list.size(); i++) {
            data.add(list.get(i).getMachineName());
            Log.e("list", data.get(i));
        }
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                mApi = AttendanceApi.createApi();
                mApi.getAllUserData(MyApplication.getInstance().getAppId(),
                        MyApplication.getInstance().getPhoneNumbere(),
                        list.get(i).getMachineId()).enqueue(new Callback<AllUserDataEntity>() {
                    @Override
                    public void onResponse(Call<AllUserDataEntity> call, Response<AllUserDataEntity> response) {
                        List<UserDataEntity> message = response.body().getMessage();
                        if (response.body().getStatus().equals("1")) {
                            CompanyFragment fragment = new CompanyFragment();
                            Gson gson1 = new Gson();
                            String json = gson1.toJson(message);
                            fragment.show(getSupportFragmentManager(),"CompanyFragment");
                            SharedPreferences sf = getSharedPreferences("userdata", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sf.edit();
                            editor.remove("usermessage");
                            editor.putInt("listnumber", i);
                            editor.putString("usermessage", json);
                            editor.commit();
                        }
                    }

                    @Override
                    public void onFailure(Call<AllUserDataEntity> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }
    //退出按钮
    @OnClick(R.id.exit)
    public void setExit(){
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示：");
        dialog.setMessage("是否退出应用程序？");
        dialog.setNegativeButton("取消",null);

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                SharedPreferences sf = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.remove("number");
                editor.commit();
                MainActivity.this.finish();
            }
        });
        dialog.show();
    }
}
