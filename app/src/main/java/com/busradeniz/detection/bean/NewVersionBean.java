package com.busradeniz.detection.bean;

import java.io.Serializable;

public class NewVersionBean implements Serializable {

    private String name;
    private String classify;
    private Location location;
    private Deviation deviation;
    private boolean status = true;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public Deviation getDeviation() {
        return deviation;
    }

    public void setDeviation(Deviation deviation) {
        this.deviation = deviation;
    }

    public static class Deviation implements Serializable {

        private Left left;
        private Left right;
        private Left top;
        private Left bottom;

        public Left getLeft() {
            return left;
        }

        public void setLeft(Left left) {
            this.left = left;
        }

        public Left getRight() {
            return right;
        }

        public void setRight(Left right) {
            this.right = right;
        }

        public Left getTop() {
            return top;
        }

        public void setTop(Left top) {
            this.top = top;
        }

        public Left getBottom() {
            return bottom;
        }

        public void setBottom(Left bottom) {
            this.bottom = bottom;
        }

        public static class Left implements Serializable {
            private int puls;

            private int negative;

            public int getPuls() {
                return puls;
            }

            public void setPuls(int puls) {
                this.puls = puls;
            }

            public int getNegative() {
                return negative;
            }

            public void setNegative(int negative) {
                this.negative = negative;
            }
        }


    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static class Location implements Serializable {


        public Location() {
        }

        public Location(int left, int right, int top, int bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        public int  left;

        private int right;

        private int top;

        private int bottom;

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

        public int getBottom() {
            return bottom;
        }

        public void setBottom(int bottom) {
            this.bottom = bottom;
        }
    }


}
