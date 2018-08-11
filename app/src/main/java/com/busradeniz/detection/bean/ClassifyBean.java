package com.busradeniz.detection.bean;

import com.google.gson.annotations.SerializedName;

public class ClassifyBean {


    /**
     * category_index : {"1":{"id":1,"name":"LCD"},"2":{"id":2,"name":"Camera"},"3":{"id":3,"name":"Fingerprint"},"4":{"id":4,"name":"Battery"},"5":{"id":5,"name":"Mic"},"6":{"id":6,"name":"Speaker"},"7":{"id":7,"name":"Motor"},"8":{"id":8,"name":"FPC"},"9":{"id":9,"name":"Cable"},"10":{"id":10,"name":"Buckle"},"11":{"id":11,"name":"Steel sheet"},"12":{"id":12,"name":"Spring"},"13":{"id":13,"name":"Screw"},"14":{"id":14,"name":"Screw nut"},"15":{"id":15,"name":"Foam"},"16":{"id":16,"name":"Waterproof"},"17":{"id":17,"name":"TP"}}
     * result : 0
     */

    private CategoryIndexBean category_index;
    private int result;

    public CategoryIndexBean getCategory_index() {
        return category_index;
    }

    public void setCategory_index(CategoryIndexBean category_index) {
        this.category_index = category_index;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public static class CategoryIndexBean {
        /**
         * 1 : {"id":1,"name":"LCD"}
         * 2 : {"id":2,"name":"Camera"}
         * 3 : {"id":3,"name":"Fingerprint"}
         * 4 : {"id":4,"name":"Battery"}
         * 5 : {"id":5,"name":"Mic"}
         * 6 : {"id":6,"name":"Speaker"}
         * 7 : {"id":7,"name":"Motor"}
         * 8 : {"id":8,"name":"FPC"}
         * 9 : {"id":9,"name":"Cable"}
         * 10 : {"id":10,"name":"Buckle"}
         * 11 : {"id":11,"name":"Steel sheet"}
         * 12 : {"id":12,"name":"Spring"}
         * 13 : {"id":13,"name":"Screw"}
         * 14 : {"id":14,"name":"Screw nut"}
         * 15 : {"id":15,"name":"Foam"}
         * 16 : {"id":16,"name":"Waterproof"}
         * 17 : {"id":17,"name":"TP"}
         */

        @SerializedName("1")
        private _$1Bean _$1;
        @SerializedName("2")
        private _$2Bean _$2;
        @SerializedName("3")
        private _$3Bean _$3;
        @SerializedName("4")
        private _$4Bean _$4;
        @SerializedName("5")
        private _$5Bean _$5;
        @SerializedName("6")
        private _$6Bean _$6;
        @SerializedName("7")
        private _$7Bean _$7;
        @SerializedName("8")
        private _$8Bean _$8;
        @SerializedName("9")
        private _$9Bean _$9;
        @SerializedName("10")
        private _$10Bean _$10;
        @SerializedName("11")
        private _$11Bean _$11;
        @SerializedName("12")
        private _$12Bean _$12;
        @SerializedName("13")
        private _$13Bean _$13;
        @SerializedName("14")
        private _$14Bean _$14;
        @SerializedName("15")
        private _$15Bean _$15;
        @SerializedName("16")
        private _$16Bean _$16;
        @SerializedName("17")
        private _$17Bean _$17;

        public _$1Bean get_$1() {
            return _$1;
        }

        public void set_$1(_$1Bean _$1) {
            this._$1 = _$1;
        }

        public _$2Bean get_$2() {
            return _$2;
        }

        public void set_$2(_$2Bean _$2) {
            this._$2 = _$2;
        }

        public _$3Bean get_$3() {
            return _$3;
        }

        public void set_$3(_$3Bean _$3) {
            this._$3 = _$3;
        }

        public _$4Bean get_$4() {
            return _$4;
        }

        public void set_$4(_$4Bean _$4) {
            this._$4 = _$4;
        }

        public _$5Bean get_$5() {
            return _$5;
        }

        public void set_$5(_$5Bean _$5) {
            this._$5 = _$5;
        }

        public _$6Bean get_$6() {
            return _$6;
        }

        public void set_$6(_$6Bean _$6) {
            this._$6 = _$6;
        }

        public _$7Bean get_$7() {
            return _$7;
        }

        public void set_$7(_$7Bean _$7) {
            this._$7 = _$7;
        }

        public _$8Bean get_$8() {
            return _$8;
        }

        public void set_$8(_$8Bean _$8) {
            this._$8 = _$8;
        }

        public _$9Bean get_$9() {
            return _$9;
        }

        public void set_$9(_$9Bean _$9) {
            this._$9 = _$9;
        }

        public _$10Bean get_$10() {
            return _$10;
        }

        public void set_$10(_$10Bean _$10) {
            this._$10 = _$10;
        }

        public _$11Bean get_$11() {
            return _$11;
        }

        public void set_$11(_$11Bean _$11) {
            this._$11 = _$11;
        }

        public _$12Bean get_$12() {
            return _$12;
        }

        public void set_$12(_$12Bean _$12) {
            this._$12 = _$12;
        }

        public _$13Bean get_$13() {
            return _$13;
        }

        public void set_$13(_$13Bean _$13) {
            this._$13 = _$13;
        }

        public _$14Bean get_$14() {
            return _$14;
        }

        public void set_$14(_$14Bean _$14) {
            this._$14 = _$14;
        }

        public _$15Bean get_$15() {
            return _$15;
        }

        public void set_$15(_$15Bean _$15) {
            this._$15 = _$15;
        }

        public _$16Bean get_$16() {
            return _$16;
        }

        public void set_$16(_$16Bean _$16) {
            this._$16 = _$16;
        }

        public _$17Bean get_$17() {
            return _$17;
        }

        public void set_$17(_$17Bean _$17) {
            this._$17 = _$17;
        }

        public static class _$1Bean {
            /**
             * id : 1
             * name : LCD
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$2Bean {
            /**
             * id : 2
             * name : Camera
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$3Bean {
            /**
             * id : 3
             * name : Fingerprint
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$4Bean {
            /**
             * id : 4
             * name : Battery
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$5Bean {
            /**
             * id : 5
             * name : Mic
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$6Bean {
            /**
             * id : 6
             * name : Speaker
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$7Bean {
            /**
             * id : 7
             * name : Motor
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$8Bean {
            /**
             * id : 8
             * name : FPC
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$9Bean {
            /**
             * id : 9
             * name : Cable
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$10Bean {
            /**
             * id : 10
             * name : Buckle
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$11Bean {
            /**
             * id : 11
             * name : Steel sheet
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$12Bean {
            /**
             * id : 12
             * name : Spring
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$13Bean {
            /**
             * id : 13
             * name : Screw
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$14Bean {
            /**
             * id : 14
             * name : Screw nut
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$15Bean {
            /**
             * id : 15
             * name : Foam
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$16Bean {
            /**
             * id : 16
             * name : Waterproof
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class _$17Bean {
            /**
             * id : 17
             * name : TP
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
