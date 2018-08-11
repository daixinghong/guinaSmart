package com.busradeniz.detection.bean;

import java.io.Serializable;

public class NewVersionBean implements Serializable {

    private String name;
    private String classify;
    private Location location;
    private Deviation deviation;
    private boolean status;

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
            private float puls;

            private float negative;

            public float getPuls() {
                return puls;
            }

            public void setPuls(float puls) {
                this.puls = puls;
            }

            public float getNegative() {
                return negative;
            }

            public void setNegative(float negative) {
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
        public float left;

        private float right;

        private float top;

        private float bottom;

        public float getLeft() {
            return left;
        }

        public void setLeft(float left) {
            this.left = left;
        }

        public float getRight() {
            return right;
        }

        public void setRight(float right) {
            this.right = right;
        }

        public float getTop() {
            return top;
        }

        public void setTop(float top) {
            this.top = top;
        }

        public float getBottom() {
            return bottom;
        }

        public void setBottom(float bottom) {
            this.bottom = bottom;
        }
    }


}
