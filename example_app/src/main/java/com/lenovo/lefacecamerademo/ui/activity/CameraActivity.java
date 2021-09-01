/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.lenovo.lefacecamerademo.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.firefly.api.callback.RecvWiegandCallBack;
import com.lenovo.lefacecamerademo.R;
import com.lenovo.lefacecamerademo.instructions.InstructionsConnect;
import com.lenovo.lefacecamerademo.ui.fragment.CameraFragment;

import com.firefly.api.HardwareCtrl;
import com.firefly.api.face.idcard.IDCardUtil;
//import com.firefly.api.face.nfc.NfcUtil;
import com.firefly.id_card.ICCardBean;
import com.firefly.id_card.IDCardBean;
import com.firefly.id_card.IDCardConfig;
import com.lenovo.lefacecamerademo.ui.ConfigClass;
import com.lenovo.lefacecamerademo.ui.fragment.HostSettingFragment;
import com.lenovo.lefacecamerademo.ui.fragment.SettingFragment;
import com.lenovo.lefacecamerademo.utils.MyContextWrapper;
import com.lenovo.lefacecamerademo.utils.MyDbHelper;
import com.lenovo.lefacecamerademo.utils.MyHardware;
import com.lenovo.lefacecamerademo.utils.MyUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Main {@code Activity} class for the Camera app.
 */
public class CameraActivity extends Activity implements IDCardUtil.IDCardCallBack {
    private IDCardUtil idCardUtil;
    //是否连接上后台服务
    private boolean isMachineConnect = false;
    private int mReadMode = IDCardConfig.READCARD_MODE_IC_CARD;
    private int mICcardType = IDCardConfig.ICCARD_TYPE_TYPEA;
    private FragmentManager fragmentManager;
    private CameraFragment cameraFragment;
    private SettingFragment settingFragment;
    private HostSettingFragment hostFragment;
    public InstructionsConnect instructionsConnect;

    //private MqttUtil mqttUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.jiagu.sdk.lefacerecognitionProtected.install(getApplication());
        setContentView(R.layout.activity_camera);
        //MyDbHelper.delDatabaseByName(this,"myfaces.db3");
        MyDbHelper.InitDb(new MyContextWrapper(this, "lecoodb"), "myfaces.db3");
        //用MyContextWrapper，或者在本Activity里面直接 @override MyContextWrapper里面的3个函数；
        Log.i("CameraActivity", "CameraActivity Thread.currentThread().getName()--" + Thread.currentThread().getName());

        //配置文件初始化
        ConfigClass.configInitMQTT(this);
        ConfigClass.configInitFunction(this);

        //服务--main线程处理 mqtt消息
        //FaceService.startService(this);
        //自创子线程处理 mqtt消息
        MyUtils.StartMqttClient(this);
        if (null == savedInstanceState) {
            cameraFragment = (CameraFragment) CameraFragment.newInstance();
            settingFragment = (SettingFragment) SettingFragment.getInstance();
            hostFragment = (HostSettingFragment) HostSettingFragment.getInstance();

            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.container, cameraFragment)
                    .add(R.id.container, settingFragment)
                    .add(R.id.container, hostFragment)
                    .hide(hostFragment)
                    .hide(cameraFragment)
                    .show(settingFragment)
                    .commit();


            fragmentManager.beginTransaction()
                    .hide(hostFragment)
                    .hide(settingFragment)
                    .show(cameraFragment)
                    .commit();
        }

        String hdver = HardwareCtrl.getHWVersion();
        Log.e("Hardware", "Hardware ver: " + hdver);
        //MyUtils.InitICCardReader((NfcUtil.OnNfcListener)this);
        //if (NfcUtil.getInstance().isSupport()) {
        //初始化nfc读卡
        //NfcUtil.getInstance().setOnNfcListener(this);
        //}
        idCardUtil = new IDCardUtil();
        //String hwver = MyHardware.getHWVersion();
        //485串口：/dev/ttyS4  ;  232串口：/dev/ttyS3
//        serialPort = HardwareCtrl.openSerialPortSignal(new File("/dev/ttyS3"), 115200, this);
//        serialPortS4 = HardwareCtrl.openSerialPortSignal(new File("/dev/ttyS4"), 115200, this);

        instructionsConnect = new InstructionsConnect();
        instructionsConnect.init();

        //韦根输入
        HardwareCtrl.openRecvMiegandSignal("/dev/wiegand");
        HardwareCtrl.recvWiegandSignal(new RecvWiegandCallBack() {
            @Override
            public void recvWiegandMsg(final int i) {
                Log.d("Wiegand-iccard", "recvWiegandMsg:" + i);
                String card_num = String.valueOf(i);
                if (card_num == null) {
                    return;
                }
                MyUtils.setWhiteLight(true, true);//刷卡,开灯
                doCheckCardNum(card_num);
            }
        });
        timer.schedule(task, 0, 1000); //1s一次
    }


    @Override
    public void onMachineConnect() {
        Log.d("iccard", "onMachineConnect");
        isMachineConnect = true;
        MyUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (idCardUtil.isSupportICCard()) {
                    mReadMode = IDCardConfig.READCARD_MODE_IC_CARD;
                    mICcardType = IDCardConfig.ICCARD_TYPE_TYPEA;
                    idCardUtil.setModel(mReadMode);
                    ;
                    idCardUtil.setICCardType(mICcardType);
                    if (mICcardType == IDCardConfig.ICCARD_TYPE_TYPEA) {
                        idCardUtil.setICCardEndianMode(true); //true: iccard-A的大端模式;
                    } else if (mICcardType == IDCardConfig.ICCARD_TYPE_TYPEB) {
                        idCardUtil.setICCardEndianMode(false); //false: iccard-B的小端模式
                    }
                }
            }
        });
    }

    @Override
    public void onModeChanged(int mode) {
        Log.d("iccard", "onModeChanged:" + mode);
    }

    //获取身份证信息
    @Override
    public void onSwipeIDCard(final IDCardBean info) {
        Log.d("iccard", "onSwipeICCard:" + info.getNum());
    }

    @Override
    public void onSwipeICCard(final ICCardBean info) {
        Log.d("iccard", "onSwipeICCard:" + info.getIcID());
        String card_num = info.getIcID();
        if (card_num == null) {
            return;
        }
        MyUtils.setWhiteLight(true, true);//刷卡,开灯
        doCheckCardNum(card_num);
    }

    @Override
    public void onSwipeIDCardUUID(final String uuid) {
        Log.d("iccard", "onSwipeIDCardUUID:" + uuid);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mIDcardUUID.setText(uuid);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        idCardUtil.startIDCardListener(this, this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //红外补光灯开
                MyUtils.setIrLight(true); //有异常,不知ok否???===todo
                //MyUtils.setWhiteLight(true, true);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        MyHardware.ctrlLedWhite(false);
//        timer.cancel();
        idCardUtil.stopIDCardListener(this);
        //红外补光灯关
        MyUtils.setIrLight(false);
        //关闭所有补光灯
        MyUtils.closeAllLight();
        HardwareCtrl.setBrightness(ConfigClass.Function.SCREEN_BRIGHTNESS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instructionsConnect.close();
        HardwareCtrl.closeRecvMiegandSignal();
    }

    //rs485/232发送信号后接收返回值
//    @Override
//    public void onDataReceived(byte[] bytes, int size) {
//        //String result = StringUtils.bytesToHexString(bytes, size);
//        if (size > 5 && bytes[0] == 0x02 && bytes[size - 1] == 0x03) {
//            String result = new String(bytes, 1, size - 2);
//            Log.d("serialport", "result = " + result);
//            MyUtils.setWhiteLight(true, true);//刷卡,开灯
//            doCheckCardNum(result);
//        }
//    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //if (NfcUtil.getInstance().handleEvent(event)) {
        if (idCardUtil.handleEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    protected void showToastTop(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    public void doCheckCardNum(final String card_nums) {
        Activity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "", card_num = "", user_id = "";
                boolean ok = false;
                String[] cards = card_nums.split("\r\n");  //分隔符 \r\n = 0A 0D
                for (String cardno : cards) {
                    if (cardno.length() > 1) {
                        card_num = cardno.substring(1);
                        String[] uinfo = MyDbHelper.Instance().getUserInfoByCardnum(card_num);
                        user_id = (uinfo == null ? "" : uinfo[0]);
                        String uname = (uinfo == null ? card_num : uinfo[1]);
                        message = String.format("刷卡-%s: %s", uinfo == null ? "失败" : "成功", uname);
                        if (uinfo != null) {
                            ok = true;
                            break;
                        }
                        ;
                    }
                }
                if (message == "") {
                    return; //
                }
                MyUtils.DoAuthorize(activity, ok, message);
                //save log
                MyDbHelper.Instance().addPassLog(new Object[]{
                        user_id, "C", ok ? "Y" : "N", ConfigClass.Function.DEVICE_ID, card_num,
                        null,
                        null,
                        0
                });

            }
        }).start();
    }
  /*
  //ICCard
  @Override
  public void onNfcSwipe(String code) {
    Log.e("iccard", "IC Card code:" + code);
  }
  */

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                MyUtils.setWhiteLight(false, false);
                MyUtils.setRedLight(false, false);
                MyUtils.setGreenLight(false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void toSetting() {
        HardwareCtrl.ctrlLedWhite(false);
        timer.cancel();
        idCardUtil.stopIDCardListener(this);
        //红外补光灯关
        MyUtils.setIrLight(false);
        //关闭所有补光灯
        MyUtils.closeAllLight();
        HardwareCtrl.setBrightness(255);
        fragmentManager.beginTransaction()
                .show(settingFragment)
                .hide(cameraFragment)
                .hide(hostFragment)
                .commit();

    }

    static String TAG = CameraActivity.class.getSimpleName();

    public void backToFace() {
        Log.d(TAG, "backToFace()");
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                try {
                    MyUtils.setWhiteLight(false, false);
                    MyUtils.setRedLight(false, false);
                    MyUtils.setGreenLight(false, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task, 0, 1000);

        idCardUtil.startIDCardListener(this, this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //红外补光灯开
                MyUtils.setIrLight(true); //有异常,不知ok否???===todo
                //MyUtils.setWhiteLight(true, true);
            }
        });
        fragmentManager.beginTransaction()
                .show(cameraFragment)
                .hide(settingFragment)
                .hide(hostFragment)
                .commit();
    }

    public void toHostSetting() {
        fragmentManager.beginTransaction()
                .show(hostFragment)
                .hide(settingFragment)
                .hide(cameraFragment)
                .commit();
    }

}
