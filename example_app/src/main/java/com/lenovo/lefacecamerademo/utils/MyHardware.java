package com.lenovo.lefacecamerademo.utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.firefly.api.FireflyApi;
import com.firefly.api.callback.RecvWiegandCallBack;
import com.firefly.api.serialport.SerialPort;
import com.firefly.api.serialport.SerialPort.Callback;
import com.firefly.api.shell.Command;
import com.firefly.api.utils.FileUtils;
import com.firefly.api.utils.StringUtils;
import com.lenovo.lefacecamerademo.ui.ConfigClass;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class MyHardware {
    public static final int LED_RED = 152;
    public static final int LED_GREEN = 8;
    public static final int LED_WHITE = 155;
    private static final String PATH_EMMC_CID = "/sys/devices/platform/fe330000.sdhci/mmc_host/mmc0/mmc0:0001/cid";
    private static final String PATH_EMMC_CID_1 = "/sys/devices/platform/fe330000.sdhci/mmc_host/mmc1/mmc1:0001/cid";
    private static final String PATH_LED_GREEN = "/sys/class/leds/face:green:user/brightness";
    private static final String PATH_LED_RED = "/sys/class/leds/face:red:user/brightness";
    private static final String PATH_TP = "/dev/tp_ctrl";
    private static final String PATH_LED_WHITE = "/sys/class/leds/face:white:user/brightness";
    private static final String PATH_MODE_SWITCH = "/sys/devices/platform/wiegand-gpio/mode_switch";
    private static final String PATH_D0 = "/sys/devices/platform/wiegand-gpio/D0";
    private static final String PATH_D1 = "/sys/devices/platform/wiegand-gpio/D1";
    private static final String PATH_BL_POWER = "/sys/class/backlight/backlight/bl_power";
    private static final String PATH_BRIGHTNESS = "/sys/class/backlight/backlight/brightness";
    private static final String PATH_WIEGAND26 = "/sys/devices/platform/wiegand-gpio/wiegand26";
    private static final String PATH_WIEGAND34 = "/sys/devices/platform/wiegand-gpio/wiegand34";
    private static final String PATH_WIEGAND = "/dev/wiegand";
    private static final String PATH_GPIO_EXPORT = "/sys/class/gpio/export";
    private static final String PATH_WLAN0 = "/sys/class/net/wlan0/address";
    private static final String PATH_WDT = "/dev/wdt_crl";
    private static final String PATH_HWVERSION = "/sys/class/misc/firefly_hwversion/hwversion";
    private static final String FACE_WHITE_LED = "/sys/pwm/face_white_led";
    public static final String OBJECTTEMP = "/sys/bus/i2c/drivers/mlx90614/7-005a/iio:device1/in_temp_object_raw";
    public static final String AMBIENTTEMP = "/sys/bus/i2c/drivers/mlx90614/7-005a/iio:device1/in_temp_ambient_raw";
    //private static volatile boolean newHWVersion_2_1 = false;
    //private static volatile boolean newHWVersion_2_3 = false;
    private static boolean newHWVersion_2_1 = true;
    private static boolean newHWVersion_2_3 = true;
    private static Class<?> systemProp;
    private static Method setValue;
    private static String HardPropVersion;
    private static final String ACTION_SCHEDULE_POWER_ON = "android.fireflyapi.action.run_power_on";
    private static final String ACTION_SCHEDULE_POWER_OFF = "android.fireflyapi.action.run_power_off";
    private static final String ACTION_SCHEDULE_POWER_REBOOT = "android.fireflyapi.action.run_power_reboot";
    private static final int GPIO_INFRARED_FILL_LIGHT = 69;
    private static final String PATH_FILL_LIGHT = "/sys/pwm/face_white_led";
    private static final int MIN_FILL_LIGHT_BRIGHTNESS = 0;
    private static final int MAX_FILL_LIGHT_BRIGHTNESS = 8;

    static String TAG = MyHardware.class.getSimpleName();

    public MyHardware() {
    }

    public static void shutdown(boolean showConfirm) {
        FireflyApi.getInstance().shutDown(showConfirm);
    }

    public static void reboot() {
        FireflyApi.getInstance().reboot();
    }

    public static void sendSignalD0(boolean up) {
        int value = up ? 1 : 0;
        if ((!getSignalD0State() || value != 1) && (getSignalD0State() || value != 0)) {
            if (!newHWVersion_2_1) {
                writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), "0");
            }

            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/D0"), value + "\n");
        }
    }

    public static void sendSignalD1(boolean up) {
        int value = up ? 1 : 0;
        if ((!getSignalD1State() || value != 1) && (getSignalD1State() || value != 0)) {
            if (!newHWVersion_2_1) {
                writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), "0");
            }

            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/D1"), value + "\n");
        }
    }

    public static boolean getSignalD1State() {
        String singal = readFromDevice(new File("/sys/devices/platform/wiegand-gpio/D1"));
        return TextUtils.equals(singal, "1");
    }

    public static boolean getSignalD0State() {
        String singal = readFromDevice(new File("/sys/devices/platform/wiegand-gpio/D0"));
        return TextUtils.equals(singal, "1");
    }

    public static Command execSuCmd(String cmd) {
        return FireflyApi.getInstance().execSuCmd(cmd);
    }

    public static void sendRelaySignal(boolean up) throws Exception {
        if (newHWVersion_2_1) {
            String relay = up ? "1" : "0";
            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), relay);
        }

    }

    public static boolean getRelayValue() throws Exception {
        boolean relay = false;
        if (newHWVersion_2_1) {
            String singal = readFromDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"));
            relay = TextUtils.equals(singal, "1");
        }

        return relay;
    }

    public static int gpioParse(String gpioStr) {
        if (gpioStr != null && gpioStr.length() == 8) {
            gpioStr = gpioStr.toUpperCase();
            if (gpioStr.charAt(4) >= '0' && gpioStr.charAt(4) <= '8') {
                if (gpioStr.charAt(6) >= 'A' && gpioStr.charAt(6) <= 'D') {
                    return gpioStr.charAt(7) >= '0' && gpioStr.charAt(7) <= '7' ? (gpioStr.charAt(4) - 48) * 32 + (gpioStr.charAt(6) - 65) * 8 + (gpioStr.charAt(7) - 48) : -1;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            System.out.println("input gpio error!");
            return -1;
        }
    }

    public static void ctrlGpio(int gpio, String direction, int value) {
        String valuepath = "/sys/class/gpio/gpio" + gpio + "/value";
        String directionPath = "/sys/class/gpio/gpio" + gpio + "/direction";
        writeToDevice(new File("/sys/class/gpio/export"), gpio + "");
        writeToDevice(new File(directionPath), direction);
        writeToDevice(new File(valuepath), value + "");
    }

    public static String wifiMacAddress() {
        return readFromDevice(new File("/sys/class/net/wlan0/address"));
    }

    public static void sendWiegandSignal(String msg) {
        if (!newHWVersion_2_1) {
            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), "0");
        }

        writeToDevice(new File("/sys/devices/platform/wiegand-gpio/wiegand26"), msg);
    }

    public static void sendWiegand34Signal(String msg) {
        if (!newHWVersion_2_1) {
            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), "0");
        }

        writeToDevice(new File("/sys/devices/platform/wiegand-gpio/wiegand34"), msg);
    }

    public static void setWdt(int value) {
        if (value <= 3 && value >= 0) {
            writeToDevice(new File("/dev/wdt_crl"), value + "");
        } else {
            new IOException();
        }

    }

    public static void ctrlBlPower(boolean open) {
        int value = open ? 0 : 1;
        writeToDevice(new File("/sys/class/backlight/backlight/bl_power"), value + "");
    }

    public static String getFireflyCid() {
        File file = new File("/sys/devices/platform/fe330000.sdhci/mmc_host/mmc0/mmc0:0001/cid");
        return file.exists() ? readFromDevice(file) : readFromDevice(new File("/sys/devices/platform/fe330000.sdhci/mmc_host/mmc1/mmc1:0001/cid"));
    }

    public static String getHWVersion() {
        return readFromDevice(new File("/sys/class/misc/firefly_hwversion/hwversion"));
    }

    public static String getFireWareVersion() {
        if (TextUtils.isEmpty(HardPropVersion)) {
            try {
                if (systemProp == null) {
                    systemProp = Class.forName("android.os.SystemProperties");
                    setValue = systemProp.getDeclaredMethod("get", String.class, String.class);
                }

                String version = (String) setValue.invoke((Object) null, "ro.firefly.build.fingerprint", "");
                if (TextUtils.isEmpty(version)) {
                    return "";
                }

                HardPropVersion = version.substring(version.indexOf("industry-71/") + 12);
            } catch (Exception var1) {
                var1.printStackTrace();
            }
        }

        return HardPropVersion;
    }

    public static String readFromDevice(File device) {
        String result = "";
        if (device.exists()) {
            if (!device.canRead() || !device.canWrite()) {
                excuseCmd("vm -c 'chmod 666 " + device.getAbsolutePath() + "'");
            }

            try {
                result = (new BufferedReader(new FileReader(device))).readLine();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }

        return result;
    }

    private static boolean shellCommand(String command) {
        Command cmd = FireflyApi.getInstance().execSuCmd(command);
        return cmd.isSuccessful();
    }

    public static void writeToDevice(File device, String value) {
        if (device.exists()) {
            if (!device.canRead() || !device.canWrite()) {
                excuseCmd("vm -c 'chmod 666 " + device.getAbsolutePath() + "'");
            }

            FileOutputStream outputStream = null;

            try {
                outputStream = new FileOutputStream(device);
                outputStream.write(value.getBytes());
                outputStream.flush();
            } catch (FileNotFoundException var14) {
                var14.printStackTrace();
            } catch (IOException var15) {
                var15.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException var13) {
                        var13.printStackTrace();
                    }
                }

            }
        }

    }

    public static void setBrightness(int value) {
        if (value <= 255 && value >= 0) {
            writeToDevice(new File("/sys/class/backlight/backlight/brightness"), value + "");
        } else {
            new IOException();
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void setLedSwitch(int gpioCode, boolean open) {
        int val = open ? 0 : 1;
        String valuepath = "/sys/class/gpio/gpio" + gpioCode + "/value";
        String directionPath = "/sys/class/gpio/gpio" + gpioCode + "/direction";
        writeToDevice(new File("/sys/class/gpio/export"), gpioCode + "");
        writeToDevice(new File(directionPath), "out");
        writeToDevice(new File(valuepath), val + "");
    }

    public static void ctrlWhiteLightness(int lightness) {
        if (lightness >= 0 && lightness <= 8 && newHWVersion_2_3) {
            writeToDevice(new File("/sys/pwm/face_white_led"), lightness + "");
        }

    }

    public static void ctrlLedWhite(boolean open) {
        Log.d(TAG, "ctrlLedWhite: " + ConfigClass.Function.FILL_LAMP_WHITE);
        if (ConfigClass.Function.FILL_LAMP_WHITE == true) {
            int val;
            if (newHWVersion_2_3) {
                val = open ? 8 : 0;
                ctrlWhiteLightness(val);
            } else {
                val = open ? 1 : 0;
                writeToDevice(new File("/sys/class/leds/face:white:user/brightness"), val + "");
            }
        }
    }

    public static void ctrlLedWhite(boolean open, int brightness) {
        int val;
        if (newHWVersion_2_3) {
            val = open ? brightness : 0;
            if (val < 0 && val > 8) {
                val = 8;
            }

            ctrlWhiteLightness(val);
        } else {
            val = open ? 1 : 0;
            writeToDevice(new File("/sys/class/leds/face:white:user/brightness"), val + "");
        }

    }

    public static void witch(int gpioCode, boolean open) {
        switch (gpioCode) {
            case 8:
                ctrlLedGreen(open);
                break;
            case 152:
                ctrlLedRed(open);
                break;
            case 155:
                ctrlLedWhite(open);
        }

    }

    public static void ctrlLedOnlyOpenGreen() {
        ctrlLedGreen(true);
        ctrlLedRed(false);
        ctrlLedWhite(false);
    }

    public static void ctrlLedOnlyOpenRed() {
        ctrlLedGreen(false);
        ctrlLedRed(true);
        ctrlLedWhite(false);
    }

    public static void ctrlLedOnlyOpenWhite() {
        ctrlLedGreen(false);
        ctrlLedRed(false);
        ctrlLedWhite(true);
    }

    public static void ctrlLedCloseAll() {
        ctrlLedGreen(false);
        ctrlLedRed(false);
        ctrlLedWhite(false);
    }

    public static void ctrlLedGreen(boolean open) {
        Log.d(TAG, "ctrlLedGreen: " + ConfigClass.Function.FILL_LAMP_GREEN);
        if (ConfigClass.Function.FILL_LAMP_GREEN == true) {
            int val = open ? 1 : 0;
            writeToDevice(new File("/sys/class/leds/face:green:user/brightness"), val + "");
        }
    }

    public static void ctrlLedRed(boolean open) {
        Log.d(TAG, "ctrlLedRed: " + ConfigClass.Function.FILL_LAMP_RED);
        if (ConfigClass.Function.FILL_LAMP_RED == true) {
            int val = open ? 1 : 0;
            writeToDevice(new File("/sys/class/leds/face:red:user/brightness"), val + "");
        }
    }

    public static void ctrlTp(boolean open) {
        int val = open ? 1 : 0;
        writeToDevice(new File("/dev/tp_ctrl"), val + "");
    }

    public static boolean isOpenTp() {
        String val = readFromDevice(new File("/dev/tp_ctrl"));
        return val.equals("1");
    }

    public static void excuseCmd(String command) {
        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("vm");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception var12) {
            var12.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (Exception var11) {
                var11.printStackTrace();
            }

        }

    }

    public static int compareVersion(String newVersion, String oldVersion) {
        if (newVersion.startsWith("V") || newVersion.startsWith("v")) {
            newVersion = newVersion.substring(1);
        }

        if (oldVersion.startsWith("V") || oldVersion.startsWith("v")) {
            oldVersion = oldVersion.substring(1);
        }

        String[] s1 = newVersion.split("\\.");
        String[] s2 = oldVersion.split("\\.");
        int len1 = s1.length;
        int len2 = s2.length;
        boolean end = false;
        int res = 0;

        for (int i = 0; !end; ++i) {
            int compare1;
            if (len1 > i && len2 > i) {
                int compare2;
                try {
                    compare1 = Integer.parseInt(s1[i]);
                    compare2 = Integer.parseInt(s2[i]);
                    if (compare1 > compare2) {
                        res = 1;
                        end = true;
                    } else if (compare1 < compare2) {
                        res = -1;
                        end = true;
                    }
                } catch (Exception var11) {
                    compare2 = s1[i].compareToIgnoreCase(s2[i]);
                    if (compare2 > 0) {
                        res = 1;
                        end = true;
                    } else if (compare2 < 0) {
                        res = -1;
                        end = true;
                    }
                }
            } else {
                if (len1 > len2) {
                    res = 1;
                } else if (len1 < len2) {
                    res = -1;
                } else {
                    compare1 = newVersion.compareToIgnoreCase(oldVersion);
                    if (compare1 > 0) {
                        res = 1;
                    } else if (compare1 < 0) {
                        res = -1;
                    } else {
                        res = 0;
                    }
                }

                end = true;
            }
        }

        return res;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static SerialPort openRs485Signal(File device, int baudrate, Callback callback) {
        SerialPort mSerialPort = null;
        if (device.exists()) {
            if (!device.canRead() || !device.canWrite()) {
                excuseCmd("vm -c 'chmod 666 " + device.getAbsolutePath() + "'");
            }

            try {
                mSerialPort = new SerialPort(device, baudrate, 0);
                mSerialPort.setCallback(callback);
            } catch (SecurityException var5) {
                var5.printStackTrace();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        return mSerialPort;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static SerialPort openRs485Signal(int baudrate, Callback callback) {
        SerialPort mSerialPort = null;
        File device = new File("/dev/ttyS4");
        if (device.exists()) {
            if (!device.canRead() || !device.canWrite()) {
                excuseCmd("vm -c 'chmod 666 " + device.getAbsolutePath() + "'");
            }

            try {
                mSerialPort = new SerialPort(device, baudrate, 0);
                mSerialPort.setCallback(callback);
            } catch (SecurityException var5) {
                var5.printStackTrace();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        return mSerialPort;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void sendRs485Msg(SerialPort mSerialPort, String msg) {
        if (!newHWVersion_2_1) {
            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), "1");
        }

        if (mSerialPort != null) {
            mSerialPort.sendMsg(msg);
        } else {
            new IOException();
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void sendRs485HexMsg(SerialPort mSerialPort, String msg) {
        if (!newHWVersion_2_1) {
            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), "1");
        }

        if (mSerialPort != null) {
            mSerialPort.sendHexMsg(StringUtils.hexString2Bytes(msg));
        } else {
            new IOException();
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void closeRs485Signal(SerialPort mSerialPort) {
        if (mSerialPort != null) {
            try {
                mSerialPort.closeSerialPort();
            } catch (SecurityException var2) {
                var2.printStackTrace();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public static SerialPort openSerialPortSignal(File device, int baudrate, Callback callback) {
        SerialPort mSerialPort = null;
        if (device.exists()) {
            if (!device.canRead() || !device.canWrite()) {
                excuseCmd("vm -c 'chmod 666 " + device.getAbsolutePath() + "'");
            }

            try {
                mSerialPort = new SerialPort(device, baudrate, 0);
                mSerialPort.setCallback(callback);
            } catch (SecurityException var5) {
                var5.printStackTrace();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        return mSerialPort;
    }

    public static void sendSerialPortMsg(SerialPort mSerialPort, String msg) {
        if (!newHWVersion_2_1) {
            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), "1");
        }

        if (mSerialPort != null) {
            mSerialPort.sendMsg(msg);
        } else {
            new IOException();
        }

    }

    public static void sendSerialPortHexMsg(SerialPort mSerialPort, String msg) {
        if (!newHWVersion_2_1) {
            writeToDevice(new File("/sys/devices/platform/wiegand-gpio/mode_switch"), "1");
        }

        if (mSerialPort != null) {
            mSerialPort.sendHexMsg(StringUtils.hexString2Bytes(msg));
        } else {
            new IOException();
        }

    }

    public static void closeSerialPortSignal(SerialPort mSerialPort) {
        if (mSerialPort != null) {
            try {
                mSerialPort.closeSerialPort();
            } catch (SecurityException var2) {
                var2.printStackTrace();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public static void openRecvMiegandSignal(String filePath) {
        if (!newHWVersion_2_1) {
            File device = new File(filePath);
            if (device.exists()) {
                if (!device.canRead() || !device.canWrite()) {
                    excuseCmd("vm -c 'chmod 666 " + device.getAbsolutePath() + "'");
                }

                openRecvMiegand(filePath);
            }
        }

    }

    public static void recvWiegandSignal(RecvWiegandCallBack callBack) {
        if (!newHWVersion_2_1) {
            recvWiegand(callBack);
        }

    }

    public static void closeRecvMiegandSignal() {
        if (!newHWVersion_2_1) {
            closeRecvMiegand();
        }

    }

    public static void setSchedulePowerOn(Context context, int id, boolean enabled, long alarm_time) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (enabled) {
            //alarmManager.set(4, alarm_time, getPendingIntent(context, "android.fireflyapi.action.run_power_on", id));
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, alarm_time, getPendingIntent(context, "android.fireflyapi.action.run_power_on", id));
        } else {
            alarmManager.cancel(getPendingIntent(context, "android.fireflyapi.action.run_power_on", id));
        }

    }

    public static void setSchedulePowerOff(Context context, int id, boolean enabled, long alarm_time) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (enabled) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, getPendingIntent(context, "android.fireflyapi.action.run_power_off", id));
        } else {
            alarmManager.cancel(getPendingIntent(context, "android.fireflyapi.action.run_power_off", id));
        }

    }

    public static void setSchedulePowerReboot(Context context, int id, boolean enabled, long alarm_time) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (enabled) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, getPendingIntent(context, "android.fireflyapi.action.run_power_reboot", id));
        } else {
            alarmManager.cancel(getPendingIntent(context, "android.fireflyapi.action.run_power_reboot", id));
        }

    }

    private static PendingIntent getPendingIntent(Context context, String action, int requestCode) {
        Intent intent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    public static boolean isSupportInfraredFillLight() {
        return newHWVersion_2_3;
    }

    public static void setInfraredFillLight(boolean enable) {
        if (isSupportInfraredFillLight()) {
            ctrlGpio(69, "out", enable ? 1 : 0);
        }

    }

    public static boolean isFillLightBrightnessSupport() {
        return (new File("/sys/pwm/face_white_led")).exists();
    }

    public static void writeFillLightBrightnessOptions(int value) {
        FileUtils.write2File(new File("/sys/pwm/face_white_led"), String.valueOf(value));
    }

    public static int readFillLightBrightnessValue() {
        String value = FileUtils.readFromFile(new File("/sys/pwm/face_white_led"));
        return TextUtils.isEmpty(value) ? 8 : Integer.getInteger(value, 8);
    }

    public static int getFillLightBrightnessMax() {
        return 8;
    }

    public static int getFillLightBrightnessMin() {
        return 0;
    }

    private static native int openRecvMiegand(String var0);

    private static native void recvWiegand(RecvWiegandCallBack var0);

    private static native int closeRecvMiegand();

    static {
        //newHWVersion_2_1 = true; //compareVersion(getHWVersion(), "2.1") >= 0;
        //newHWVersion_2_3 = true; //compareVersion(getHWVersion(), "2.3") >= 0;
  /*      systemProp = null;
        setValue = null;

        try {
            systemProp = Class.forName("android.os.SystemProperties");
            setValue = systemProp.getDeclaredMethod("get", String.class, String.class);
        } catch (Exception var1) {
            var1.printStackTrace();
        }
*/
        HardPropVersion = "";
        System.loadLibrary("Wiegand");
    }
}
