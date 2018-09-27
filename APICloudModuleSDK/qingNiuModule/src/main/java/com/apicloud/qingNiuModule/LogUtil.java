package com.apicloud.qingNiuModule;

import android.util.Log;

import static com.apicloud.qingNiuModule.Constant.isDebug;

/**
 * Created by admin on 2018/3/28.
 */

public class LogUtil {

    public static void e(String msg) {
        if (isDebug) {
            Log.e("TAG", msg);
        }
    }
}
