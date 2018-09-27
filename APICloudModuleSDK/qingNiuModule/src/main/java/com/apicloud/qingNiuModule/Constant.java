package com.apicloud.qingNiuModule;

/**
 * Created by admin on 2018/3/25.
 */

public class Constant {

    public static final boolean isDebug = false;

    //0：传入连接参数错误
    public static final int ERROR_PARAMS = 0;
    //1：连接设备失败
    public static final int ERROR_CONNECT = 1;
    //2：查找设备的服务或者特征失败
    public static final int FAILURE_FIND_SERVICE = 2;
    //3：接收到的数据出错（重新连接）
    public static final int ERROR_GET_DATA = 3;
    //4：正在测量
    public static final int MESURING = 4;
    //5：测量完毕
    public static final int MESURED = 5;
    //6：正在获取存储数据
    public static final int GETTING_STORAGE_DATA = 6;
    //7：获取完所有的存储数据
    public static final int GETTED_STORAGE_DATA = 10;
    //8：测量完毕后自动断开了连接
    public static final int DISCONNECTED = -1;
    /**
     * 正在断开连接：
     */
    public static final int DISCONNECT_ING = 3;
    //正在连接
    public static final int CONNECT_ING = 2;
    //连接成功时候的回调
    public static final int CONNCETED_SUCCESS = 1;
    //设备端低电压
    public static final int ON_LOW_POWER = 10;

    //正在测量体重
    public static final int WEIGHT_MESUREING = 5;

}
