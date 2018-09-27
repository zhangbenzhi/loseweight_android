package com.apicloud.qingNiuModule;

import android.app.Application;

import com.yolanda.health.qnblesdk.out.QNBleApi;

/**
 * Created by zhangbenzhi on 2018/3/26.
 */

public class QNBleApiHelper {

    private static QNBleApi qnBleApi;
    private static Application application;

    public synchronized static Application getApplication() {
        if (application == null) {
            try {
                application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return application;
    }


    public static QNBleApi getQnBleApi() {
        if (qnBleApi == null) {
            synchronized (QNBleApiHelper.class) {
                if (qnBleApi == null) {
                    qnBleApi = QNBleApi.getInstance(getApplication());
                }
            }
        }
        return qnBleApi;
    }

}
