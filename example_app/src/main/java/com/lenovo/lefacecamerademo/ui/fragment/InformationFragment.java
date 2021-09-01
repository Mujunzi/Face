package com.lenovo.lefacecamerademo.ui.fragment;

import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lenovo.lefacecamerademo.R;
import com.lenovo.lefacecamerademo.ui.ConfigClass;
import com.lenovo.lefacecamerademo.ui.activity.CameraActivity;

public class InformationFragment extends BaseFragment implements View.OnClickListener {

    private ConstraintLayout mSettingInformationItemActivationCode;
    private ConstraintLayout mSettingInformationItemActivatedState;
    private ConstraintLayout mSettingInformationItemSoftwareVersion;
    private ConstraintLayout mSettingInformationItemFirmwareVersion;
    private ConstraintLayout mSettingInformationItemHardwareVersion;
    private ConstraintLayout mSettingInformationItemEquipmentId;
    private TextView mSettingInformationItemTextActivationCode;
    private TextView mSettingInformationItemTextActivatedState;
    private TextView mSettingInformationItemTextSoftwareVersion;
    private TextView mSettingInformationItemTextFirmwareVersion;
    private TextView mSettingInformationItemTextHardwareVersion;
    private TextView mSettingInformationItemTextEquipmentId;
    private CameraActivity activity;
    private TextView mSettingInformationItemTextDeviceStatus;
    private ConstraintLayout mSettingInformationItemDeviceStatus;
    private ConstraintLayout mSettingInformationItemHostSettings;
    private FragmentManager fragmentManager;
    private HostSettingFragment hostSettingFragment;
    private static String versionName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        activity = (CameraActivity) getActivity();
        try {
            versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSettingInformationItemTextDeviceStatus = view.findViewById(R.id.setting_information_item_text_device_status);
        mSettingInformationItemDeviceStatus = view.findViewById(R.id.setting_information_item_device_status);
        mSettingInformationItemHostSettings = view.findViewById(R.id.setting_information_item_host_settings);
        mSettingInformationItemTextSoftwareVersion = view.findViewById(R.id.setting_information_item_text_software_version);
        mSettingInformationItemSoftwareVersion = view.findViewById(R.id.setting_information_item_software_version);
        mSettingInformationItemTextEquipmentId = view.findViewById(R.id.setting_information_item_text_equipment_id);
        mSettingInformationItemEquipmentId = view.findViewById(R.id.setting_information_item_equipment_id);

        mSettingInformationItemHostSettings.setOnClickListener(this);

        mSettingInformationItemTextSoftwareVersion.setText(versionName);
        mSettingInformationItemTextEquipmentId.setText(ConfigClass.Function.DEVICE_ID);
    }

    @Override
    protected void afterRequestPermission(int requestCode, boolean isAllGranted) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_information_item_host_settings:
                activity.toHostSetting();
                break;
        }
    }
}