package com.lenovo.lefacecamerademo.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.firefly.api.HardwareCtrl;
import com.lenovo.lefacecamerademo.R;
import com.lenovo.lefacecamerademo.ui.ConfigClass;
import com.lenovo.lefacecamerademo.ui.activity.CameraActivity;
import com.lenovo.lefacecamerademo.utils.MyHardware;
import com.lenovo.lefacecamerademo.utils.MyUtils;
import com.lenovo.lefacecamerademo.utils.SharedPreferencesUtil;

public class FunctionFragment extends BaseFragment implements View.OnClickListener {

    private CameraActivity activity;
    private TextView mSettingFunctionItemTextRecognizeFaceSize;
    private ConstraintLayout mSettingFunctionItemRecognizeFaceSize;
    private TextView mSettingFunctionItemTextMatchingDegree;
    private ConstraintLayout mSettingFunctionItemMatchingDegree;
    private TextView mSettingFunctionItemTextLivingOrNot;
    private ConstraintLayout mSettingFunctionItemLivingOrNot;
    private TextView mSettingFunctionItemTextScreenshotSize;
    private ConstraintLayout mSettingFunctionItemScreenshotSize;
    private TextView mSettingFunctionItemTextIdentificationInterval;
    private TextView mSettingFunctionItemTextScreenBrightness;
    private ConstraintLayout mSettingFunctionItemIdentificationInterval;
    private SeekBar mSettingFunctionItemScreenBrightness;
    private TextView mSettingFunctionItemTextWhiteBrightness;

    String TAG = FunctionFragment.class.getSimpleName();
    private Switch switchRed;
    private Switch switchGreen;
    private Switch switchWhite;
    private Switch switchInfrared;
    private Switch switchDetection;
    private SeekBar mSettingFunctionItemWhiteBrightness;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_function, container, false);
        activity = (CameraActivity) getActivity();
        initView(view);
        return view;

    }

    private void initView(View view) {
        mSettingFunctionItemTextRecognizeFaceSize = view.findViewById(R.id.setting_function_item_text_recognize_face_size);
        mSettingFunctionItemRecognizeFaceSize = view.findViewById(R.id.setting_function_item_recognize_face_size);
        mSettingFunctionItemTextMatchingDegree = view.findViewById(R.id.setting_function_item_text_matching_degree);
        mSettingFunctionItemMatchingDegree = view.findViewById(R.id.setting_function_item_matching_degree);
        mSettingFunctionItemTextLivingOrNot = view.findViewById(R.id.setting_function_item_text_living_or_not);
        mSettingFunctionItemLivingOrNot = view.findViewById(R.id.setting_function_item_living_or_not);
        mSettingFunctionItemTextScreenshotSize = view.findViewById(R.id.setting_function_item_text_screenshot_size);
        mSettingFunctionItemScreenshotSize = view.findViewById(R.id.setting_function_item_screenshot_size);
        mSettingFunctionItemTextIdentificationInterval = view.findViewById(R.id.setting_function_item_text_identification_interval);
        mSettingFunctionItemIdentificationInterval = view.findViewById(R.id.setting_function_item_identification_interval);
        mSettingFunctionItemScreenBrightness = view.findViewById(R.id.setting_function_item_screen_brightness);
        mSettingFunctionItemTextScreenBrightness = view.findViewById(R.id.setting_function_item_text_screen_brightness);
        mSettingFunctionItemWhiteBrightness = view.findViewById(R.id.setting_function_item_white_brightness);
        mSettingFunctionItemTextWhiteBrightness = view.findViewById(R.id.setting_function_item_text_white_brightness);

        mSettingFunctionItemRecognizeFaceSize.setOnClickListener(this);
        mSettingFunctionItemMatchingDegree.setOnClickListener(this);
        mSettingFunctionItemLivingOrNot.setOnClickListener(this);
        mSettingFunctionItemScreenshotSize.setOnClickListener(this);
        mSettingFunctionItemIdentificationInterval.setOnClickListener(this);

        switchRed = view.findViewById(R.id.setting_function_item_switch_red);
        switchGreen = view.findViewById(R.id.setting_function_item_switch_green);
        switchWhite = view.findViewById(R.id.setting_function_item_switch_white);
        switchInfrared = view.findViewById(R.id.setting_function_item_switch_infrared);
        switchDetection = view.findViewById(R.id.setting_function_turn_on_infrared_detection);

        switchRed.setChecked(ConfigClass.Function.FILL_LAMP_RED);
        switchGreen.setChecked(ConfigClass.Function.FILL_LAMP_GREEN);
        switchWhite.setChecked(ConfigClass.Function.FILL_LAMP_WHITE);
        switchInfrared.setChecked(ConfigClass.Function.FILL_LAMP_INFRARED);
        switchDetection.setChecked(ConfigClass.Function.TURN_ON_INFRARED_DETECTION);

        switchRed.setOnClickListener(this);
        switchGreen.setOnClickListener(this);
        switchWhite.setOnClickListener(this);
        switchInfrared.setOnClickListener(this);
        switchDetection.setOnClickListener(this);

        mSettingFunctionItemWhiteBrightness.setMax(7);
        mSettingFunctionItemTextWhiteBrightness.setText(ConfigClass.Function.FILL_LAMP_WHITE_BRIGHTNESS + 1 + "");
        mSettingFunctionItemWhiteBrightness.setProgress(ConfigClass.Function.FILL_LAMP_WHITE_BRIGHTNESS);
        mSettingFunctionItemWhiteBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ConfigClass.Function.FILL_LAMP_WHITE_BRIGHTNESS = progress;
                SharedPreferencesUtil.putValue(activity, "Function.FILL_LAMP_WHITE_BRIGHTNESS", ConfigClass.Function.FILL_LAMP_WHITE_BRIGHTNESS);

                MyUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSettingFunctionItemTextWhiteBrightness.setText(ConfigClass.Function.FILL_LAMP_WHITE_BRIGHTNESS + 1 + "");
                    }
                });
                HardwareCtrl.ctrlLedWhite(true, ConfigClass.Function.FILL_LAMP_WHITE_BRIGHTNESS + 1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(2000);
                                HardwareCtrl.ctrlLedWhite(false);
                                break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSettingFunctionItemScreenBrightness.setMax(225);
        mSettingFunctionItemTextScreenBrightness.setText(ConfigClass.Function.SCREEN_BRIGHTNESS + "");
        mSettingFunctionItemScreenBrightness.setProgress(ConfigClass.Function.SCREEN_BRIGHTNESS);
        mSettingFunctionItemScreenBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                ConfigClass.Function.SCREEN_BRIGHTNESS = progress + 30;
                SharedPreferencesUtil.putValue(activity, "Function.SCREEN_BRIGHTNESS", ConfigClass.Function.SCREEN_BRIGHTNESS);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSettingFunctionItemTextScreenBrightness.setText(ConfigClass.Function.SCREEN_BRIGHTNESS + "");
                        HardwareCtrl.setBrightness(ConfigClass.Function.SCREEN_BRIGHTNESS);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSettingFunctionItemTextRecognizeFaceSize.setText(ConfigClass.Function.FACE_DETECT_MIN + "");//int
        mSettingFunctionItemTextMatchingDegree.setText(ConfigClass.Function.SCORE_THRESH_FACE + "");//float
        mSettingFunctionItemTextLivingOrNot.setText(ConfigClass.Function.SCORE_THRESH_SpoofIR + "");//float
        mSettingFunctionItemTextScreenshotSize.setText(ConfigClass.Function.LOG_IMG_WIDTH + "");//int
        mSettingFunctionItemTextIdentificationInterval.setText(ConfigClass.Function.FACE_FEATURE_INTERVAL * 2 + "");//int
    }

    @Override
    protected void afterRequestPermission(int requestCode, boolean isAllGranted) {

    }

    @Override
    public void onClick(View v) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view;
        PopupWindow popupWindow;
        TextView title;
        EditText edit;
        switch (v.getId()) {
            case R.id.setting_function_item_recognize_face_size:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingFunctionItemRecognizeFaceSize, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("识别人脸大小");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.Function.FACE_DETECT_MIN + "");

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.Function.FACE_DETECT_MIN = Integer.valueOf(edit.getText().toString());
                        mSettingFunctionItemTextRecognizeFaceSize.setText(ConfigClass.Function.FACE_DETECT_MIN + "");//int
                        SharedPreferencesUtil.putValue(activity, "Function.FACE_DETECT_MIN", ConfigClass.Function.FACE_DETECT_MIN);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.setting_function_item_matching_degree:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingFunctionItemMatchingDegree, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("识别人脸匹配度");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.Function.SCORE_THRESH_FACE + "");

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.Function.SCORE_THRESH_FACE = Float.parseFloat(edit.getText().toString());
                        mSettingFunctionItemTextMatchingDegree.setText(ConfigClass.Function.SCORE_THRESH_FACE + "");//float
                        SharedPreferencesUtil.putValue(activity, "Function.SCORE_THRESH_FACE", ConfigClass.Function.SCORE_THRESH_FACE);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.setting_function_item_living_or_not:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingFunctionItemLivingOrNot, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("识别是否活体");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.Function.SCORE_THRESH_SpoofIR + "");

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.Function.SCORE_THRESH_SpoofIR = Float.parseFloat(edit.getText().toString());
                        mSettingFunctionItemTextLivingOrNot.setText(ConfigClass.Function.SCORE_THRESH_SpoofIR + "");//float
                        SharedPreferencesUtil.putValue(activity, "Function.SCORE_THRESH_SpoofIR", ConfigClass.Function.SCORE_THRESH_SpoofIR);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.setting_function_item_screenshot_size:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingFunctionItemTextScreenshotSize, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("截图大小");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.Function.LOG_IMG_WIDTH + "");

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.Function.LOG_IMG_WIDTH = Integer.valueOf(edit.getText().toString());
                        mSettingFunctionItemTextScreenshotSize.setText(ConfigClass.Function.LOG_IMG_WIDTH + "");//int
                        SharedPreferencesUtil.putValue(activity, "Function.LOG_IMG_WIDTH", ConfigClass.Function.LOG_IMG_WIDTH);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.setting_function_item_identification_interval:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingFunctionItemTextIdentificationInterval, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("识别间隔");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.Function.FACE_FEATURE_INTERVAL * 2 + "");

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.Function.FACE_FEATURE_INTERVAL = Integer.valueOf(edit.getText().toString()) / 2;
                        mSettingFunctionItemTextIdentificationInterval.setText(ConfigClass.Function.FACE_FEATURE_INTERVAL * 2 + "");//int
                        SharedPreferencesUtil.putValue(activity, "Function.FACE_FEATURE_INTERVAL", ConfigClass.Function.FACE_FEATURE_INTERVAL);
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.setting_function_item_switch_red:
                ConfigClass.Function.FILL_LAMP_RED = !ConfigClass.Function.FILL_LAMP_RED;
                SharedPreferencesUtil.putValue(activity, "Function.FILL_LAMP_RED", ConfigClass.Function.FILL_LAMP_RED);
                if (ConfigClass.Function.FILL_LAMP_RED == true) {
                    MyHardware.ctrlLedRed(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                    MyHardware.ctrlLedRed(false);
                                    break;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                } else {
                    MyHardware.ctrlLedRed(false);
                }
                break;
            case R.id.setting_function_item_switch_green:
                ConfigClass.Function.FILL_LAMP_GREEN = !ConfigClass.Function.FILL_LAMP_GREEN;
                SharedPreferencesUtil.putValue(activity, "Function.FILL_LAMP_GREEN", ConfigClass.Function.FILL_LAMP_GREEN);
                if (ConfigClass.Function.FILL_LAMP_GREEN == true) {
                    MyHardware.ctrlLedGreen(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                    MyHardware.ctrlLedGreen(false);
                                    break;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                } else {
                    MyHardware.ctrlLedGreen(false);
                }
                break;
            case R.id.setting_function_item_switch_white:
                ConfigClass.Function.FILL_LAMP_WHITE = !ConfigClass.Function.FILL_LAMP_WHITE;
                SharedPreferencesUtil.putValue(activity, "Function.FILL_LAMP_WHITE", ConfigClass.Function.FILL_LAMP_WHITE);
                if (ConfigClass.Function.FILL_LAMP_WHITE == true) {
                    MyHardware.ctrlLedWhite(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                    MyHardware.ctrlLedWhite(false);
                                    break;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                } else {
                    MyHardware.ctrlLedWhite(false);
                }
                break;
            case R.id.setting_function_item_switch_infrared:
                ConfigClass.Function.FILL_LAMP_INFRARED = !ConfigClass.Function.FILL_LAMP_INFRARED;
                SharedPreferencesUtil.putValue(activity, "Function.FILL_LAMP_WHITE", ConfigClass.Function.FILL_LAMP_INFRARED);
                if (ConfigClass.Function.FILL_LAMP_INFRARED == true) {
                    HardwareCtrl.setInfraredFillLight(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                    HardwareCtrl.setInfraredFillLight(false);
                                    break;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                } else {
                    HardwareCtrl.setInfraredFillLight(false);
                }
                break;
        }
    }
}