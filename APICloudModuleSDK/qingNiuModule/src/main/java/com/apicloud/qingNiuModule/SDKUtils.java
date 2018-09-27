package com.apicloud.qingNiuModule;

import android.widget.Toast;

import com.yolanda.health.qnblesdk.listen.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBleApi;
import com.yolanda.health.qnblesdk.out.QNBleDevice;
import com.yolanda.health.qnblesdk.out.QNConfig;
import com.yolanda.health.qnblesdk.out.QNUser;

import java.util.Date;


/**
 * Created by admin on 2018/3/25.
 */

public class SDKUtils {


    /**
     * 初始化SDK:
     *
     * @param appid
     * @param callback
     */
    public static void initSDK(String appid, QNResultCallback callback) {
        //123456789是测试版的appid
        if (appid == null) {
            Toast.makeText(QNBleApiHelper.getApplication(), "appId为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String encryptPath = "file:///android_asset/bjscwlkjgfyxgs20180328.qn";
        QNBleApiHelper.getQnBleApi().initSdk(appid, encryptPath, callback);
    }

    /**
     * 搜索设备：
     *
     * @param callback
     */
    public static void scan(QNResultCallback callback) {
        QNBleApiHelper.getQnBleApi().startBleDeviceDiscovery(getQNConfig(), callback);
    }

    /**
     * 获取配置信息
     *
     * @return
     */
    public static QNConfig getQNConfig() {
        QNConfig qnConfig = new QNConfig();
        return qnConfig;
    }

    /**
     * 停止搜索设备：
     */
    public static void doStopScan(QNResultCallback qnResultCallback) {
        QNBleApiHelper.getQnBleApi().stopBleDeviceDiscovery(qnResultCallback);
    }

    /**
     * 连接设备：
     *
     * @param userId
     * @param height
     * @param gender
     * @param birthday
     * @param callback
     */
    public static void connect(QNBleDevice qnBleDevice, String userId, int height, int gender
            , Date birthday, final QNResultCallback callback) {
        QNUser qnUser = QNBleApiHelper.getQnBleApi().buildUser(userId, height, gender == 1 ? "male" : "female", birthday, new QNResultCallback() {
            @Override
            public void onResult(int i, String s) {

            }
        });
        QNBleApiHelper.getQnBleApi().connectDevice(qnBleDevice, qnUser, callback);
    }

    /**
     * 断开链接：
     */
    public static void disConnect(String mac, QNResultCallback qnResultCallback) {
        QNBleApiHelper.getQnBleApi().disconnectDevice(mac, qnResultCallback);
    }
}
