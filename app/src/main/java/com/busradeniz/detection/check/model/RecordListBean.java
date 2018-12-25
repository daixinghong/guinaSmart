package com.busradeniz.detection.check.model;

import java.util.List;

public class RecordListBean {


    /**
     * datas : [{"result":1,"upload_at":null,"path":"/home/advan-serving/images/upload/20181022/1540180068830.jpg","id":1,"config":1,"config_name":"ttt","create_at":"2018-10-22T03:47:48.835","defect":"{\"json\":\"text\"}"},{"result":1,"upload_at":null,"path":"/home/advan-serving/images/upload/20181022/1540192575890.jpg","id":2,"config":1,"config_name":"ttt","create_at":"2018-10-22T07:16:15.897","defect":"11"},{"result":1,"upload_at":null,"path":"/home/advan-serving/images/upload/20181022/1540192674384.jpg","id":3,"config":1,"config_name":"ttt","create_at":"2018-10-22T07:17:54.395","defect":"11"},{"result":1,"upload_at":null,"path":"/home/advan-serving/images/upload/20181022/1540193015768.jpg","id":4,"config":1,"config_name":"ttt","create_at":"2018-10-22T15:23:35.779","defect":"11"},{"result":1,"upload_at":null,"path":"/home/advan-serving/images/upload/20181022/1540193029936.jpg","id":5,"config":1,"config_name":"ttt","create_at":"2018-10-22T15:23:49.947","defect":"11"},{"result":0,"upload_at":null,"path":"/home/advan-serving/images/upload/20181023/1540264424304.jpg","id":6,"config":1,"config_name":"ttt","create_at":"2018-10-23T11:13:44.306","defect":"11"},{"result":1,"upload_at":null,"path":"/home/advan-serving/images/upload/20181025/1540435483990.jpg","id":7,"config":24,"config_name":"OPPs","create_at":"2018-10-25T10:44:44.004","defect":"位置偏移"},{"result":0,"upload_at":null,"path":"/home/advan-serving/images/upload/20181025/1540435661071.jpg","id":8,"config":24,"config_name":"OPPs","create_at":"2018-10-25T10:47:41.086","defect":"位置偏移"}]
     * result : 0
     * pages : {"total":8,"totalpage":1,"currentpage":"1"}
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
         * total : 8
         * totalpage : 1
         * currentpage : 1
         */

        private int total;
        private int totalpage;
        private String currentpage;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalpage() {
            return totalpage;
        }

        public void setTotalpage(int totalpage) {
            this.totalpage = totalpage;
        }

        public String getCurrentpage() {
            return currentpage;
        }

        public void setCurrentpage(String currentpage) {
            this.currentpage = currentpage;
        }
    }

    public static class DatasBean {
        /**
         * result : 1
         * upload_at : null
         * path : /home/advan-serving/images/upload/20181022/1540180068830.jpg
         * id : 1
         * config : 1
         * config_name : ttt
         * create_at : 2018-10-22T03:47:48.835
         * defect : {"json":"text"}
         */

        private int result;
        private Object upload_at;
        private String path;
        private int id;
        private int config;
        private String config_name;
        private String create_at;
        private String defect;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public Object getUpload_at() {
            return upload_at;
        }

        public void setUpload_at(Object upload_at) {
            this.upload_at = upload_at;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getConfig() {
            return config;
        }

        public void setConfig(int config) {
            this.config = config;
        }

        public String getConfig_name() {
            return config_name;
        }

        public void setConfig_name(String config_name) {
            this.config_name = config_name;
        }

        public String getCreate_at() {
            return create_at;
        }

        public void setCreate_at(String create_at) {
            this.create_at = create_at;
        }

        public String getDefect() {
            return defect;
        }

        public void setDefect(String defect) {
            this.defect = defect;
        }
    }
}
