package com.busradeniz.detection.bean;

public class ConfigureInfoBean {


    /**
     * result : 0
     * data : {"name":"OPPO","id":24,"data":"{\"deviation\":[{\"deviation\":{\"bottom\":{\"negative\":0,\"puls\":0},\"left\":{\"negative\":0,\"puls\":0},\"right\":{\"negative\":0,\"puls\":0},\"top\":{\"negative\":0,\"puls\":0}},\"location\":{\"bottom\":697,\"left\":34,\"right\":360,\"top\":257},\"name\":\"Battery\",\"status\":true},{\"deviation\":{\"bottom\":{\"negative\":0,\"puls\":0},\"left\":{\"negative\":0,\"puls\":0},\"right\":{\"negative\":0,\"puls\":0},\"top\":{\"negative\":0,\"puls\":0}},\"location\":{\"bottom\":272,\"left\":119,\"right\":263,\"top\":186},\"name\":\"FPC\",\"status\":true},{\"deviation\":{\"bottom\":{\"negative\":0,\"puls\":0},\"left\":{\"negative\":0,\"puls\":0},\"right\":{\"negative\":0,\"puls\":0},\"top\":{\"negative\":0,\"puls\":0}},\"location\":{\"bottom\":237,\"left\":319,\"right\":332,\"top\":224},\"name\":\"Screw\",\"status\":false},{\"deviation\":{\"bottom\":{\"negative\":0,\"puls\":0},\"left\":{\"negative\":0,\"puls\":0},\"right\":{\"negative\":0,\"puls\":0},\"top\":{\"negative\":0,\"puls\":0}},\"location\":{\"bottom\":478,\"left\":17,\"right\":26,\"top\":469},\"name\":\"Screw nut\",\"status\":false},{\"deviation\":{\"bottom\":{\"negative\":0,\"puls\":0},\"left\":{\"negative\":0,\"puls\":0},\"right\":{\"negative\":0,\"puls\":0},\"top\":{\"negative\":0,\"puls\":0}},\"location\":{\"bottom\":624,\"left\":17,\"right\":26,\"top\":613},\"name\":\"Screw nut\",\"status\":false},{\"deviation\":{\"bottom\":{\"negative\":0,\"puls\":0},\"left\":{\"negative\":0,\"puls\":0},\"right\":{\"negative\":0,\"puls\":0},\"top\":{\"negative\":0,\"puls\":0}},\"location\":{\"bottom\":198,\"left\":164,\"right\":229,\"top\":133},\"name\":\"Fingerprint\",\"status\":true}],\"location\":[{\"checkLocation\":{\"bottom\":737,\"left\":0,\"right\":400,\"top\":217},\"left_distance\":34,\"width\":326,\"name\":\"Battery\",\"height\":440,\"top_distance\":40},{\"checkLocation\":{\"bottom\":312,\"left\":79,\"right\":303,\"top\":146},\"left_distance\":40,\"width\":144,\"name\":\"FPC\",\"height\":86,\"top_distance\":40},{\"checkLocation\":{\"bottom\":238,\"left\":124,\"right\":269,\"top\":93},\"left_distance\":40,\"width\":65,\"name\":\"Fingerprint\",\"height\":65,\"top_distance\":40}]}","config_type":1,"delete_at":null,"path":"/home/advan-serving/images/config/24.jpg","remote_id":null,"config_type_name":"semi","update_at":"2018-10-24T10:37:50.670"}
     */

    private int result;
    private DataBean data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : OPPO
         * id : 24
         * data : {"deviation":[{"deviation":{"bottom":{"negative":0,"puls":0},"left":{"negative":0,"puls":0},"right":{"negative":0,"puls":0},"top":{"negative":0,"puls":0}},"location":{"bottom":697,"left":34,"right":360,"top":257},"name":"Battery","status":true},{"deviation":{"bottom":{"negative":0,"puls":0},"left":{"negative":0,"puls":0},"right":{"negative":0,"puls":0},"top":{"negative":0,"puls":0}},"location":{"bottom":272,"left":119,"right":263,"top":186},"name":"FPC","status":true},{"deviation":{"bottom":{"negative":0,"puls":0},"left":{"negative":0,"puls":0},"right":{"negative":0,"puls":0},"top":{"negative":0,"puls":0}},"location":{"bottom":237,"left":319,"right":332,"top":224},"name":"Screw","status":false},{"deviation":{"bottom":{"negative":0,"puls":0},"left":{"negative":0,"puls":0},"right":{"negative":0,"puls":0},"top":{"negative":0,"puls":0}},"location":{"bottom":478,"left":17,"right":26,"top":469},"name":"Screw nut","status":false},{"deviation":{"bottom":{"negative":0,"puls":0},"left":{"negative":0,"puls":0},"right":{"negative":0,"puls":0},"top":{"negative":0,"puls":0}},"location":{"bottom":624,"left":17,"right":26,"top":613},"name":"Screw nut","status":false},{"deviation":{"bottom":{"negative":0,"puls":0},"left":{"negative":0,"puls":0},"right":{"negative":0,"puls":0},"top":{"negative":0,"puls":0}},"location":{"bottom":198,"left":164,"right":229,"top":133},"name":"Fingerprint","status":true}],"location":[{"checkLocation":{"bottom":737,"left":0,"right":400,"top":217},"left_distance":34,"width":326,"name":"Battery","height":440,"top_distance":40},{"checkLocation":{"bottom":312,"left":79,"right":303,"top":146},"left_distance":40,"width":144,"name":"FPC","height":86,"top_distance":40},{"checkLocation":{"bottom":238,"left":124,"right":269,"top":93},"left_distance":40,"width":65,"name":"Fingerprint","height":65,"top_distance":40}]}
         * config_type : 1
         * delete_at : null
         * path : /home/advan-serving/images/config/24.jpg
         * remote_id : null
         * config_type_name : semi
         * update_at : 2018-10-24T10:37:50.670
         */

        private String name;
        private int id;
        private String data;
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

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
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
