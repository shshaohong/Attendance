package com.edu.chenshaohong.attendance.entity;

import java.util.List;

/**
 * Created by shaohong on 2017-3-27.
 */

public class DayRecordEntity {
    private String status;
    private List<TimeStampEntity> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TimeStampEntity> getData() {
        return data;
    }

    public void setData(List<TimeStampEntity> data) {
        this.data = data;
    }
}
