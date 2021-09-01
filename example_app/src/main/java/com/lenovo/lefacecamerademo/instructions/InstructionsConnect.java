package com.lenovo.lefacecamerademo.instructions;

import android.util.Log;

import com.firefly.api.HardwareCtrl;
import com.firefly.api.serialport.SerialPort;
import com.firefly.api.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

public class InstructionsConnect implements SerialPort.Callback {

    private short HEAD = 0xAA;              //帧头
    private short LENGTH = 0x00;            //数据长度
    private short CMD = 0x00;              //动作指令
    private short[] ORDER = new short[]{};  //指令参数
    private short CHECKSUM = 0x00;          //校验位

    private SerialPort serialPort;

    String TAG = InstructionsConnect.class.getSimpleName();

    public void init() {
        serialPort = HardwareCtrl.openSerialPortSignal(new File("/dev/ttyS4"), 115200, this);
    }

    public void cmd(String s) {
        byte[] bytes = StringUtils.hexString2Bytes(s);

        Log.e("TAG", "cmd: " + s);

        HardwareCtrl.sendSerialPortHexMsg(serialPort, StringUtils.bytesToHexString(bytes, bytes.length));
    }

    public void sendCmd(short[] order) {
        ORDER = order;

        LENGTH = (short) (ORDER.length + 4);        //长度
        CHECKSUM = (short) (HEAD + LENGTH + CMD);        //校验位
        for (int i = 0; i < ORDER.length; i++) {
            CHECKSUM += ORDER[i];
        }
        CHECKSUM %= 256;

        ArrayList<Byte> bytes = new ArrayList<>();
        bytes.add((byte) HEAD);
        bytes.add((byte) LENGTH);
        bytes.add((byte) CMD);
        for (int i = 0; i < ORDER.length; i++) {
            bytes.add(i + 3, (byte) ORDER[i]);
        }
        bytes.add((byte) CHECKSUM);

        byte[] sbyte = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            sbyte[i] = bytes.get(i);
        }
        HardwareCtrl.sendSerialPortHexMsg(serialPort, StringUtils.bytesToHexString(sbyte, sbyte.length));
    }

    public void closeDoor() {
        CMD = 0x51;
        short[] order = new short[]{0x02, 0x00};
        sendCmd(order);
        Log.e("TAG", "closeDoor");
    }

    public void alwaysOpenEnter() {
        CMD = 0x51;
        short[] order = new short[]{0x02, 0x01};
        sendCmd(order);
        Log.e("TAG", "alwaysOpenEnter");
    }

    public void alwaysOpenLeave() {
        CMD = 0x51;
        short[] order = new short[]{0x02, 0x03};
        sendCmd(order);
        Log.e("TAG", "alwaysOpenLeave");
    }

    public void openEnter() {
        CMD = 0x51;
        short[] order = new short[]{0x01, 0x00};
        sendCmd(order);
        Log.e("TAG", "openEnter");
    }

    public void openLeave() {
        CMD = 0x51;
        short[] order = new short[]{0x01, 0x01};
        sendCmd(order);
        Log.e("TAG", "openLeave");
    }

    public void readConfig() {
        CMD = 0x11;
        short[] order = new short[]{0xFF};
        sendCmd(order);
        Log.e("TAG", "readConfig");
    }

    public void LecooAI() {
        CMD = 0x11;
        short[] order = new short[]{0x01};
        sendCmd(order);
        Log.e("TAG", "LecooAI");
    }

    public void firmwareVersion() {
        CMD = 0x11;
        short[] order = new short[]{0x02};
        sendCmd(order);
        Log.e("TAG", "firmwareVersion");
    }

    public void softRestart() {
        CMD = 0xB1;
        short[] order = new short[]{};
        sendCmd(order);
        Log.e("TAG", "softRestart");
    }

    public void openDoorEnter() {
        CMD = 0x51;
        short[] order = new short[]{0x03, 0x01};
        sendCmd(order);
        Log.e("TAG", "softRestart");
    }

    public void openDoorLeave() {
        CMD = 0x51;
        short[] order = new short[]{0x03, 0x10};
        sendCmd(order);
        Log.e("TAG", "softRestart");
    }

    public void openDoor() {
        CMD = 0x51;
        short[] order = new short[]{0x03, 0x00};
        sendCmd(order);
        Log.e("TAG", "softRestart");
    }

    public void modeOneByOne() {
        CMD = 0x51;
        short[] order = new short[]{0x04, 0x11};
        sendCmd(order);
        Log.e("TAG", "softRestart");
    }

    public void modeLineUp() {
        CMD = 0x51;
        short[] order = new short[]{0x04, 0x00};
        sendCmd(order);
        Log.e("TAG", "softRestart");
    }

    @Override
    public void onDataReceived(byte[] bytes, int i) {
        String s = "";
        for (int j = 0; j < bytes.length; j++) {
            if (j == 0) {
                s += "[" + bytes[j] + ", ";
            }
            if (j > 0 && j < bytes.length) {
                s += bytes[j] + ", ";
            }
            if (j == bytes.length - 1) {
                s += bytes[j] + "]";
            }

        }

        if (bytes[0] == -86) {
            Log.e("TAG", "onDataReceived: " + s + i);

            String result = StringUtils.bytesToHexString(bytes, i);
            Log.e("TAG", "485 = " + result);
        }
    }

    public void close() {
        HardwareCtrl.closeSerialPortSignal(serialPort);
    }
}
