package com.apicloud.qingNiuModule;


import android.util.Log;

import com.qingniu.qnble.utils.QNLogUtils;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.yolanda.health.qnblesdk.constant.QNIndicator;
import com.yolanda.health.qnblesdk.listen.QNBleConnectionChangeListener;
import com.yolanda.health.qnblesdk.listen.QNBleDeviceDiscoveryListener;
import com.yolanda.health.qnblesdk.listen.QNDataListener;
import com.yolanda.health.qnblesdk.listen.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBleDevice;
import com.yolanda.health.qnblesdk.out.QNScaleData;
import com.yolanda.health.qnblesdk.out.QNScaleItemData;
import com.yolanda.health.qnblesdk.out.QNScaleStoreData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.apicloud.qingNiuModule.Constant.GETTED_STORAGE_DATA;
import static com.apicloud.qingNiuModule.Constant.WEIGHT_MESUREING;
import static com.apicloud.qingNiuModule.Constant.isDebug;


public class APIModule extends UZModule {

    public APIModule(UZWebView webView) {
        super(webView);
    }

    public void jsmethod_test(final UZModuleContext uzModuleContext) {
        LogUtil.e("test");
    }

    public static ArrayList<QNBleDevice> qnBleDevices = new ArrayList<>();//存储获取到的蓝牙设备信息

    /**
     * 初始化体脂称sdk模块:
     *
     * @param uzModuleContext
     */
    public void jsmethod_init(final UZModuleContext uzModuleContext) {
        String appId = uzModuleContext.optString("appid");//从js获取轻牛appId;
        if (isDebug) {
            appId = "bjscwlkjgfyxgs20180328";
        }
        SDKUtils.initSDK(appId, new QNResultCallback() {
            @Override
            public void onResult(int code, String s) {
                LogUtil.e("init:onCompete " + s);
                String status;
                switch (code) {
                    case 0:
                        status = "0";
                        break;
                    default:
                        status = "1";
                        break;
                }
                JSONObject ret = new JSONObject();
                try {
                    ret.put("status", status);
                    ret.put("errorCode", code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                uzModuleContext.success(ret, true);
            }
        });
    }

    private static UZModuleContext mUzModuleContext;

    /**
     * 监听搜索到蓝牙设备及开始及停止搜索：
     */
    public static void setBleDeviceDiscoveryListener() {
        final JSONArray jsonArray = new JSONArray();
        final JSONObject ret = new JSONObject();
        QNBleApiHelper.getQnBleApi().setBleDeviceDiscoveryListener(null);
        QNBleApiHelper.getQnBleApi().setBleDeviceDiscoveryListener(new QNBleDeviceDiscoveryListener() {
            @Override
            public void onDeviceDiscover(QNBleDevice bleDevice) {
                try {
                    boolean isHave = false;
                    for (QNBleDevice qnBleDevice : qnBleDevices) {
                        if (qnBleDevice.getMac().equals(bleDevice.getMac())) {
                            isHave = true;
                            break;
                        }
                    }
                    if (!isHave) {
                        qnBleDevices.add(bleDevice);
                    }
                    jsonArray.put(jsonArray.length(),
                            new JSONObject()
                                    .put("name", bleDevice.getName())
                                    .put("uuid", bleDevice.getMac())
                                    .put("model", bleDevice.getBluetoothName())
                                    .put("deviceState", bleDevice.isScreenOn() ? "1" : "0"));
                    ret.put("status", "0");
                    ret.put("errorCode", "0");
                    ret.put("scanArray", jsonArray);
                    mUzModuleContext.success(ret, false);
                    LogUtil.e("scan:onScan " + jsonArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartScan() {
                LogUtil.e("开始搜索");
            }

            @Override
            public void onStopScan() {
                LogUtil.e("停止搜索");
            }
        });
    }

    /**
     * 扫描附近的体脂称设备:
     */
    public void jsmethod_scan(final UZModuleContext uzModuleContext) {
        mUzModuleContext = uzModuleContext;
        setBleDeviceDiscoveryListener();
        final JSONObject ret = new JSONObject();
        SDKUtils.scan(new QNResultCallback() {
            @Override
            public void onResult(int code, String s) {
                try {
                    String status;
                    switch (code) {
                        case 0:
                            status = "0";
                            break;
                        default:
                            status = "1";
                            break;
                    }
                    LogUtil.e("扫描成功，待返回数据");
                    ret.put("status", status);
                    ret.put("errorCode", code);
                    ret.put("scanArray", null);
                    uzModuleContext.success(ret, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 停止扫描：
     *
     * @param uzModuleContext
     */
    public void jsmethod_stopScan(final UZModuleContext uzModuleContext) {
        mUzModuleContext = uzModuleContext;
        setBleDeviceDiscoveryListener();
        SDKUtils.doStopScan(new QNResultCallback() {
            @Override
            public void onResult(int code, String s) {
                String status;
                switch (code) {
                    case 0:
                        status = "0";
                        break;
                    default:
                        status = "1";
                        break;
                }
                JSONObject ret = new JSONObject();
                try {
                    ret.put("status", status);
                    ret.put("errorCode", code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                uzModuleContext.success(ret, true);
            }
        });
    }

    /**
     * 连接设备:
     *
     * @param uzModuleContext
     */
    public void jsmethod_connect(final UZModuleContext uzModuleContext) {
        clearListener();
        String uuid;
        String userId;
        Date dateBirthday;
        int intHeight;
        int intGender;
        try {
            uuid = uzModuleContext.optString("uuid");
            userId = uzModuleContext.optString("userId");
            String height = uzModuleContext.optString("height");
            String gender = uzModuleContext.optString("gender");//性别0：女 1：男；
            String birthday = uzModuleContext.optString("birthday");//生日：格式2018-03-12
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateBirthday = dateFormat.parse(birthday);
            intHeight = Integer.parseInt(height);
            intGender = Integer.parseInt(gender);
        } catch (Exception e) {
            //参数传入错误
            sendData(uzModuleContext, "1", null, "传入参数类型转换错误", null, null);
            return;
        }

        if (uuid == null) {
            sendData(uzModuleContext, "1", null, "uuid不能为空", null, null);
            return;
        }
        QNBleApiHelper.getQnBleApi().setBleConnectionChangeListener(new QNBleConnectionChangeListener() {
            //正在连接
            @Override
            public void onConnecting(QNBleDevice device) {
                //sendData(uzModuleContext, "0", null, "0", "state", Constant.CONNECT_ING + "");
            }

            //已连接
            @Override
            public void onConnected(QNBleDevice device) {
                //sendData(uzModuleContext, "0", null, "0", "state", Constant.CONNCETED_SUCCESS + "");
            }

            @Override
            public void onServiceSearchComplete(QNBleDevice device) {

            }

            //正在断开连接，调用断开连接时，会马上回调
            @Override
            public void onDisconnecting(QNBleDevice device) {
                //sendData(uzModuleContext, "0", null, "0", "state", Constant.DISCONNECT_ING + "");
            }

            // 断开连接，断开连接后回调
            @Override
            public void onDisconnected(QNBleDevice device) {
                //sendData(uzModuleContext, "0", null, "0", "state", Constant.DISCONNECTED + "");
            }

            //出现了连接错误，错误码参考附表
            @Override
            public void onConnectError(QNBleDevice device, int errorCode) {

            }

            @Override
            public void onScaleStateChange(QNBleDevice device, int status) {
                sendData(uzModuleContext, "0", null, "0", "state", status + "");
            }
        });

        QNBleApiHelper.getQnBleApi().setDataListener(new QNDataListener() {
            @Override
            public void onGetUnsteadyWeight(QNBleDevice device, double weight) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("weight", weight + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendData(uzModuleContext, "0", jsonObject, "0", "state", WEIGHT_MESUREING + "");
            }

            @Override
            public void onGetScaleData(QNBleDevice device, QNScaleData qnData) {
                Log.d("ConnectActivity", "收到测量数据");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", qnData.getQnUser() != null ? qnData.getQnUser().getUserId() : null);
                    jsonObject.put("weight", qnData.getItem(QNIndicator.TYPE_WEIGHT) != null ? qnData.getItem(QNIndicator.TYPE_WEIGHT).getValue() : null);
                    jsonObject.put("bmi", qnData.getItem(QNIndicator.TYPE_BMI) != null ? qnData.getItem(QNIndicator.TYPE_BMI).getValue() : null);
                    jsonObject.put("bodyage", qnData.getItem(QNIndicator.TYPE_BODY_AGE) != null ? qnData.getItem(QNIndicator.TYPE_BODY_AGE).getValue() : null);
                    jsonObject.put("bodyfat", qnData.getItem(QNIndicator.TYPE_BODYFAT) != null ? qnData.getItem(QNIndicator.TYPE_BODYFAT).getValue() : null);
                    jsonObject.put("subfat", qnData.getItem(QNIndicator.TYPE_SUBFAT) != null ? qnData.getItem(QNIndicator.TYPE_SUBFAT).getValue() : null);
                    jsonObject.put("visfat", qnData.getItem(QNIndicator.TYPE_VISFAT) != null ? qnData.getItem(QNIndicator.TYPE_VISFAT).getValue() : null);
                    jsonObject.put("water", qnData.getItem(QNIndicator.TYPE_WATER) != null ? qnData.getItem(QNIndicator.TYPE_WATER).getValue() : null);
                    jsonObject.put("muscle", qnData.getItem(QNIndicator.TYPE_MUSCLE) != null ? qnData.getItem(QNIndicator.TYPE_MUSCLE).getValue() : null);
                    jsonObject.put("bone", qnData.getItem(QNIndicator.TYPE_BONE) != null ? qnData.getItem(QNIndicator.TYPE_BONE).getValue() : null);
                    jsonObject.put("bmr", qnData.getItem(QNIndicator.TYPE_BMR) != null ? qnData.getItem(QNIndicator.TYPE_BMR).getValue() : null);
                    jsonObject.put("bodyType", qnData.getItem(QNIndicator.TYPE_BODY_SHAPE) != null ? qnData.getItem(QNIndicator.TYPE_BODY_SHAPE).getValue() : null);
                    jsonObject.put("protein", qnData.getItem(QNIndicator.TYPE_PROTEIN) != null ? qnData.getItem(QNIndicator.TYPE_PROTEIN).getValue() : null);
                    jsonObject.put("lbm", qnData.getItem(QNIndicator.TYPE_LBM) != null ? qnData.getItem(QNIndicator.TYPE_LBM).getValue() : null);
                    jsonObject.put("muscle_mass", qnData.getItem(QNIndicator.TYPE_MUSCLE_MASS) != null ? qnData.getItem(QNIndicator.TYPE_MUSCLE_MASS).getValue() : null);
                    jsonObject.put("score", qnData.getItem(QNIndicator.TYPE_SCORE) != null ? qnData.getItem(QNIndicator.TYPE_SCORE).getValue() : null);
                    jsonObject.put("heart_rate", qnData.getItem(QNIndicator.TYPE_HEART_RATE) != null ? qnData.getItem(QNIndicator.TYPE_HEART_RATE).getValue() : null);
                    jsonObject.put("heart_index", qnData.getItem(QNIndicator.TYPE_HEART_INDEX) != null ? qnData.getItem(QNIndicator.TYPE_HEART_INDEX).getValue() : null);
                    sendData(uzModuleContext, "0", jsonObject, "0", "state", GETTED_STORAGE_DATA + "");
                } catch (Exception e) {
                }
            }

            @Override
            public void onGetStoredScale(QNBleDevice device, List<QNScaleStoreData> storedDataList) {
            }
        });

        QNBleDevice myQnBleDevice = null;
        for (QNBleDevice qnBleDevice : qnBleDevices) {
            if (uuid.equals(qnBleDevice.getMac())) {
                myQnBleDevice = qnBleDevice;
                break;
            }
        }

        if (myQnBleDevice == null) {
            //未找到该设备
            sendData(uzModuleContext, "1", null, "-1", null, null);
            return;
        }

        SDKUtils.connect(myQnBleDevice, userId, intHeight, intGender, dateBirthday, new QNResultCallback() {
            @Override
            public void onResult(int code, String s) {
                String status = "";
                switch (code) {
                    case 0:
                        status = "0";
                        break;
                    default:
                        status = "1";
                        break;
                }
                sendData(uzModuleContext, status, null, code + "", null, null);
            }
        });
    }

    public void sendData(UZModuleContext uzModuleContext, String status, JSONObject data, String errorCode, String key, String value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", status);
            jsonObject.put("errorCode", errorCode);
            if (key != null) {
                jsonObject.put(key, value + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (data != null) {
            try {
                jsonObject.put("scaleData", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        uzModuleContext.success(jsonObject, false);
    }

    private static void clearListener() {
        QNBleApiHelper.getQnBleApi().setBleConnectionChangeListener(null);
        QNBleApiHelper.getQnBleApi().setDataListener(null);
    }

    /**
     * 断开连接：
     *
     * @param uzModuleContext
     */
    public void jsmethod_disConnect(final UZModuleContext uzModuleContext) {
        clearListener();
        String mac = uzModuleContext.optString("uuid");
        SDKUtils.disConnect(mac, new QNResultCallback() {
            @Override
            public void onResult(int code, String s) {
                String status;
                switch (code) {
                    case 0:
                        status = "0";
                        break;
                    default:
                        status = "1";
                        break;
                }
                JSONObject ret = new JSONObject();
                try {
                    ret.put("status", status);
                    ret.put("errorCode", code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                uzModuleContext.success(ret, true);
            }
        });
    }

    /**
     * 蓝牙状态：
     *
     * @param uzModuleContext
     */
    public void jsmethod_bluetoothStatus(final UZModuleContext uzModuleContext) {
        LogUtil.e("bluetoothStatus开始");
        //1：蓝牙已关闭
        //2：蓝牙已开启
        //3：设备不支持蓝牙
        //4：状态未知
        String state = "2";
        boolean bluetoothSupported = BLEHelper.isBluetoothSupported();
        boolean bluetoothEnabled = BLEHelper.isBluetoothEnabled();
        if (!bluetoothSupported) {
            state = "3";
        } else if (bluetoothEnabled) {
            state = "2";
        } else {
            state = "1";
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", "0");
            jsonObject.put("errorCode", "0");
            jsonObject.put("state", state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uzModuleContext.success(jsonObject, true);
    }

    /**
     * 开启蓝牙：
     *
     * @param uzModuleContext
     */
    public void jsmethod_turnOnBluetooth(UZModuleContext uzModuleContext) {
        boolean b = BLEHelper.turnOnBluetooth();
        JSONObject jsonObject = new JSONObject();
        //1：蓝牙已关闭
        //2：蓝牙已开启
        String status = "1";
        if (b) {
            status = "2";
        }
        try {
            jsonObject.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("turnOnBluetooth:status " + status);
        uzModuleContext.success(jsonObject, true);
    }

}
