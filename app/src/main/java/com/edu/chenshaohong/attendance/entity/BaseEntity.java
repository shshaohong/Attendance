package com.edu.chenshaohong.attendance.entity;

/**
 * Created by shaohong on 2017-3-22.
 */

public class BaseEntity {
    private String status;
    private String message;
    private String mess;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }
}
