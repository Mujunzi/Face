package com.lenovo.lefacecamerademo.ui;


import android.content.Context;

import com.lenovo.lefacecamerademo.utils.SharedPreferencesUtil;

public class ConfigClass {

    public static class MQTT {
        //MQTT相关配置
        public static String CLIENTID;
        public static String HOST;//服务器地址
        public static String USERNAME;//用户名
        public static String PASSWORD;//密码
        public static String PUBLISH_TOPIC;//PUBLISH_TOPIC
        public static String SUBSCRIBE_TOPIC;//SUBSCRIBE_TOPIC
        public static String RESPONSE_TOPIC;//RESPONSE_TOPIC
    }

    public static class Function {
        //功能设置
        public static int FACE_DETECT_MIN;//识别人脸大小
        public static String DEVICE_ID;//设备ID
        public static float SCORE_THRESH_FACE;//识别人脸匹配度
        public static float SCORE_THRESH_SpoofIR;//检测是否活体
        public static int LOG_IMG_WIDTH;//识别后得到人脸截图大小
        public static int FACE_FEATURE_INTERVAL;//识别间隔
        public static int SCREEN_BRIGHTNESS;//屏幕亮度
        public static boolean FILL_LAMP_RED;//补光灯红
        public static boolean FILL_LAMP_GREEN;//补光灯绿
        public static boolean FILL_LAMP_WHITE;//补光灯白
        public static boolean FILL_LAMP_INFRARED;//补光灯红外
        public static int FILL_LAMP_WHITE_BRIGHTNESS;//补光灯白亮度
        public static boolean TURN_ON_INFRARED_DETECTION;//补光灯白亮度
    }

    public class HostSetting {
        public static final int MODE_ONE = 1001;//单人模式
        public static final int MODE_MORE = 1002;//多人模式
        public static final int MAKE_OPENING = 1003;//触发开门
        public static final int MAKE_NOT_OPENING = 1004;//不触发开门
        public static final int DOOR_OPEN = 1005;//开门
        public static final int DOOR_CLOSE = 1006;//关门
        public static final int ALWAYS_OPEN = 1007;//常开
        public static final int NOT_ALWAYS_OPEN = 1008;//不常开
//        public static final int

    }

    public static void configInitMQTT(Context context) {
        //MQTT.CLIENTID
        MQTT.CLIENTID = SharedPreferencesUtil.getStringValue(context, "MQTT.CLIENTID", "AndroidUser_Leface_001");
        //MQTT.HOST
        MQTT.HOST = SharedPreferencesUtil.getStringValue(context, "MQTT.HOST", "tcp://192.168.1.111:1883");
        //MQTT.USERNAME
        MQTT.USERNAME = SharedPreferencesUtil.getStringValue(context, "MQTT.USERNAME", "admin");
        //MQTT.PASSWORD
        MQTT.PASSWORD = SharedPreferencesUtil.getStringValue(context, "MQTT.PASSWORD", "public");
        //MQTT.PUBLISH_TOPIC
        MQTT.PUBLISH_TOPIC = SharedPreferencesUtil.getStringValue(context, "MQTT.PUBLISH_TOPIC", "leface/pass/dev_id");
        //MQTT.SUBSCRIBE_TOPIC
        MQTT.SUBSCRIBE_TOPIC = SharedPreferencesUtil.getStringValue(context, "MQTT.SUBSCRIBE_TOPIC", "leface/syncuser/dev_id");
        //MQTT.RESPONSE_TOPIC
        MQTT.RESPONSE_TOPIC = SharedPreferencesUtil.getStringValue(context, "MQTT.RESPONSE_TOPIC", "leface/syncresp/dev_id");

    }

    public static void configInitFunction(Context context) {
        //Function.FACE_DETECT_MIN
        Function.FACE_DETECT_MIN = SharedPreferencesUtil.getIntValue(context, "Function.FACE_DETECT_MIN", 200);
        //Function.DEVICE_ID
        Function.DEVICE_ID = SharedPreferencesUtil.getStringValue(context, "Function.DEVICE_ID", "test001");
        //Function.SCORE_THRESH_FACE
        Function.SCORE_THRESH_FACE = SharedPreferencesUtil.getFloatValue(context, "Function.SCORE_THRESH_FACE", 0.75f);
        //Function.SCORE_THRESH_SpoofIR
        Function.SCORE_THRESH_SpoofIR = SharedPreferencesUtil.getFloatValue(context, "Function.SCORE_THRESH_SpoofIR", 0.6f);
        //Function.LOG_IMG_WIDTH
        Function.LOG_IMG_WIDTH = SharedPreferencesUtil.getIntValue(context, "Function.LOG_IMG_WIDTH", 400);
        //Function.FACE_FEATURE_INTERVAL
        Function.FACE_FEATURE_INTERVAL = SharedPreferencesUtil.getIntValue(context, "Function.FACE_FEATURE_INTERVAL", 3000);
        //Function.SCREEN_BRIGHTNESS
        Function.SCREEN_BRIGHTNESS = SharedPreferencesUtil.getIntValue(context, "Function.SCREEN_BRIGHTNESS", 255);
        //Function.FILL_LAMP_RED
        Function.FILL_LAMP_RED = SharedPreferencesUtil.getBooleanValue(context, "Function.FILL_LAMP_RED", true);
        //Function.FILL_LAMP_GREEN
        Function.FILL_LAMP_GREEN = SharedPreferencesUtil.getBooleanValue(context, "Function.FILL_LAMP_GREEN", true);
        //Function.FILL_LAMP_WHITE
        Function.FILL_LAMP_WHITE = SharedPreferencesUtil.getBooleanValue(context, "Function.FILL_LAMP_WHITE", true);
        //Function.FILL_LAMP_INFRARED
        Function.FILL_LAMP_INFRARED = SharedPreferencesUtil.getBooleanValue(context, "Function.FILL_LAMP_INFRARED", true);
        //Function.FILL_LAMP_WHITE_BRIGHTNESS
        Function.FILL_LAMP_WHITE_BRIGHTNESS = SharedPreferencesUtil.getIntValue(context, "Function.FILL_LAMP_WHITE_BRIGHTNESS", 7);
        //Function.TURN_ON_INFRARED_DETECTION
        Function.TURN_ON_INFRARED_DETECTION = SharedPreferencesUtil.getBooleanValue(context, "Function.TURN_ON_INFRARED_DETECTION", true);
    }
}
