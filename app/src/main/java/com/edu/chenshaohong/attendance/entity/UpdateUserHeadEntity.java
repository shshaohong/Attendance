package com.edu.chenshaohong.attendance.entity;

import okhttp3.MultipartBody;

/**
 * Created by shaohong on 2017-3-25.
 */

public class UpdateUserHeadEntity {

    private String phone;
    private MultipartBody.Part mPart;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public MultipartBody.Part getPart() {
        return mPart;
    }

    public void setPart(MultipartBody.Part part) {
        mPart = part;
    }
}
