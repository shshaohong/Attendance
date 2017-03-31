package com.edu.chenshaohong.attendance.nextwork;

import com.edu.chenshaohong.attendance.entity.AllUserDataEntity;
import com.edu.chenshaohong.attendance.entity.BaseEntity;
import com.edu.chenshaohong.attendance.entity.DayRecordEntity;
import com.edu.chenshaohong.attendance.entity.LoginEntity;
import com.edu.chenshaohong.attendance.entity.MonthRecordEntity;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by shaohong on 2017-3-20.
 */

public class AttendanceApi {
    public static Api createApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.40.70.170")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);
        return api;
    }

    public interface Api {
        //登录
        @GET("/attendance/login/{appid}/{phonenumber}/bind")
        Call<LoginEntity> login(@Path("appid") String appid,
                                @Path("phonenumber") String phoneNumber);

        //获取所有用户信息,进入主界面触发
        @GET("/attendance/machine/{appid}/{phonenumber}/{machineid}")
        Call<AllUserDataEntity> getAllUserData(@Path("appid") String appid,
                                               @Path("phonenumber") String phonenumber,
                                               @Path("machineid") String machineid);
        //修改用户信息
        @GET("/attendance/update/{userId}/{department}/{email}")
        Call<BaseEntity> updateUserData(@Path("userId") String userId,
                                        @Path("department") String department,
                                        @Path("email") String email);

        //获取月考勤记录
        @GET("/attendance/getPunchMonth/{userid}/{machineid}/{time}")
        Call<MonthRecordEntity> getMonthRecord(@Path("userid") String userid,
                                               @Path("machineid") String machineid,
                                               @Path("time") String time);
        //获取日考勤记录
        @GET("/attendance/getPunchDay/{userid}/{machineid}/{time}")
        Call<DayRecordEntity> getDayRecord(@Path("userid") String userid,
                                           @Path("machineid") String machineid,
                                           @Path("time") String time);
        //上传头像
        @Multipart
        @POST("/attendance/head_back/")
        Call<BaseEntity> updateUserHead(
                @Part List<MultipartBody.Part> part);
    }

}
