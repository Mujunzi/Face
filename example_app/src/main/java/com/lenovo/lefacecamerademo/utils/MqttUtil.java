package com.lenovo.lefacecamerademo.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lenovo.lefacecamerademo.ui.ConfigClass;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * CreateTime 2019/8/8 16:11
 * Author LiuShiHua
 * Description：
 */

public class MqttUtil {
    private final String TAG = "------------->mqtt";
    private static MqttUtil mqttUtil;
    private Context context;

    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    private boolean isConnectSuccess = false, isConnectionLost = true;
    //MQTT相关配置
//    private final String CLIENTID = ConfigClass.MQTT.CLIENTID;
//    public String HOST = ConfigClass.MQTT.HOST;//服务器地址（协议+地址+端口号） cpb
//    public String USERNAME = ConfigClass.MQTT.USERNAME;//用户名 cpb
//    public String PASSWORD = ConfigClass.MQTT.PASSWORD;//密码 cpb
//    public static String PUBLISH_TOPIC = ConfigClass.MQTT.PUBLISH_TOPIC;//发布主题 cpb
//    public static String SUBSCRIBE_TOPIC = ConfigClass.MQTT.SUBSCRIBE_TOPIC;//发布主题
//    public static String RESPONSE_TOPIC = ConfigClass.MQTT.RESPONSE_TOPIC;//订阅主题
    /**
     * QUALITY_OF_SERVICE
     * 至多一次，消息发布完全依赖底层 TCP/IP 网络。会发生消息丢失或重复。这一级别可用于如下情况，环境传感器数据，丢失一次读记录无所谓，因为不久后还会有第二次发送。
     * 至少一次，确保消息到达，但消息重复可能会发生。
     * 只有一次，确保消息到达一次。这一级别可用于如下情况，在计费系统中，消息重复或丢失会导致不正确的结果
     */
    private final int QUALITY_OF_SERVICE = 1; //0;//服务质量,0最多一次，1最少一次，2只一次


    private final int HAND_FACE_SYNC = 101;//
    private final int HAND_RECONNECT = 1;//重连hand
    private final int RECONNECT_TIME_CONFIG = 5 * 1000;//重连时间间隔为10秒
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "mqtt-handleMessage Thread.currentThread().getName()--" + Thread.currentThread().getName());
            switch (msg.what) {
                case HAND_FACE_SYNC:
                    //==todo==
                    //add face data(id,name,feat) to map and db
                    try {
                        String jsonText = new String(msg.getData().getByteArray("json"));

                        JSONTokener t = new JSONTokener(jsonText);
                        JSONObject obj = (JSONObject) t.nextValue();
                        // 应该这样判断json是否为null
                        String juserid = "", jusername = "", jcardnum = "";
                        if (obj.has("user_id")) {
                            juserid = obj.get("user_id").toString();
                        }
                        if (obj.has("user_name")) {
                            jusername = obj.get("user_name").toString();
                        }
                        if (obj.has("card_num")) {
                            jcardnum = obj.get("card_num").toString();
                        }
                        JSONArray jfeature = null;
                        if (obj.has("face_feature")) {
                            try {
                                jfeature = (JSONArray) obj.get("face_feature");
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        Log.i(TAG, "HAND_FACE_SYNC:" + " uid:" + juserid.toString() + ", uname:" + jusername.toString() + ", card:"
                                + jcardnum.toString() + ", feas:" + (jfeature == null ? "" : jfeature.length()));

                        if (juserid.isEmpty()) {
                            break;
                        }
                        if (jusername.isEmpty()) {
                            jusername = juserid;
                        }
                        //
                        if (!jcardnum.isEmpty()) {
                            MyDbHelper.Instance().addOrUpdUserCard(juserid, jusername, jcardnum);
                        }
                        if (jfeature != null && jfeature.length() == 256) {
                            float[] feas = new float[jfeature.length()];
                            for (int i = 0; i < feas.length; i++) {
                                feas[i] = Float.valueOf(jfeature.get(i).toString());
                            }
                            MyDbHelper.Instance().addOrUpdUserFace(juserid, jusername, MyUtils.FloatArrayToByteArray(feas));
                            if (MyUtils.userFaces != null) {
                                MyUtils.userFaces.put(juserid, feas);
                                Log.i(TAG, " addOrUpdUserFace当前人脸个数:" + MyUtils.userFaces.size());
                            }
                        }
                        response("message handled: user_id=" + juserid);
                        //
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case HAND_RECONNECT:
                    if (!isConnectSuccess)
                        doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
                    break;
            }
        }
    };

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            isConnectSuccess = true;
            try {
                mqttAndroidClient.subscribe(ConfigClass.MQTT.SUBSCRIBE_TOPIC, QUALITY_OF_SERVICE);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "onFailure 连接失败:" + arg1.getMessage());
            isConnectSuccess = false;
            handler.sendEmptyMessageDelayed(HAND_RECONNECT, RECONNECT_TIME_CONFIG);
        }
    };

    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "收到消息： " + new String(message.getPayload()) + "\tToString:" + message.toString());
            //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
            //response("message arrived:"+message);
            //handler.sendEmptyMessageDelayed(HAND_FACE_SYNC, RECONNECT_TIME_CONFIG);
            Message msg = Message.obtain();
            msg.what = HAND_FACE_SYNC;
            Bundle bundle = new Bundle();
            bundle.putByteArray("json", message.getPayload());
            msg.setData(bundle);
            handler.sendMessageDelayed(msg, 10);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i(TAG, "deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开");
            doClientConnection();//连接断开，重连
        }
    };

    //单例模式
    public static MqttUtil getInstance(Context context) {
        if (mqttUtil == null) {
            mqttUtil = new MqttUtil(context);
        }
        return mqttUtil;
    }

    private MqttUtil(Context context) {
        this.context = context;
        initMqtt();
    }

    private void initMqtt() {
        String serverURI = ConfigClass.MQTT.HOST; //服务器地址（协议+地址+端口号）
        mqttAndroidClient = new MqttAndroidClient(context, serverURI, ConfigClass.MQTT.CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback); //设置订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(20); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(ConfigClass.MQTT.USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(ConfigClass.MQTT.PASSWORD.toCharArray()); //设置密码

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + ConfigClass.MQTT.CLIENTID + "\"}";
        String topic = ConfigClass.MQTT.PUBLISH_TOPIC;
        Integer qos = QUALITY_OF_SERVICE;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            try {
                mMqttConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "setWill Exception Occured:" + e.getMessage(), e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            Log.d(TAG, "mMqttConnectOptions.setWill Success");
            doClientConnection();
        }
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        Log.d(TAG, "是否链接成功：" + mqttAndroidClient.isConnected());
        if (!mqttAndroidClient.isConnected()) { // && Tools.isInternetConnect(context)) {
            try {
                mqttAndroidClient.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                Log.d(TAG, "doClientConnection:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 发布消息
     *
     * @param message 消息
     */
    public void publish(String message) {
        String topic = ConfigClass.MQTT.PUBLISH_TOPIC;
        Integer qos = QUALITY_OF_SERVICE;
        Boolean retained = false;
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
                mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } else {
                Log.d(TAG, "mqttAndroidClient is Null");
            }
        } catch (MqttException e) {
            Log.d(TAG, "publish MqttException:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void response(String message) {
        String topic = ConfigClass.MQTT.RESPONSE_TOPIC;
        Integer qos = QUALITY_OF_SERVICE;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            Log.d(TAG, "publish:" + e.getMessage());
            e.printStackTrace();
        }
    }

    //断开链接
    public void disconnect() {
        try {
            if (mqttAndroidClient != null)
                mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

