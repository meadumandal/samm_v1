package com.umandalmead.samm_v1;

/**
 * Created by eleazerarcilla on 12/05/2018.
 */

public class Enums {
    public enum UIType{
        MAIN("Main", 0),
        SHOWING_ROUTES("ShowingRoutes", 1);

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
}
