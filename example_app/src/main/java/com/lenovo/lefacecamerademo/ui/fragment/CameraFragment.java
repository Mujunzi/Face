package com.lenovo.lefacecamerademo.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v13.app.FragmentCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.firefly.api.HardwareCtrl;
import com.lenovo.lefacecamerademo.ui.CircleImageView;
import com.lenovo.lefacecamerademo.ui.activity.CameraActivity;
import com.lenovo.lefacecamerademo.R;
import com.lenovo.lefacecamerademo.camera.CameraListener;
import com.lenovo.lefacecamerademo.camera.DualCameraHelper;
import com.lenovo.lefacecamerademo.ui.ConfigClass;
import com.lenovo.lefacecamerademo.utils.*;
import com.lenovo.lefacecamerademo.widget.AutoFitTextureView;
import com.lenovo.lefacecamerademo.widget.OverlayView;
import com.lenovo.lefacesdk.IdentificationResult;
import com.lenovo.lefacesdk.LefaceEngine;
import com.lenovo.lefacesdk.Rect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CameraFragment extends BaseFragment {
    private AutoFitTextureView textureViewIR, textureViewRGB;
    private OverlayView overlayView;
    private static LefaceEngine mEngine;
    private LefaceEngine.InitializationResult mInitResult;
    private static final String ACCESS_KEY =
            "92bd284b5f2d73bab0a40df3de0ed4106d906e1eb467af45a13e0d43a0924528";
    private static final String SECRET_KEY =
            "173ced25e826c85745ccb13060580e4cb1a2a62d2267e16f2e4c1f00695d8af7";
    private DualCameraHelper cameraHelperIR;
    private DualCameraHelper cameraHelperRGB;
    private final String TAG = "CameraFragment";
    private final int CAMERA_REQUEST_CODE = 1001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //
    public String userid = "";
    public long next_feat_ticks = 0;
    private volatile boolean isSpoofIR = true;
    private volatile boolean needReconize = true;
    private LinearLayout setting;
    private int setBTNClickTime = 0;
    private CameraActivity activity;
    private TextClock clock;
    private ConstraintLayout face;
    private CircleImageView faceImage;
    private Bitmap bmp;
    private TextView faceText;
    private Button jump;
    private boolean isToSetting = false;

    public static android.app.Fragment newInstance() {
        return new CameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //PadLightUtils.setIRLight();
        initView(view);
        sysSetting();
        initOverlayView();
        requestPermission();
    }

    private void initView(View view) {
        textureViewIR = (AutoFitTextureView) view.findViewById(R.id.texture_IR);
        textureViewRGB = (AutoFitTextureView) view.findViewById(R.id.texture_RGB);
        overlayView = (OverlayView) view.findViewById(R.id.overlay);
        setting = (LinearLayout) view.findViewById(R.id.btn_sys_setting);
        clock = (TextClock) view.findViewById(R.id.clock);
        face = (ConstraintLayout) view.findViewById(R.id.face);
        faceImage = (CircleImageView) view.findViewById(R.id.face_image);
        faceText = (TextView) view.findViewById(R.id.face_text);

        jump = (Button) view.findViewById(R.id.jump);

        final int[] a = {3};

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a[0] % 3 == 0) {
                    textureViewIR.setVisibility(View.INVISIBLE);
                    textureViewRGB.setVisibility(View.VISIBLE);
                    jump.setText("RGB");
                } else if (a[0] % 3 == 1) {
                    textureViewIR.setVisibility(View.VISIBLE);
                    textureViewRGB.setVisibility(View.INVISIBLE);
                    jump.setText("IR");
                } else if (a[0] % 3 == 2) {
                    textureViewIR.setVisibility(View.VISIBLE);
                    textureViewRGB.setVisibility(View.VISIBLE);
                    jump.setText("正常");
                }
                a[0]++;

//                Log.e("SERIAL", "onClick: " + android.os.Build.SERIAL);
            }
        });

        activity = (CameraActivity) getActivity();
    }

    //设置按钮参数
    public boolean begin = false;//是否开始点击
    public int a;//时间秒数，5秒内没有操作进入设置就从头开始
    public ClickThread clickThread;//操作计时线程
    public int time = 0;//点击次数

    private void sysSetting() {
        //时间控件点击事件
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "吐司", Toast.LENGTH_SHORT).show();
                //点击时begin为true说明开始点击
                begin = true;
                time++;//每点击一次次数增加一次
                Log.e(TAG, "onClick: " + time);
                if (clickThread == null && begin == true) {
                    clickThread = new ClickThread();
                    clickThread.start();
                }
                if (time == 5) {
                    Toast.makeText(getActivity(), "设置", Toast.LENGTH_SHORT).show();
                    popupWindow();
                    clickThread = null;//清除线程方便下一次操作
                    begin = false;
                    time = 0;
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            isToSetting = true;
        } else {
            isToSetting = false;
        }
    }

    private void popupWindow() {
        isToSetting = true;
        //关闭所有补光灯
        MyUtils.closeAllLight();
        HardwareCtrl.setBrightness(255);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_layout, null, false);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(setting, Gravity.CENTER, 0, 0);

        view.findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        isToSetting = false;
                    }
                });

        view.findViewById(R.id.btn_determine)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        activity.toSetting();
                    }
                });
    }

    class ClickThread extends Thread {

        @Override
        public void run() {
            super.run();
            a = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    Log.e(TAG, "run: " + a++);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //如果时间超过五秒时没有进入到设置界面就从头开始操作
                if (a == 5) {
                    clickThread = null;//清除线程方便下一次操作
                    begin = false;
                    time = 0;
                    break;
                }
            }
        }
    }

    private void initOverlayView() {
        //overlayView.setZOrderOnTop(true);
        //overlayView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    private void requestPermission() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            FragmentCompat.requestPermissions(this, NEEDED_PERMISSIONS, CAMERA_REQUEST_CODE);
            return;
        }
        initLefaceModel();

    }

    private void initLefaceModel() {
        try {
            if (faceLoop == null) {
                faceLoop = new Thread(face_run);
                faceLoop.start();
            }
            if (MyUtils.userFaces == null) {
                MyUtils.userFaces = MyDbHelper.Instance().loadAllFaces();
            }

            if (mEngine == null) {
                mEngine = new LefaceEngine(getActivity(), ACCESS_KEY, SECRET_KEY);
                mInitResult = mEngine.initialize();
                Log.d(TAG, "engine initialization result:" + mInitResult.toString());
                if (mInitResult == LefaceEngine.InitializationResult.SUCCESS) {
                    mEngine.initializeIRSpoofDetector();
                    mEngine.initializeDetector();
                    mEngine.initializeRecognizer();
                    mEngine.setNumThreads(4);
                    //mEngine.setUseGPU(true);
                    new Thread(camera_run).start();
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to initialize an image classifier.", e);
        }
    }

    private void initRGBCamera() {
        cameraHelperRGB = new DualCameraHelper.Builder()
                .previewViewSize(
                        new Point(textureViewRGB.getMeasuredWidth(), textureViewRGB.getMeasuredHeight()))
                .rotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .previewOn(textureViewRGB)
                .cameraListener(cameraListenerRGB)
                .isMirror(false)
                .build();
        cameraHelperRGB.init();
        try {
            cameraHelperRGB.start();
        } catch (RuntimeException e) {
            Log.e(TAG, "initRGBCamera-->" + e.getMessage());
        }
    }

    private void initIRCamera() {
        cameraHelperIR = new DualCameraHelper.Builder()
                .previewViewSize(
                        new Point(textureViewIR.getMeasuredWidth(), textureViewIR.getMeasuredHeight()))
                .rotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .previewOn(textureViewIR)
                .cameraListener(cameraListenerIR)
                .isMirror(false)
                .build();
        cameraHelperIR.init();
        try {
            cameraHelperIR.start();
        } catch (RuntimeException e) {
            Log.e(TAG, "initIRCamera-->" + e.getMessage());
        }
    }

    //先检测RGB人脸
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Rect detectFace(Bitmap bmpRgb, Bitmap bmpIR) {
        //lock.lock();
        long start = System.currentTimeMillis();
        List<Rect> results = null;
        final List<Float>[] IRdetectList = new List[]{null};
        final List<Rect>[] detectFaceList = new List[]{null};
        HashMap<List<Float>, List<Rect>> IRdetectHashMap = null;
        Rect faceRect = null;
        Thread irLoop = null;
        try {
            results = mEngine.detectFace(bmpRgb, 20);
            if (results.isEmpty()) {
                overlayView.clearOverlay();
                overlayView.setUserInfo("");
                if (irLoop != null) irLoop.interrupt();
                return null;
            }
            //查找最大face
            int idx_max = MyUtils.getMaxFace(results);
            faceRect = results.get(idx_max);
            //overlayView.drawOverlay(isSpoofIR?OverlayView.RED:OverlayView.GREEN);
            if (isSpoofIR) {
                overlayView.setUserInfo("");
                overlayView.clearOverlay();
            } else {
                overlayView.setDetectionRect(faceRect);
                overlayView.drawOverlay(OverlayView.GREEN);
            }
            Log.e(TAG, "results-->" + Arrays.toString(results.toArray()));
            //spoofIR check
            if (irLoop == null) {
//                IRdetectHashMap = mEngine.detectSpoofIRyinghuochong(bmpIR);//活体检测
            } else {
                irLoop.join();  //等待活体检测线程结束
            }
            long end = System.currentTimeMillis() - start;
            Log.e(TAG, "SpoofIR time-" + end);
            if (IRdetectHashMap.size() < 1) {
                return null;
            }
            IRdetectHashMap.forEach((k, v) -> {
                IRdetectList[0] = k;
                detectFaceList[0] = v;
            });
            Log.e(TAG, "IRdetectList-->" + Arrays.toString(IRdetectList[0].toArray()));

            boolean isSpoofIRNew = true;
            boolean isIROk = true;
            for (int i = 0; i < IRdetectList[0].size(); i++) {
                float f = IRdetectList[0].get(i);
                if (f < 0) {
                    continue;
                }
                isIROk = true;
                if (f > ConfigClass.Function.SCORE_THRESH_SpoofIR) {
                    isSpoofIRNew = false;
                    MyUtils.setWhiteLight(true, true);//有活体,开灯
                    break;
                }
            }

            if (isIROk && isSpoofIRNew != isSpoofIR) { //活体值有变化
                isSpoofIR = isSpoofIRNew;
                //overlayView.drawOverlay(isSpoofIR ? OverlayView.RED : OverlayView.GREEN);
                if (!isSpoofIR) {  //活体
                    overlayView.setDetectionRect(faceRect);
                    overlayView.drawOverlay(OverlayView.GREEN);
                }
            }

            if (!isIROk  //ir无人脸
                    || isSpoofIR  //非活体
                    || next_feat_ticks > System.currentTimeMillis() //检查间隔时间
            ) {
                if (bmpRgb != null) bmpRgb.recycle();
                return null;
            }
            next_feat_ticks = System.currentTimeMillis() + ConfigClass.Function.FACE_FEATURE_INTERVAL;
            Log.d(TAG, "detectFaceIr: " + ConfigClass.Function.FACE_FEATURE_INTERVAL);
            return faceRect;
        } catch (Exception e) {
            e.printStackTrace();
            return faceRect;
        } finally {
            if (irLoop != null) irLoop.interrupt();

            //lock.unlock();
            /** Need to recycle bitmap **/
            //if(bitmap!=null) bitmap.recycle();
            if (bmpIR != null) bmpIR.recycle();
        }

    }

    //先检测IR活体人脸
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Rect detectFaceIr(Bitmap bmpRgb, Bitmap bmpIR) {
        //lock.lock();
        long start = System.currentTimeMillis();
        List<Rect> results = null;
        HashMap<List<Float>, List<Rect>> IRdetectHashMap = null;
        final List<Float>[] IRdetectList = new List[]{null};
        final List<Rect>[] detectFaceList = new List[]{null};
        Rect faceRect = null;
        Thread irLoop = null;
        isSpoofIR = true;
        try {
            IRdetectHashMap = mEngine.detectSpoofIRyinghuochong(bmpIR);//活体检测
            boolean isSpoofIRNew = true;
            boolean isIROk = false;
            if (!IRdetectHashMap.isEmpty()) {
                IRdetectHashMap.forEach((k, v) -> {
                    IRdetectList[0] = k;
                    detectFaceList[0] = v;
                });
                Log.e(TAG, "IRdetectList-->" + Arrays.toString(IRdetectList[0].toArray()));
                int max_wid = ConfigClass.Function.FACE_DETECT_MIN;
                for (int i = 0; i < IRdetectList[0].size(); i++) {
                    float f = IRdetectList[0].get(i);
                    if (f < 0) {
                        continue;
                    }
                    isIROk = true;
                    if (f > ConfigClass.Function.SCORE_THRESH_SpoofIR) {
                        Rect rt = detectFaceList[0].get(i);
                        if (rt.width > max_wid) {
                            faceRect = rt;
                            isSpoofIRNew = false;
                        }
                        MyUtils.setWhiteLight(true, true);//有活体,开灯
                        Log.d(TAG, "detectFaceIr: ");
                    }
                }
            }
            if (isIROk) {
                isSpoofIR = isSpoofIRNew;
            }
            if (isSpoofIR) {  //非活体
                overlayView.setUserInfo("");
                overlayView.clearOverlay();
            } else { //活体
                overlayView.setDetectionRect(faceRect);
                overlayView.drawOverlay(OverlayView.GREEN);
            }
            if (!isSpoofIR) {
                //rgb 人脸检测
                results = mEngine.detectFace(bmpRgb, 20);
                Log.e(TAG, "results-->" + Arrays.toString(results.toArray()));
                long end = System.currentTimeMillis();
                Log.e(TAG, "SpoofIR time-" + (end - start));
                //查找最大face
                int idx_max = MyUtils.getChkRectFace(results, faceRect);
                faceRect = (idx_max < 0 ? null : results.get(idx_max));
                if (faceRect == null) {
                    overlayView.clearOverlay();
                }
            }
            if (!isIROk  //ir无人脸
                    || isSpoofIR  //非活体
                    || (faceRect == null)  //无rgb人脸对应
                    || next_feat_ticks > System.currentTimeMillis() //检查间隔时间
            ) {
                if (bmpRgb != null) bmpRgb.recycle();
                return null;
            }
            next_feat_ticks = System.currentTimeMillis() + ConfigClass.Function.FACE_FEATURE_INTERVAL;
            Log.d(TAG, "detectFaceIr: " + ConfigClass.Function.FACE_FEATURE_INTERVAL);
            return faceRect;
        } catch (Exception e) {
            e.printStackTrace();
            return faceRect;
        } finally {
            if (irLoop != null) irLoop.interrupt();

            //lock.unlock();
            /** Need to recycle bitmap **/
            //if(bitmap!=null) bitmap.recycle();
            if (bmpIR != null) bmpIR.recycle();
        }

    }

    /*
    private boolean spoofIRCheck(Bitmap bitmap, Bitmap bitmapIR, Rect faceRect)
    {
      //lock1.lock();
      try {
        long start = System.currentTimeMillis();
        List<Float> IRdetectList = mEngine.detectSpoofIRyinghuochong(bitmapIR);//活体检测
        //List<Float> IRdetectList = new ArrayList<Float>();
        Log.e(TAG, "IRdetectList-->" + Arrays.toString(IRdetectList.toArray()));
        //float[] IRdetectArr = mEngine.detectSpoofIR(bitmapIR);//活体检测
        //Log.e(TAG, "IRdetectList-->" + Arrays.toString(IRdetectArr));
        boolean isSpoofIRNew = true;
        isIROk = false;
        for (float f : IRdetectList) {
          //if(IRdetectList.size() == results.size()) {
          //  float f = IRdetectList.get(idx_max);
          //if(IRdetectArr.length == results.size()) {
          //  float f = IRdetectArr[idx_max];
          if (f < 0) {
            continue;
          }
          isIROk = true;
          long end = System.currentTimeMillis() - start;
          Log.e("SpoofIR", "ir_fval="+f+", time-" + end);
          if (f > MyUtils.SCORE_THRESH_SpoofIR) {
            isSpoofIRNew = false;
            break;
          }
        }
        if (isIROk && isSpoofIRNew != isSpoofIR) { //活体值有变化
          isSpoofIR = isSpoofIRNew;
          //overlayView.drawOverlay(isSpoofIR ? OverlayView.RED : OverlayView.GREEN);
          if(!isSpoofIR) {  //活体
            overlayView.setDetectionRect(faceRect);
            overlayView.drawOverlay(OverlayView.GREEN);
          }
        }

        if(!isIROk  //ir无人脸
          || isSpoofIR  //非活体
          || next_feat_ticks > System.currentTimeMillis() //检查间隔时间
        ) {
          if(bitmap!=null) bitmap.recycle();
          return false;
        }
        //saveBitmap(bitmap, bitmapIR);

        next_feat_ticks = System.currentTimeMillis() + MyUtils.FACE_FEATURE_INTERVAL;
        return true;
      }
      catch (Exception e) {
        e.printStackTrace();
        return false;
      } finally {
        //lock1.unlock();
        //Need to recycle bitmap
        //if(bitmap!=null) bitmap.recycle();
        if(bitmapIR!=null) bitmapIR.recycle();
      }
    }
    */
    private void doRecognizeFace(Bitmap bitmap, Rect faceRect) {
        Log.d(TAG, "doRecognizeFace()");
        long start = System.currentTimeMillis();
        try {
            //特征值提取
            //List<Rect> farrRect = new ArrayList<Rect>();
            //farrRect.add(faceRect);
            List<float[]> emblist = mEngine.featExtract(bitmap, new ArrayList<Rect>() {{
                add(faceRect);
            }});
            if (emblist.size() < 1 || MyUtils.userFaces.size() < 1) {
                Log.e("featExtract", "featExtract no features");
                return;
            }


            HashMap<Bitmap, byte[]> bitmapHashMap = MyUtils.CompressBmp2Bytes(bitmap, faceRect);
            Set<Bitmap> set = bitmapHashMap.keySet();
            for (Bitmap bitSet : set) {
                bmp = bitSet;
            }

            float[] fea = emblist.get(0);
            IdentificationResult id_result = mEngine.identifyFace(fea, MyUtils.userFaces);
            String msg = "";
            boolean ok = false;
            if (id_result.confidence >= ConfigClass.Function.SCORE_THRESH_FACE) {
                userid = id_result.id;
                //pose += String.format("[%s, %.2f, %d ms]", id_result.id, id_result.confidence, detectEndTime2-detectEndTime1);
                String[] uinfo = MyDbHelper.Instance().getUserInfoByUserId(userid, "face");
                ok = true;

                msg = String.format("刷脸成功: %s, %f", uinfo == null ? userid : uinfo[1], id_result.confidence);
                faceText.setText("刷脸成功");

            } else {
                msg = String.format("刷脸失败: %s, %f", id_result.id, id_result.confidence);
                faceText.setText("刷脸失败");

            }

            //overlayView.setUserInfo(msg);
            MyUtils.DoAuthorize(getActivity(), ok, msg);
            //save face image

            MyUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    face.setVisibility(View.VISIBLE);
                    faceImage.setImageBitmap(bmp);
                }
            });

            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, "保存成功！", Toast.LENGTH_SHORT).show();
                    saveBitmap(bmp);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(2000);
                            MyUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    face.setVisibility(View.GONE);
                                }
                            });
                            break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            Log.d(TAG, "faceImage.setVisibility(View.VISIBLE);");

    /*
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss
    String path =
            getActivity().getFilesDir() + "/images/" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + File.separator;
    BitmapUtils.saveBitmap(path, String.format("face_%d.jpg",System.currentTimeMillis()), MyUtils.CompressBmp(bitmap, faceRect));

     */
            //save log
            MyDbHelper.Instance().addPassLog(new Object[]{
                    id_result.id, "F", ok ? "Y" : "N", ConfigClass.Function.DEVICE_ID, "",
                    bitmapHashMap.get(bmp),
                    MyUtils.FloatArrayToByteArray(fea),
                    id_result.confidence
            });
            long end = System.currentTimeMillis();
            Log.e(TAG, "doRecognizeFace time-" + (end - start));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (bitmap != null) bitmap.recycle();
        }
    }

    public void saveBitmap(Bitmap bmp) {
        Log.e(TAG, "保存图片");
        File f = new File("/storage/emulated/0", "face.jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveBitmap(Bitmap bitmap, Bitmap bitmapIR) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss
        String path =
                getActivity().getFilesDir() + "/images/" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + File.separator;
        BitmapUtils.saveBitmap(path, String.format("RGB_%d.jpg", System.currentTimeMillis()), bitmap);
        BitmapUtils.saveBitmap(path, String.format("IR_%d.jpg", System.currentTimeMillis()), bitmapIR);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void doDetctFace(String camera_flag) {
        Bitmap bitmap, bitmapIR;
        Rect rect = null;
        synchronized (this) {
            bitmap = textureViewRGB.getBitmap();
            bitmapIR = textureViewIR.getBitmap();
        }
        if (camera_flag == "ir") {
            rect = detectFaceIr(bitmap, bitmapIR);
        }
        if (camera_flag == "rgb") {
            rect = detectFace(bitmap, bitmapIR);
        }
        if (rect == null) {
            return;
        }
        if (faceLoop != null && faceLoop.isAlive()) {
            synchronized (this) {
                if (lstFaceBmp.size() < 2) {
                    lstFaceBmp.add(bitmap);
                    lstFaceRect.add(rect);
                } else {
                    bitmap.recycle();
                }
            }
            return;
        }

        //启动线程,使得画框快些
        Rect finalRect = rect;
        new Thread(new Runnable() {
            @Override
            public void run() {
                doRecognizeFace(bitmap, finalRect);
            }
        }).start();

    }

    CameraListener cameraListenerRGB = new CameraListener() {
        @Override
        public void onCameraOpened(Camera camera, int cameraId, int displayOrientation,
                                   boolean isMirror) {

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onPreview(byte[] data, Camera camera) {
            //Log.e("lc", " Thread.currentThread().getName()--" + Thread.currentThread().getName());
//            doDetctFace("rgb");
        }

        @Override
        public void onCameraClosed() {

        }

        @Override
        public void onCameraError(Exception e) {

        }

        @Override
        public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

        }
    };

    CameraListener cameraListenerIR = new CameraListener() {
        @Override
        public void onCameraOpened(Camera camera, int cameraId, int displayOrientation,
                                   boolean isMirror) {

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onPreview(byte[] data, Camera camera) {
//            if (ConfigClass.Function.TURN_ON_INFRARED_DETECTION == true) {
            if (isToSetting == false) {
                doDetctFace("ir");
            }
//            }
        }

        @Override
        public void onCameraClosed() {

        }

        @Override
        public void onCameraError(Exception e) {

        }

        @Override
        public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        try {
      /*
      if (cameraHelperIR != null) {
        cameraHelperIR.start();
      }
      if (cameraHelperRGB != null) {
        cameraHelperRGB.start();
      }*/
            requestPermission();
        } catch (RuntimeException e) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraHelperIR != null) {
            cameraHelperIR.stop();
        }
        if (cameraHelperRGB != null) {
            cameraHelperRGB.stop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (cameraHelperIR != null) {
            cameraHelperIR.release();
            cameraHelperIR = null;
        }
        if (cameraHelperRGB != null) {
            cameraHelperRGB.release();
            cameraHelperRGB = null;
        }
        try {
            mEngine.close();
        } catch (Exception ex) {
            mEngine = null;
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void afterRequestPermission(int requestCode, boolean isAllGranted) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (isAllGranted) {
                initLefaceModel();

            } else {
                showToast("权限被拒绝！");
            }
        }
    }

    Runnable camera_run = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                initRGBCamera();
                initIRCamera();

                Looper.loop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    private List<Bitmap> lstFaceBmp = new ArrayList<Bitmap>();
    private List<Rect> lstFaceRect = new ArrayList<Rect>();

    Thread faceLoop = null;
    Runnable face_run = new Runnable() {
        @Override
        public void run() {
            Bitmap bitmap = null;
            Rect rect = null;
            while (true) {
                try {
                    if (lstFaceBmp.size() < 1) {
                        try {
                            Thread.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    //
                    bitmap = null;
                    rect = null;
                    synchronized (this) {
                        if (lstFaceBmp.size() > 0) {
                            bitmap = lstFaceBmp.remove(0);
                        }
                        if (lstFaceRect.size() > 0) {
                            rect = lstFaceRect.remove(0);
                        }
                    }
                    if (bitmap != null && rect != null) {
                        doRecognizeFace(bitmap, rect);
                    }
                    Log.e("faceLoop", "lstFaceBmp size = " + lstFaceBmp.size());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
}
