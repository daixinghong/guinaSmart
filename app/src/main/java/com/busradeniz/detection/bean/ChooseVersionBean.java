package com.busradeniz.detection.bean;

import java.util.List;

public class ChooseVersionBean {

    private String projectName;

    private List<NewVersionBean> list;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<NewVersionBean> getList() {
        return list;
    }

    public void setList(List<NewVersionBean> list) {
        this.list = list;
    }
}
