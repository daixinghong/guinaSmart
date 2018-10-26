package com.busradeniz.detection.bean;

import java.util.List;

public class LocationBean {


    private List<DeviationBeanX> deviation;
    private List<LocationBeanX> location;

    public List<DeviationBeanX> getDeviation() {
        return deviation;
    }

    public void setDeviation(List<DeviationBeanX> deviation) {
        this.deviation = deviation;
    }

    public List<LocationBeanX> getLocation() {
        return location;
    }

    public void setLocation(List<LocationBeanX> location) {
        this.location = location;
    }

    public static class DeviationBeanX {
        /**
         * deviation : {"bottom":{"negative":0,"puls":0},"left":{"negative":0,"puls":0},"right":{"negative":0,"puls":0},"top":{"negative":0,"puls":0}}
         * location : {"bottom":697,"left":34,"right":360,"top":257}
         * name : Battery
         * status : true
         */

        private DeviationBean deviation;
        private LocationBeanB location;
        private String name;
        private boolean status;

        public DeviationBean getDeviation() {
            return deviation;
        }

        public void setDeviation(DeviationBean deviation) {
            this.deviation = deviation;
        }

        public LocationBeanB getLocation() {
            return location;
        }

        public void setLocation(LocationBeanB location) {
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public static class DeviationBean {
            /**
             * bottom : {"negative":0,"puls":0}
             * left : {"negative":0,"puls":0}
             * right : {"negative":0,"puls":0}
             * top : {"negative":0,"puls":0}
             */

            private BottomBean bottom;
            private LeftBean left;
            private RightBean right;
            private TopBean top;

            public BottomBean getBottom() {
                return bottom;
            }

            public void setBottom(BottomBean bottom) {
                this.bottom = bottom;
            }

            public LeftBean getLeft() {
                return left;
            }

            public void setLeft(LeftBean left) {
                this.left = left;
            }

            public RightBean getRight() {
                return right;
            }

            public void setRight(RightBean right) {
                this.right = right;
            }

            public TopBean getTop() {
                return top;
            }

            public void setTop(TopBean top) {
                this.top = top;
            }

            public static class BottomBean {
                /**
                 * negative : 0
                 * puls : 0
                 */

                private int negative;
                private int puls;

                public int getNegative() {
                    return negative;
                }

                public void setNegative(int negative) {
                    this.negative = negative;
                }

                public int getPuls() {
                    return puls;
                }

                public void setPuls(int puls) {
                    this.puls = puls;
                }
            }

            public static class LeftBean {
                /**
                 * negative : 0
                 * puls : 0
                 */

                private int negative;
                private int puls;

                public int getNegative() {
                    return negative;
                }

                public void setNegative(int negative) {
                    this.negative = negative;
                }

                public int getPuls() {
                    return puls;
                }

                public void setPuls(int puls) {
                    this.puls = puls;
                }
            }

            public static class RightBean {
                /**
                 * negative : 0
                 * puls : 0
                 */

                private int negative;
                private int puls;

                public int getNegative() {
                    return negative;
                }

                public void setNegative(int negative) {
                    this.negative = negative;
                }

                public int getPuls() {
                    return puls;
                }

                public void setPuls(int puls) {
                    this.puls = puls;
                }
            }

            public static class TopBean {
                /**
                 * negative : 0
                 * puls : 0
                 */

                private int negative;
                private int puls;

                public int getNegative() {
                    return negative;
                }

                public void setNegative(int negative) {
                    this.negative = negative;
                }

                public int getPuls() {
                    return puls;
                }

                public void setPuls(int puls) {
                    this.puls = puls;
                }
            }
        }

        public static class LocationBeanB {
            /**
             * bottom : 697
             * left : 34
             * right : 360
             * top : 257
             */

            private int bottom;
            private int left;
            private int right;
            private int top;

            public int getBottom() {
                return bottom;
            }

            public void setBottom(int bottom) {
                this.bottom = bottom;
            }

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getRight() {
                return right;
            }

            public void setRight(int right) {
                this.right = right;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }
        }
    }

    public static class LocationBeanX {
        /**
         * checkLocation : {"bottom":737,"left":0,"right":400,"top":217}
         * height : 440
         * left_distance : 34
         * name : Battery
         * top_distance : 40
         * width : 326
         */

        private CheckLocationBean checkLocation;
        private int height;
        private int left_distance;
        private String name;
        private int top_distance;
        private int width;

        public CheckLocationBean getCheckLocation() {
            return checkLocation;
        }

        public void setCheckLocation(CheckLocationBean checkLocation) {
            this.checkLocation = checkLocation;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getLeft_distance() {
            return left_distance;
        }

        public void setLeft_distance(int left_distance) {
            this.left_distance = left_distance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTop_distance() {
            return top_distance;
        }

        public void setTop_distance(int top_distance) {
            this.top_distance = top_distance;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public static class CheckLocationBean {
            /**
             * bottom : 737
             * left : 0
             * right : 400
             * top : 217
             */

            private int bottom;
            private int left;
            private int right;
            private int top;

            public int getBottom() {
                return bottom;
            }

            public void setBottom(int bottom) {
                this.bottom = bottom;
            }

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getRight() {
                return right;
            }

            public void setRight(int right) {
                this.right = right;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }
        }
    }
}
