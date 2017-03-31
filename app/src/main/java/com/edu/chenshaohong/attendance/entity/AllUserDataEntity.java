package com.edu.chenshaohong.attendance.entity;

import java.util.List;

/**
 * Created by shaohong on 2017-3-22.
 */

public class AllUserDataEntity {
    private String status;
    private List<UserDataEntity> message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserDataEntity> getMessage() {
        return message;
    }

    public void setMessage(List<UserDataEntity> message) {
        this.message = message;
    }
}
