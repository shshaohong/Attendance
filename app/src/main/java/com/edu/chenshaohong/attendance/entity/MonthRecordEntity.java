package com.edu.chenshaohong.attendance.entity;

import java.util.List;

/**
 * Created by shaohong on 2017-3-23.
 */

public class MonthRecordEntity {
    private String status;
    private List<MonthEntity> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MonthEntity> getData() {
        return data;
    }

    public void setData(List<MonthEntity> data) {
        this.data = data;
    }
}
