package com.lenovo.lefacecamerademo.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.firefly.api.HardwareCtrl;
import com.lenovo.lefacecamerademo.R;
import com.lenovo.lefacecamerademo.ui.activity.CameraActivity;
import com.lenovo.lefacecamerademo.utils.MyUtils;

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private ConstraintLayout backToFace;
    private CameraActivity activity;
    private BackgroundFragment backgroundFragment;
    private InformationFragment informationFragment;
    private FunctionFragment functionFragment;
    private FragmentManager fragmentManager;
    private Button settingBackground;
    private Button settingInformation;
    private Button settingFunction;

    String TAG = SettingFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        MyUtils.closeAllLight();
        HardwareCtrl.setBrightness(255);
        initView(view);
        activity = (CameraActivity) getActivity();
        return view;
    }

    public static Fragment getInstance() {
        return new SettingFragment();
    }

    private void initView(View view) {
        backToFace = view.findViewById(R.id.btn_back_to_face);
        settingFunction = view.findViewById(R.id.btn_setting_function);
//        settingSystem = view.findViewById(R.id.btn_setting_system);
        settingInformation = view.findViewById(R.id.btn_setting_information);
        settingBackground = view.findViewById(R.id.btn_setting_background);

        backToFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick()");
                activity.backToFace();
            }
        });

        functionFragment = new FunctionFragment();
        informationFragment = new InformationFragment();
        backgroundFragment = new BackgroundFragment();

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fm_setting, functionFragment)
                .add(R.id.fm_setting, informationFragment)
                .add(R.id.fm_setting, backgroundFragment)
                .show(functionFragment)
                .hide(informationFragment)
                .hide(backgroundFragment)
                .commit();

        settingFunction.setOnClickListener(this);
        settingInformation.setOnClickListener(this);
        settingBackground.setOnClickListener(this);

        settingFunction.setTextSize(20);
        settingInformation.setTextSize(16);
        settingBackground.setTextSize(16);
    }


    @Override
    protected void afterRequestPermission(int requestCode, boolean isAllGranted) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting_function:
                settingFunction.setTextSize(20);
                settingInformation.setTextSize(16);
                settingBackground.setTextSize(16);
                fragmentManager.beginTransaction()
                        .show(functionFragment)
                        .hide(informationFragment)
                        .hide(backgroundFragment)
                        .commit();
                break;
            case R.id.btn_setting_information:
                settingFunction.setTextSize(16);
                settingInformation.setTextSize(20);
                settingBackground.setTextSize(16);
                fragmentManager.beginTransaction()
                        .hide(functionFragment)
                        .show(informationFragment)
                        .hide(backgroundFragment)
                        .commit();
                break;
            case R.id.btn_setting_background:
                settingFunction.setTextSize(16);
                settingInformation.setTextSize(16);
                settingBackground.setTextSize(20);
                fragmentManager.beginTransaction()
                        .hide(functionFragment)
                        .hide(informationFragment)
                        .show(backgroundFragment)
                        .commit();
                break;
        }
    }

}