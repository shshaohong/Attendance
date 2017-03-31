package com.edu.chenshaohong.attendance.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shaohong on 2017-3-22.
 */

/**每个用户信息
 *
 * department：部门
 email:电子邮箱
 headurl:用户头像
 id:用户识别符，用于获取用户月，日考勤信息
 phonenumber:手机号码

 */
public class UserDataEntity {
    private String id;
    private String username;
    private String phonenumber;
    private String email;
    @SerializedName("is_online")
    private String isOnline;
    @SerializedName("is_admin")
    private String isAdmin;
    @SerializedName("is_accept")
    private String isAccept;
    private String headurl;
    private String topimg;
    private String department;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getIsAccept() {
        return isAccept;
    }

    public void setIsAccept(String isAccept) {
        this.isAccept = isAccept;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getTopimg() {
        return topimg;
    }

    public void setTopimg(String topimg) {
        this.topimg = topimg;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

