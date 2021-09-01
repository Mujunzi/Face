package com.lenovo.lefacecamerademo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.lenovo.lefacecamerademo.R;
import com.lenovo.lefacecamerademo.camera.CameraListener;
import com.lenovo.lefacecamerademo.camera.DualCameraHelper;
import com.lenovo.lefacecamerademo.utils.BitmapUtils;
import com.lenovo.lefacecamerademo.utils.PadLightUtils;
import com.lenovo.lefacecamerademo.widget.AutoFitTextureView;
import com.lenovo.lefacecamerademo.widget.OverlayView;
import com.lenovo.lefacesdk.LefaceEngine;
import com.lenovo.lefacesdk.Rect;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CameraFragment extends BaseFragment {
  private AutoFitTextureView textureViewIR, textureViewRGB;
  private OverlayView overlayView;
  private  LefaceEngine mEngine;
  private LefaceEngine.InitializationResult mInitResult;
  private static final String ACCESS_KEY =
      "";
  private static final String SECRET_KEY =
      "";
  private DualCameraHelper cameraHelperIR;
  private DualCameraHelper cameraHelperRGB;
  private final String TAG = "CameraFragment";
  private final int CAMERA_REQUEST_CODE = 1001;
  private static final String[] NEEDED_PERMISSIONS = new String[] {
      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  public static android.app.Fragment newInstance() {
    return new CameraFragment();
  }

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    PadLightUtils.setIRLight();
    initView(view);
    requestPermission();
  }

  private void initView(View view) {
    textureViewIR = (AutoFitTextureView) view.findViewById(R.id.texture_IR);
    textureViewRGB = (AutoFitTextureView) view.findViewById(R.id.texture_RGB);
    overlayView = (OverlayView) view.findViewById(R.id.overlay);
  }

  private void requestPermission() {
    if (!checkPermissions(NEEDED_PERMISSIONS)) {
      FragmentCompat.requestPermissions(this, NEEDED_PERMISSIONS, CAMERA_REQUEST_CODE);
      return;
    }
    new Thread(new Runnable() {
      @Override public void run() {
        Looper.prepare();
        initLefaceModel();
        Looper.loop();
      }
    }).start();
  }

  private void initLefaceModel() {
    try {
      if (mEngine == null) {
        mEngine = new LefaceEngine(getActivity(), ACCESS_KEY, SECRET_KEY);
        mInitResult = mEngine.initialize();
        Log.d(TAG, "engine initialization result:" + mInitResult.toString());
        if (mInitResult == LefaceEngine.InitializationResult.SUCCESS) {
          mEngine.initializeIRSpoofDetector();
          mEngine.initializeDetector();
          mEngine.setNumThreads(2);
          mEngine.setUseGPU(true);
          initRGBCamera();
          initIRCamera();
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

  private void loadDetectResult(List<Float> IRdetectList, List<Rect> detectFaceList) {
    if (!detectFaceList.isEmpty()) {
      //saveBitmap(bitmap, bitmapIR);
      overlayView.setDetectionRect(detectFaceList);
      overlayView.drawOverlay(OverlayView.BLUE);
      Log.e(TAG, "result-->" + Arrays.toString(detectFaceList.toArray()));
      Log.e(TAG, "IRdetectList-->" + Arrays.toString(IRdetectList.toArray()));
      for (float f : IRdetectList) {
        if (f < 0) {
          return;
        }
        if (f > 0.5) {
          overlayView.drawOverlay(OverlayView.GREEN);
        } else {
          overlayView.drawOverlay(OverlayView.RED);
        }
      }
    } else {
      overlayView.clearOverlay();
    }
  }

  private void saveBitmap(Bitmap bitmap, Bitmap bitmapIR) {
    String path =
        getActivity().getFilesDir() + "/images/" + System.currentTimeMillis() + File.separator;
    BitmapUtils.saveBitmap(path, "RGB.png", bitmap);
    BitmapUtils.saveBitmap(path, "IR.png", bitmapIR);
  }

  CameraListener cameraListenerRGB = new CameraListener() {
    @Override public void onCameraOpened(Camera camera, int cameraId, int displayOrientation,
        boolean isMirror) {

    }

    @SuppressLint("NewApi") @Override public void onPreview(byte[] data, Camera camera) {
        Bitmap bitmapIR = textureViewIR.getBitmap();
        HashMap<List<Float>, List<Rect>> IRdetectHashMap =
            mEngine.detectSpoofIRyinghuochong(bitmapIR);//活体检测
        IRdetectHashMap.forEach((k, v) -> loadDetectResult(k, v));
    }

    @Override public void onCameraClosed() {

    }

    @Override public void onCameraError(Exception e) {

    }

    @Override public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

    }
  };

  CameraListener cameraListenerIR = new CameraListener() {
    @Override public void onCameraOpened(Camera camera, int cameraId, int displayOrientation,
        boolean isMirror) {

    }

    @Override public void onPreview(byte[] data, Camera camera) {

    }

    @Override public void onCameraClosed() {

    }

    @Override public void onCameraError(Exception e) {

    }

    @Override public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

    }
  };

  @Override
  public void onResume() {
    super.onResume();
    try {
      if (cameraHelperIR != null) {
        cameraHelperIR.start();
      }
      if (cameraHelperRGB != null) {
        cameraHelperRGB.start();
      }
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
  public void onDestroy() {
    mEngine.close();
    if (cameraHelperIR != null) {
      cameraHelperIR.release();
      cameraHelperIR = null;
    }
    if (cameraHelperRGB != null) {
      cameraHelperRGB.release();
      cameraHelperRGB = null;
    }
    super.onDestroy();
  }

  @Override void afterRequestPermission(int requestCode, boolean isAllGranted) {
    if (requestCode == CAMERA_REQUEST_CODE) {
      if (isAllGranted) {
        initLefaceModel();
      } else {
        showToast("权限被拒绝！");
      }
    }
  }
}
