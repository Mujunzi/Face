package com.lenovo.lefacecamerademo.instructions;

public class State {
    private short rf_conf;
    private long door_open_times;
    private long cur_work_secs;
    private long cur_err_secs;
    private long total_work_secs;
    private long person_enter_count;
    private long person_leave_count;
    private int ini_err;
    private int sensors;
    private short door_cmd;
    private short door_sts;
    private short tickets;
    private short work_mode;
    private short pass_direction;
    private short trigger_mode;
    private short auth_pass_mode;
    private short alarm;
    private short switch_state;
    private short switch2_state;

    public State() {

    }
}
