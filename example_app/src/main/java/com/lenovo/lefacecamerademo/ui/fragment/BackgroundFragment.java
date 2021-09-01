package com.lenovo.lefacecamerademo.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lenovo.lefacecamerademo.R;
import com.lenovo.lefacecamerademo.ui.ConfigClass;
import com.lenovo.lefacecamerademo.ui.activity.CameraActivity;
import com.lenovo.lefacecamerademo.utils.SharedPreferencesUtil;

public class BackgroundFragment extends BaseFragment implements View.OnClickListener {

    private CameraActivity activity;
    private TextView mSettingBackgroundItemTextServerAddress;
    private ConstraintLayout mSettingBackgroundItemServerAddress;
    private ConstraintLayout mSettingBackgroundItemServerUsernamePassword;
    private TextView mSettingBackgroundItemTextPublishTopic;
    private ConstraintLayout mSettingBackgroundItemPublishTopic;
    private TextView mSettingBackgroundItemTextSubscribeTopic;
    private ConstraintLayout mSettingBackgroundItemSubscribeTopic;
    private TextView mSettingBackgroundItemTextResponseTopic;
    private ConstraintLayout mSettingBackgroundItemResponseTopic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_background, container, false);
        activity = (CameraActivity) getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSettingBackgroundItemTextServerAddress = view.findViewById(R.id.setting_background_item_text_server_address);
        mSettingBackgroundItemServerAddress = view.findViewById(R.id.setting_background_item_server_address);
        mSettingBackgroundItemServerUsernamePassword = view.findViewById(R.id.setting_background_item_server_username_password);
        mSettingBackgroundItemTextPublishTopic = view.findViewById(R.id.setting_background_item_text_publish_topic);
        mSettingBackgroundItemPublishTopic = view.findViewById(R.id.setting_background_item_publish_topic);
        mSettingBackgroundItemTextSubscribeTopic = view.findViewById(R.id.setting_background_item_text_subscribe_topic);
        mSettingBackgroundItemSubscribeTopic = view.findViewById(R.id.setting_background_item_subscribe_topic);
        mSettingBackgroundItemTextResponseTopic = view.findViewById(R.id.setting_background_item_text_response_topic);
        mSettingBackgroundItemResponseTopic = view.findViewById(R.id.setting_background_item_response_topic);

        mSettingBackgroundItemServerAddress.setOnClickListener(this);
        mSettingBackgroundItemServerUsernamePassword.setOnClickListener(this);
        mSettingBackgroundItemPublishTopic.setOnClickListener(this);
        mSettingBackgroundItemSubscribeTopic.setOnClickListener(this);
        mSettingBackgroundItemResponseTopic.setOnClickListener(this);

        mSettingBackgroundItemTextServerAddress.setText(ConfigClass.MQTT.HOST);
        mSettingBackgroundItemTextPublishTopic.setText(ConfigClass.MQTT.PUBLISH_TOPIC);
        mSettingBackgroundItemTextSubscribeTopic.setText(ConfigClass.MQTT.SUBSCRIBE_TOPIC);
        mSettingBackgroundItemTextResponseTopic.setText(ConfigClass.MQTT.RESPONSE_TOPIC);
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
            case R.id.setting_background_item_server_address:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingBackgroundItemServerAddress, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("服务器地址");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.MQTT.HOST);

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.MQTT.HOST = edit.getText().toString();
                        mSettingBackgroundItemTextServerAddress.setText(ConfigClass.MQTT.HOST);
                        SharedPreferencesUtil.putValue(activity, "MQTT.HOST", edit.getText().toString());
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.setting_background_item_server_username_password:
                view = inflater.inflate(R.layout.popup_background_username, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingBackgroundItemServerUsernamePassword, Gravity.CENTER, 0, 0);

                EditText username = view.findViewById(R.id.edit_background_username);
                EditText password = view.findViewById(R.id.edit_background_password);

                username.setText(ConfigClass.MQTT.USERNAME);
                password.setText(ConfigClass.MQTT.PASSWORD);

                view.findViewById(R.id.btn_background_popup_definite)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ConfigClass.MQTT.USERNAME = username.getText().toString();
                                ConfigClass.MQTT.PASSWORD = password.getText().toString();

                                SharedPreferencesUtil.putValue(activity, "MQTT.USERNAME", username.getText().toString());
                                SharedPreferencesUtil.putValue(activity, "MQTT.PASSWORD", password.getText().toString());

                                popupWindow.dismiss();
                            }
                        });

                break;
            case R.id.setting_background_item_publish_topic:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingBackgroundItemPublishTopic, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("PUBLISH_TOPIC");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.MQTT.PUBLISH_TOPIC);

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.MQTT.PUBLISH_TOPIC = edit.getText().toString();
                        mSettingBackgroundItemTextPublishTopic.setText(ConfigClass.MQTT.PUBLISH_TOPIC);
                        SharedPreferencesUtil.putValue(activity, "MQTT.PUBLISH_TOPIC", edit.getText().toString());
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.setting_background_item_subscribe_topic:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingBackgroundItemSubscribeTopic, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("SUBSCRIBE_TOPIC");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.MQTT.SUBSCRIBE_TOPIC);

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.MQTT.SUBSCRIBE_TOPIC = edit.getText().toString();
                        mSettingBackgroundItemTextSubscribeTopic.setText(ConfigClass.MQTT.SUBSCRIBE_TOPIC);
                        SharedPreferencesUtil.putValue(activity, "MQTT.SUBSCRIBE_TOPIC", edit.getText().toString());
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.setting_background_item_response_topic:
                view = inflater.inflate(R.layout.popup_background_item, null, false);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(mSettingBackgroundItemResponseTopic, Gravity.CENTER, 0, 0);

                title = view.findViewById(R.id.text_popup_title);
                title.setText("RESPONSE_TOPIC");

                edit = view.findViewById(R.id.edit_background);
                edit.setText(ConfigClass.MQTT.RESPONSE_TOPIC);

                view.findViewById(R.id.btn_background_definite).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigClass.MQTT.RESPONSE_TOPIC = edit.getText().toString();
                        mSettingBackgroundItemTextResponseTopic.setText(ConfigClass.MQTT.RESPONSE_TOPIC);
                        SharedPreferencesUtil.putValue(activity, "MQTT.RESPONSE_TOPIC", edit.getText().toString());
                        popupWindow.dismiss();
                    }
                });
                break;
        }
    }
}