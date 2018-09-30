package com.busradeniz.detection.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SupportBean {


    @Id(autoincrement = true)
    private long _id;

    private String projectName;

    private String Data;

    private String location;


    private boolean selectedStatus;



    @Generated(hash = 263525024)
    public SupportBean(long _id, String projectName, String Data, String location,
            boolean selectedStatus) {
        this._id = _id;
        this.projectName = projectName;
        this.Data = Data;
        this.location = location;
        this.selectedStatus = selectedStatus;
    }

    @Generated(hash = 1317272459)
    public SupportBean() {
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getData() {
        return this.Data;
    }

    public void setData(String Data) {
        this.Data = Data;
    }

    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public boolean getSelectedStatus() {
        return this.selectedStatus;
    }

    public void setSelectedStatus(boolean selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
