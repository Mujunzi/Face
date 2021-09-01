package com.lenovo.lefacecamerademo.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.lenovo.lefacecamerademo.utils.MqttUtil;
import com.lenovo.lefacecamerademo.utils.MyUtils;

/**
 * CreateTime 2019/8/7 11:00
 * Author LiuShiHua
 * Description：
 */

public class FaceService extends IntentService {
    private Context context;

    public FaceService() {
        super("FaceService");
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, FaceService.class));
    }

    private final int HAND_PASS_LOG = 1;//
    //private final int HAND_SYNC_FACE = 2;//
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAND_PASS_LOG:
                    break;
                case 2:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Log.i("FaceService", "FaceService Thread.currentThread().getName()--" + Thread.currentThread().getName());
        MqttUtil.getInstance(context);
        //MyUtils.StartMqttClient(context);  //服务里面启动线程, 连接一次失败后不会重新连接?????
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onDestroy() {
//        stopSelf();
//        mqttUtil.disconnect();
        super.onDestroy();
    }
}

