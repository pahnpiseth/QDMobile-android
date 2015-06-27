package com.qiyue.qdmobile;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.qiyue.qdmobile.utils.Constants;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by newsbeat on 6/27/15.
 */
public class BasFragmentActivity extends FragmentActivity {

    protected void initBarTintManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor(Constants.HOME_SCREEN_STATUS_BAR_COLOR));
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }



}
