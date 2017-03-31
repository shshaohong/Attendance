package com.edu.chenshaohong.attendance.entity;

import java.util.List;

/**
 * Created by shaohong on 2017-3-20.
 */

public class LoginEntity {
    private String status;
    private List<MessageEntity> message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MessageEntity> getMessage() {
        return message;
    }

    public void setMessage(List<MessageEntity> message) {
        this.message = message;
    }
}
