package com.easemob.livestream.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import app.config.DemoApplication;


/**
 * Created by wei on 2016/6/2.
 */
public class Utils {
    /**
     * 隐藏键盘
     * @param view
     */
    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) DemoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     * @param view
     */
    public static void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) DemoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
