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
        HIDE_INFO_SEARCH("HideInfoSearch",4),
        HIDE_SEARCH_FRAGMENT_ON_SEARCH("HideSearchFragmentOnSearch", 5),
        ADMIN_HIDE_MAPS_LINEARLAYOUT("AdminHideMapsLinearLayout",6),
        ADMIN_SHOW_MAPS_LINEARLAYOUT("AdminShowMapsLinearLayout",7),
        APPBAR_MIN_HEIGHT("AppBarMinHeight",8),
        SHOWING_NAVIGATION_DRAWER("ShowingNavigationDrawer",9),
        REPORT_ECOLOOP("ReportEcoLoop",10),
        REPORT_ROUNDS("ReportRounds",11),
        ASK_RATING("AskRating",12),
        SEARCHING_DISABLED("SearchingDisabled",13);

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
    public enum UserType{
        SAMM_DEFAULT("SAMMER (Guest)",0),
        SAMM_FACEBOOK("SAMMER (Facebook)",1),
        SAMM_ADMINISTRATOR("SAMMER (Administrator)",2),
        SAMM_DEFAULT_REGISTERED("Registered SAMMER",3),
        SAMM_DRIVER("SAMM Driver",4),
        SAMM_SUPERADMIN("SAMMER (Super Administrator)",5);
        private String stringValue;
        private int intValue;
        private UserType(String toString, int value){
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
           return stringValue;
        }

    }
    public enum InfoLayoutType{
        INFO_STATION("Station",0),
        INFO_VEHICLE("Vehicle",1),
        INFO_PERSON("Person",2);
        private String stringValue;
        private int intValue;
        private InfoLayoutType(String toString, int value){
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }
    public enum TutorialType{
        NONE("None", 0),
        MAP_LAYER_STYLE("MapLayerStyle",1);
        private String stringValue;
        private int intValue;
        private TutorialType(String toString, int value){
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
