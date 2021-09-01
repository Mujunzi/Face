package com.lenovo.lefacecamerademo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.firefly.api.HardwareCtrl;
import com.firefly.api.face.nfc.NfcUtil;
import com.lenovo.lefacecamerademo.ui.ConfigClass;
import com.lenovo.lefacesdk.Rect;

import java.util.HashMap;
import java.util.List;

public class MyUtils {
    //    public static int FACE_DETECT_MIN = ConfigClass.Function.FACE_DETECT_MIN; //识别人脸大小 cpb
//    public static String DEVICE_ID = ConfigClass.Function.DEVICE_ID; //cpb
//    public static float SCORE_THRESH_FACE = ConfigClass.Function.SCORE_THRESH_FACE; // 0.85f;识别人脸匹配度
//    public static float SCORE_THRESH_SpoofIR = ConfigClass.Function.SCORE_THRESH_SpoofIR; // 0.5f;识别是否活体
//    public static int LOG_IMG_WIDTH = ConfigClass.Function.LOG_IMG_WIDTH;//识别到人脸 截图大小
//    public static int FACE_FEATURE_INTERVAL = ConfigClass.Function.FACE_FEATURE_INTERVAL;  //3s 识别间隔
    public static java.util.LinkedHashMap<java.lang.String, float[]> userFaces = null;

    public static int getMaxFace(List<Rect> faces) {
        int idx = 0;
        int max_wid = ConfigClass.Function.FACE_DETECT_MIN;
        int size = faces.size();
        for (int i = 0; i < size; i++) {
            Rect rect = faces.get(i);
            if (rect.width > max_wid) {
                max_wid = rect.width;
                idx = i;
            }
        }
        return idx;
    }

    public static int getChkRectFace(List<Rect> faces, Rect frect) {
        if (faces.isEmpty()) {
            return -1;
        }
        int size = faces.size();
        int idx = -1;
        int max_wid = ConfigClass.Function.FACE_DETECT_MIN;
        android.graphics.Rect rect0 = new android.graphics.Rect();
        android.graphics.Rect rect1 = new android.graphics.Rect();
        rect1.set(frect.x, frect.y, frect.x + frect.width, frect.y + frect.height);
        float mj_max = 0, mj = 0;
        for (int i = 0; i < size; i++) {
            Rect rect = faces.get(i);
            rect0.set(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
            if (rect0.intersect(rect1)) {
                mj = rect0.width() * rect0.height();
                if (mj > mj_max) {
                    idx = i;
                    mj_max = mj;
                }
            }
        }
        if (mj_max < frect.width * frect.height / 2) {
            return -1;
        }
        return idx;
    }

    public static float ByteArryToFloat(byte[] bArray, int idx) {
        int accum = 0;
        accum = bArray[idx++] & 0xFF;
        accum |= (long) (bArray[idx++] & 0xFF) << 8;
        accum |= (long) (bArray[idx++] & 0xFF) << 16;
        accum |= (long) (bArray[idx++] & 0xFF) << 24;
        return Float.intBitsToFloat(accum);
    }

    public static void FloatToByteArray(byte[] bArray, int idx, float fValue) {
        int accum = Float.floatToRawIntBits(fValue);
        bArray[idx++] = (byte) (accum & 0xFF);
        bArray[idx++] = (byte) ((accum >> 8) & 0xFF);
        bArray[idx++] = (byte) ((accum >> 16) & 0xFF);
        bArray[idx++] = (byte) ((accum >> 24) & 0xFF);
    }

    public static float ByteArryToFloat2(byte[] bArray, int idx) {
        int accum = 0;
        accum |= (long) (bArray[idx++] & 0xFF) << 24;
        accum |= (long) (bArray[idx++] & 0xFF) << 16;
        accum |= (long) (bArray[idx++] & 0xFF) << 8;
        accum |= (long) (bArray[idx++] & 0xFF);
        return Float.intBitsToFloat(accum);
    }

    public static void FloatToByteArray2(byte[] bArray, int idx, float fValue) {
        int accum = Float.floatToRawIntBits(fValue);
        bArray[idx++] = (byte) ((accum >> 24) & 0xFF);
        bArray[idx++] = (byte) ((accum >> 16) & 0xFF);
        bArray[idx++] = (byte) ((accum >> 8) & 0xFF);
        bArray[idx++] = (byte) ((accum) & 0xFF);
    }

    //float数组转byte数组
    public static byte[] FloatArrayToByteArray(float[] fArray) {
        int fbytes = Float.BYTES;
        byte[] bArray = new byte[fArray.length * fbytes];
        for (int i = 0; i < fArray.length; i++) {
            FloatToByteArray2(bArray, fbytes * i, fArray[i]);
        }
        return bArray;
    }

    //byte数组转float数组
    public static float[] ByteArrayToFloatArray(byte[] bArray) {
        int fbytes = Float.BYTES;
        float[] fArray = new float[bArray.length / fbytes];
        for (int i = 0; i < fArray.length; i++) {
            fArray[i] = ByteArryToFloat2(bArray, fbytes * i);
        }
        return fArray;
    }

    public static HashMap<Bitmap, byte[]> CompressBmp2Bytes(Bitmap bmp, Rect rect) {
        HashMap<Bitmap, byte[]> bitmapHashMap = new HashMap<>();
        Bitmap bmp1 = CompressBmp(bmp, rect);
        bitmapHashMap.put(bmp1, BmpCompressUtils.getBytesByBitmap(bmp1, 100, Bitmap.CompressFormat.PNG));
        return bitmapHashMap;
    }

    public static Bitmap CompressBmp(Bitmap bmp, Rect rect) {//获取到图片
    /*    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String strTime = simpleDateFormat.format(date);

        //通过UUID生成字符串文件名
        String fileName = String.format("%s-%s",strTime,UUID.randomUUID().toString());
      */
        android.graphics.Rect rect0 = new android.graphics.Rect();
        rect0.set(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);

        Bitmap bmp1 = null;
        if (rect.width >= ConfigClass.Function.LOG_IMG_WIDTH) {
            float scale = ConfigClass.Function.LOG_IMG_WIDTH * 1.0f / rect.width;
            bmp1 = BmpCompressUtils.martix(BmpCompressUtils.ImageCropWithRect(bmp, rect0), scale);
        } else {
            int left = (int) (rect0.exactCenterX() - ConfigClass.Function.LOG_IMG_WIDTH / 2.0f);
            if (left < 0) left = 0;
            int top = (int) (rect0.exactCenterY() - ConfigClass.Function.LOG_IMG_WIDTH / 2.0f);
            if (top < 0) top = 0;
            int right = left + ConfigClass.Function.LOG_IMG_WIDTH;
            if (right > bmp.getWidth()) right = bmp.getWidth();
            int bottom = top + ConfigClass.Function.LOG_IMG_WIDTH;
            if (bottom > bmp.getHeight()) right = bmp.getHeight();
            android.graphics.Rect rect1 = new android.graphics.Rect();
            rect1.set(left, top, right, bottom);
            rect0.union(rect1);
            bmp1 = BmpCompressUtils.ImageCropWithRect(bmp, rect0);

        }
        return bmp1;
    }

    public static void SndNotify(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

    public static void SndAlert(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

    public static int InitICCardReader(NfcUtil.OnNfcListener onNfc) {
        if (NfcUtil.getInstance().isSupport()) {
            //初始化nfc读卡
            NfcUtil.getInstance().setOnNfcListener(onNfc);
            return 1;
        }
        return 0;
    }

    public static void runOnUiThread(Runnable run) {
        new Handler(Looper.getMainLooper()).post(run);
    }

    public static synchronized void closeAllLight() {
        HardwareCtrl.ctrlLedCloseAll();
    }

    public static int Red_Light_Keep_MS = 1500;
    public static long Red_offLight_ticks = 0;

    public static synchronized void setRedLight(boolean on, boolean force) {
        if (on) {
            Red_offLight_ticks = System.currentTimeMillis() + Red_Light_Keep_MS;
            //红色补光灯
            MyHardware.ctrlLedRed(true);
        } else {
            if (!force) {
                if (Red_offLight_ticks == 0 || System.currentTimeMillis() < Red_offLight_ticks) {
                    return;
                }
            }
            MyHardware.ctrlLedRed(false);
        }
    }

    public static int Green_Light_Keep_MS = 1500;
    public static long Green_offLight_ticks = 0;

    public static synchronized void setGreenLight(boolean on, boolean force) {
        if (on) {
            Green_offLight_ticks = System.currentTimeMillis() + Green_Light_Keep_MS;
            //红色补光灯
            MyHardware.ctrlLedGreen(true);
        } else {
            if (!force) {
                if (Green_offLight_ticks == 0 || System.currentTimeMillis() < Green_offLight_ticks) {
                    return;
                }
            }
            MyHardware.ctrlLedGreen(false);
        }
    }

    private static String TAG = MyUtils.class.getSimpleName();

    public static int White_Light_Keep_MS = 5000;
    public static long White_offLight_ticks = 0;

    public static synchronized void setWhiteLight(boolean on, boolean force) {
        Log.d(TAG, "setWhiteLight(boolean " + on + ", boolean " + force + ")");
        if (on) {
            White_offLight_ticks = System.currentTimeMillis() + White_Light_Keep_MS;
            //白色补光灯
            if (ConfigClass.Function.FILL_LAMP_WHITE == true) {
                MyHardware.ctrlLedWhite(true);
                int val = HardwareCtrl.getFillLightBrightnessMax();
                HardwareCtrl.writeFillLightBrightnessOptions(val);
            }
            //屏幕亮度
            HardwareCtrl.setBrightness(ConfigClass.Function.SCREEN_BRIGHTNESS);
        } else {
            if (!force) {
                if (White_offLight_ticks == 0 || System.currentTimeMillis() < White_offLight_ticks) {
                    return;
                }
            }
            MyHardware.ctrlLedWhite(false);
            HardwareCtrl.setBrightness(30);
        }
    }

    //红外补光灯关
    public static synchronized void setIrLight(boolean on) {
        if (ConfigClass.Function.FILL_LAMP_INFRARED == true) {
            HardwareCtrl.setInfraredFillLight(on); //有异常,不知ok否???===todo
        }
    }

    public static Thread mqttThread = null;

    public static void StartMqttClient(Context context) {
        if (mqttThread != null) {
            return;
        }
        mqttThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("mqttUtil", "mqttUtil Thread.currentThread().getName()--" + Thread.currentThread().getName());
                    Looper.prepare();
                    MqttUtil.getInstance(context);
                    //mqttUtil.publish(String.valueOf(ava));
                    //handler.sendEmptyMessageDelayed(HAND_100ms, 100);
                    Looper.loop();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        mqttThread.start();
    }

    public static void showToastTop(Context contex, final String s) {
        Log.e(TAG, "showToastTop()");
        MyUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(contex, s, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            }
        });
    }

    public static void DoOpenDoor() {
        //open door
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean sts = HardwareCtrl.getRelayValue();
                    HardwareCtrl.sendRelaySignal(!sts);
                    Thread.sleep(300);
                    HardwareCtrl.sendRelaySignal(sts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void DoAuthorize(Activity activity, boolean ok, final String msg) {
        if (ok) {
            MyUtils.DoOpenDoor();
            MyUtils.SndNotify(activity);
            MyUtils.setGreenLight(true, false);
        } else {
            MyUtils.SndAlert(activity);
            MyUtils.setRedLight(true, false);
        }
        MyUtils.showToastTop(activity, msg);

    }

}