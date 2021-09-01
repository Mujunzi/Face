package com.lenovo.lefacecamerademo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import android.view.View;
import android.widget.Switch;
import com.lenovo.lefacesdk.Rect;

import java.util.ArrayList;
import java.util.List;

public class OverlayView extends View {
  private float displayRatio = 1f;
  private Paint detectionPaint, textPaint;
  private List<RectF> detectionResults;
  private String mUinfo = "";
  private float mDensity = 2.0f;
  private float mTextSize = 24f;

  public static final int GREEN=1;
  public static final int RED=2;
  public static final int BLUE=3;


  public OverlayView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setWillNotDraw(false);
    detectionPaint = new Paint();
    detectionPaint.setColor(Color.GREEN);
    detectionPaint.setStyle(Paint.Style.STROKE);
    detectionPaint.setStrokeWidth(4.0f);

    textPaint = new Paint();
    textPaint.setColor(Color.GREEN);
    textPaint.setTextSize(mTextSize * mDensity);
    textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
  }

  /**
   * Update the rect info and the device orientation so it can be drawn properly
   */
  public void setDetectionRect(List<Rect> locations) {
    detectionResults = new ArrayList<>();
    for (Rect loc : locations) {
      //System.out.println("loc coordinates: left: "+loc.x+" top: "+ loc.y+" width: "+loc.width+" height: "+loc.height);
      int left, right, top, bottom;
      left = (int) (loc.x * displayRatio);
      top = (int) (loc.y * displayRatio);
      right = (int) ((loc.width + loc.x) * displayRatio);
      bottom = (int) ((loc.height + loc.y) * displayRatio);

      RectF facerect_overlay = new RectF(left, top, right, bottom);
      //System.out.println("facerect_overlay coordinates: left: "+facerect_overlay.left+" top: "+facerect_overlay.top+" right: "+facerect_overlay.right+" bottom: "+facerect_overlay.bottom);

      detectionResults.add(facerect_overlay);
    }
  }
  public void setDetectionRect(Rect loc) {
    detectionResults = new ArrayList<>();
    //for (Rect loc : locations) {
      //System.out.println("loc coordinates: left: "+loc.x+" top: "+ loc.y+" width: "+loc.width+" height: "+loc.height);
      int left, right, top, bottom;
      left = (int) (loc.x * displayRatio);
      top = (int) (loc.y * displayRatio);
      right = (int) ((loc.width + loc.x) * displayRatio);
      bottom = (int) ((loc.height + loc.y) * displayRatio);

      RectF facerect_overlay = new RectF(left, top, right, bottom);
      //System.out.println("facerect_overlay coordinates: left: "+facerect_overlay.left+" top: "+facerect_overlay.top+" right: "+facerect_overlay.right+" bottom: "+facerect_overlay.bottom);

      detectionResults.add(facerect_overlay);
    //}
  }
  public void setUserInfo(String userInfo)
  {
    mUinfo = userInfo;
  }
  @Override
  public void onDraw(Canvas canvas) {
    if (detectionResults != null) {
      for (RectF facerect : detectionResults) {
        /** draw rectangles **/
        canvas.drawRect(facerect, detectionPaint);
        /** put text **/
        //mUinfo = String.valueOf((int) facerect.width());
        canvas.drawText(mUinfo,
              facerect.left, facerect.top - textPaint.getTextSize(), textPaint);
        //String dist = String.format("%.2f",distanceFromRect(facerect.width()));
        //canvas.drawText(dist+" m", facerect.left, facerect.top - textPaint.getTextSize(), textPaint);
      }
    }
  }

  public void drawOverlay(int detectCode) {
    switch (detectCode){
      case RED:
        detectionPaint.setColor(Color.RED);
        textPaint.setColor(Color.RED);
        break;
      case GREEN:
        detectionPaint.setColor(Color.GREEN);
        textPaint.setColor(Color.GREEN);
        break;
      case BLUE:
        detectionPaint.setColor(Color.BLUE);
        textPaint.setColor(Color.BLUE);
        break;

    }
    postInvalidate();
  }


  public void clearOverlay() {
    detectionResults = new ArrayList<>();
    postInvalidate();
  }
}
