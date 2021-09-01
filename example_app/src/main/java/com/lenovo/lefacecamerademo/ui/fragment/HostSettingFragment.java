package com.lenovo.lefacecamerademo.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lenovo.lefacecamerademo.R;
import com.lenovo.lefacecamerademo.ui.activity.CameraActivity;

public class HostSettingFragment extends BaseFragment implements View.OnClickListener {

    String TAG = HostSettingFragment.class.getSimpleName();
    private CameraActivity activity;
    private Button mSettingHostItemCloseDoor;
    private Button mSettingHostItemAlwaysOpenEnter;
    private Button mSettingHostItemAlwaysOpenLeave;
    private Button mSettingHostItemOpenEnter;
    private Button mSettingHostItemOpenLeave;
    private ConstraintLayout mBackToSetting;
    private Button mSettingHostItemReadGcuConfig;
    private Button mSettingHostItemReadLecooai;
    private Button mSettingHostItemFirmwareVersion;
    private Button mSettingHostItemSoftRestart;
    private Button mSettingHostItemOpenDoorEnter;
    private Button mSettingHostItemOpenDoorLeave;
    private Button mSettingHostItemModeOneByOne;
    private Button mSettingHostItemOpenDoor;
    private Button mSettingHostItemModeLineUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_setting, container, false);
        activity = (CameraActivity) getActivity();
        initView(view);
        return view;
    }

    public static Fragment getInstance() {
        return new HostSettingFragment();
    }

    private void initView(View view) {
        mBackToSetting = view.findViewById(R.id.btn_back_to_setting);
        mSettingHostItemCloseDoor = view.findViewById(R.id.setting_host_item_close_door);
        mSettingHostItemAlwaysOpenEnter = view.findViewById(R.id.setting_host_item_always_open_enter);
        mSettingHostItemAlwaysOpenLeave = view.findViewById(R.id.setting_host_item_always_open_leave);
        mSettingHostItemOpenEnter = view.findViewById(R.id.setting_host_item_open_enter);
        mSettingHostItemOpenLeave = view.findViewById(R.id.setting_host_item_open_leave);
        mSettingHostItemReadGcuConfig = view.findViewById(R.id.setting_host_item_read_gcu_config);
        mSettingHostItemReadLecooai = view.findViewById(R.id.setting_host_item_read_lecooai);
        mSettingHostItemFirmwareVersion = view.findViewById(R.id.setting_host_item_firmware_version);
        mSettingHostItemSoftRestart = view.findViewById(R.id.setting_host_item_soft_restart);
        mSettingHostItemOpenDoorEnter = view.findViewById(R.id.setting_host_item_open_door_enter);
        mSettingHostItemOpenDoorLeave = view.findViewById(R.id.setting_host_item_open_door_leave);
        mSettingHostItemOpenDoor = view.findViewById(R.id.setting_host_item_open_door);
        mSettingHostItemModeOneByOne = view.findViewById(R.id.setting_host_item_mode_one_by_one);
        mSettingHostItemModeLineUp = view.findViewById(R.id.setting_host_item_mode_line_up);

        mBackToSetting.setOnClickListener(this);
        mSettingHostItemCloseDoor.setOnClickListener(this);
        mSettingHostItemAlwaysOpenEnter.setOnClickListener(this);
        mSettingHostItemAlwaysOpenLeave.setOnClickListener(this);
        mSettingHostItemOpenEnter.setOnClickListener(this);
        mSettingHostItemOpenLeave.setOnClickListener(this);
        mSettingHostItemReadGcuConfig.setOnClickListener(this);
        mSettingHostItemReadLecooai.setOnClickListener(this);
        mSettingHostItemFirmwareVersion.setOnClickListener(this);
        mSettingHostItemSoftRestart.setOnClickListener(this);
        mSettingHostItemOpenDoorEnter.setOnClickListener(this);
        mSettingHostItemOpenDoorLeave.setOnClickListener(this);
        mSettingHostItemOpenDoor.setOnClickListener(this);
        mSettingHostItemModeOneByOne.setOnClickListener(this);
        mSettingHostItemModeLineUp.setOnClickListener(this);
    }

    @Override
    protected void afterRequestPermission(int requestCode, boolean isAllGranted) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_to_setting:
                activity.toSetting();
                break;
            case R.id.setting_host_item_close_door:
                activity.instructionsConnect.closeDoor();
                break;
            case R.id.setting_host_item_always_open_enter:
                activity.instructionsConnect.alwaysOpenEnter();
                break;
            case R.id.setting_host_item_always_open_leave:
                activity.instructionsConnect.alwaysOpenLeave();
                break;
            case R.id.setting_host_item_open_enter:
                activity.instructionsConnect.openEnter();
                break;
            case R.id.setting_host_item_open_leave:
                activity.instructionsConnect.openLeave();
                break;
            case R.id.setting_host_item_read_gcu_config:
                activity.instructionsConnect.readConfig();
                break;
            case R.id.setting_host_item_read_lecooai:
                activity.instructionsConnect.LecooAI();
                break;
            case R.id.setting_host_item_firmware_version:
                activity.instructionsConnect.firmwareVersion();
                break;
            case R.id.setting_host_item_soft_restart:
                activity.instructionsConnect.softRestart();
                break;
            case R.id.setting_host_item_open_door_enter:
                activity.instructionsConnect.openDoorEnter();
                break;
            case R.id.setting_host_item_open_door_leave:
                activity.instructionsConnect.openDoorLeave();
                break;
            case R.id.setting_host_item_open_door:
                activity.instructionsConnect.openDoor();
                break;
            case R.id.setting_host_item_mode_one_by_one:
                activity.instructionsConnect.modeOneByOne();
                break;
            case R.id.setting_host_item_mode_line_up:
                activity.instructionsConnect.modeLineUp();
                break;
        }
    }
}