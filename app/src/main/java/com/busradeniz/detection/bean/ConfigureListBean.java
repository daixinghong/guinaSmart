package com.busradeniz.detection.bean;

import java.util.List;

public class ConfigureListBean {


    /**
     * result : 0
     * datas : [{"name":"OPPO","id":24,"config_type":1,"delete_at":null,"path":"/home/advan-serving/images/config/24.jpg","remote_id":null,"config_type_name":"semi","update_at":"2018-10-24T10:37:50.670"}]
     * pages : {"totalpage":1,"total":1,"currentpage":1}
     */

    private int result;
    private PagesBean pages;
    private List<DatasBean> datas;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public PagesBean getPages() {
        return pages;
    }

    public void setPages(PagesBean pages) {
        this.pages = pages;
    }

    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }

    public static class PagesBean {
        /**
         * totalpage : 1
         * total : 1
         * currentpage : 1
         */

        private int totalpage;
        private int total;
        private int currentpage;

        public int getTotalpage() {
            return totalpage;
        }

        public void setTotalpage(int totalpage) {
            this.totalpage = totalpage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getCurrentpage() {
            return currentpage;
        }

        public void setCurrentpage(int currentpage) {
            this.currentpage = currentpage;
        }
    }

    public static class DatasBean {
        /**
         * name : OPPO
         * id : 24
         * config_type : 1
         * delete_at : null
         * path : /home/advan-serving/images/config/24.jpg
         * remote_id : null
         * config_type_name : semi
         * update_at : 2018-10-24T10:37:50.670
         */

        private String name;
        private int id;
        private int config_type;
        private Object delete_at;
        private String path;
        private Object remote_id;
        private String config_type_name;
        private String update_at;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getConfig_type() {
            return config_type;
        }

        public void setConfig_type(int config_type) {
            this.config_type = config_type;
        }

        public Object getDelete_at() {
            return delete_at;
        }

        public void setDelete_at(Object delete_at) {
            this.delete_at = delete_at;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Object getRemote_id() {
            return remote_id;
        }

        public void setRemote_id(Object remote_id) {
            this.remote_id = remote_id;
        }

        public String getConfig_type_name() {
            return config_type_name;
        }

        public void setConfig_type_name(String config_type_name) {
            this.config_type_name = config_type_name;
        }

        public String getUpdate_at() {
            return update_at;
        }

        public void setUpdate_at(String update_at) {
            this.update_at = update_at;
        }
    }
}
