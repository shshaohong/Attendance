package com.edu.chenshaohong.attendance.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shaohong on 2017-3-20.
 */

public class MessageEntity {

    @SerializedName("machine_name")
    private String machineName;//机器名称
    @SerializedName("tp_machineid")
    private String machineId;//机器ID
    private String location;//公司考勤地点经纬度（经度：维度）

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
