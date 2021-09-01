package com.lenovo.lefacecamerademo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
  public static void saveBitmap(String path,String name, Bitmap bm) {
    if (!fileIsExist(path)) {
    } else {
      File saveFile = new File(path, name);
      try {
        FileOutputStream saveImgOut = new FileOutputStream(saveFile);
        bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
        saveImgOut.flush();
        saveImgOut.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  static boolean fileIsExist(String fileName)
  {
    File file=new File(fileName);
    if (file.exists())
      return true;
    else{
      Log.i("fileIsExist", "makdirs="+fileName);
      return file.mkdirs();
    }
  }


}
