package com.lenovo.lefacecamerademo.utils;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class PadLightUtils {
  //开灯
  private static final String[] LED_ON_VAL = {
      "1", //红灯
      "3", //绿灯
      "5", //摄像头白灯
      "7", //摄像头红灯
      "11" //工作指示灯
  };
  private static final String LED_CTL_PATH = "/sys/class/zh_gpio_out/out";

  public static void setIRLight() {
    ctlLedRelay(LED_ON_VAL[3]);
    ctlLedRelay(LED_ON_VAL[2]);
  }

  private static void ctlLedRelay(String val) {
    File file = new File(LED_CTL_PATH);
    if (!file.exists() || !file.canWrite()) {
      Log.e("PadLightUtils", "LED ctl path is not exists or can not write!!");
      return;
    }
    try {
      FileOutputStream fout = new FileOutputStream(file);
      PrintWriter pWriter = new PrintWriter(fout);
      pWriter.println(val);
      pWriter.flush();
      pWriter.close();
      fout.close();
    } catch (IOException re) {
      Log.e("PadLightUtils", "write error:" + re);
    }
  }
}
