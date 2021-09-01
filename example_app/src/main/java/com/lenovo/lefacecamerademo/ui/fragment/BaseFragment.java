package com.lenovo.lefacecamerademo.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {
  /**
   * 权限检查
   *
   * @param neededPermissions 需要的权限
   * @return 是否全部被允许
   */
  protected boolean checkPermissions(String[] neededPermissions) {
    if (neededPermissions == null || neededPermissions.length == 0) {
      return true;
    }
    boolean allGranted = true;
    for (String neededPermission : neededPermissions) {
      allGranted &= ContextCompat.checkSelfPermission(getActivity(), neededPermission) == PackageManager.PERMISSION_GRANTED;
    }
    return allGranted;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    boolean isAllGranted = true;
    for (int grantResult : grantResults) {
      isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
    }
    afterRequestPermission(requestCode, isAllGranted);
  }

  /**
   * 请求权限的回调
   *
   * @param requestCode  请求码
   * @param isAllGranted 是否全部被同意
   */
  protected abstract void afterRequestPermission(int requestCode, boolean isAllGranted);

  protected void showToast(String s) {
    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
  }
  protected void showToastTop(String s) {
    Toast toast = Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.TOP,0,0);
    toast.show();
  }
  protected void showLongToastTop(String s) {
    Toast toast = Toast.makeText(getActivity(), s, Toast.LENGTH_LONG);
    toast.setGravity(Gravity.TOP,0,0);
    toast.show();
  }
  protected void showLongToast(String s) {
    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
  }

  public Context getMyContex()
  {
    return this.getActivity();
    //return this.getView().getContext();
  }
}
