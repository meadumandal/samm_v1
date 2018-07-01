package com.umandalmead.samm_v1;

/**
 * Created by eleazerarcilla on 12/05/2018.
 */

public class Enums {
    public enum UIType{
        MAIN("Main", 0),
        SHOWING_ROUTES("ShowingRoutes", 1),
        SHOWING_INFO("ShowingInfo", 2),
        HIDE_INFO("HideInfo", 3),
        HIDE_INFO_SEARCH("HideInfoSearch",4);

        private String stringValue;
        private int intValue;
        private UIType(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }
        @Override
        public String toString() {
            return stringValue;
        }

    }
    public enum GoogleMapType{
        MAP_TYPE_NORMAL("MAP_TYPE_NORMAL", 0),
        MAP_TYPE_SATELLITE("MAP_TYPE_SATELLITE", 1),
        MAP_TYPE_HYBRID("MAP_TYPE_HYBRID", 2),
        MAP_TYPE_TERRAIN("MAP_TYPE_TERRAIN", 3);

        private String stringValue;
        private int intValue;
        private GoogleMapType(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }
        @Override
        public String toString() {
            return stringValue;
        }

    }
    public enum ActionType{
        ADD("Add", 0),
        EDIT("Edit", 1),
        DELETE("Delete", 2),
        VIEW("View", 3);

        private String stringValue;
        private int intValue;
        private ActionType(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }
        @Override
        public String toString() {
            return stringValue;
        }
    }
}
