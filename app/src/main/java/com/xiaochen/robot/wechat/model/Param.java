package com.xiaochen.robot.wechat.model;

/**
 * Created by tanfujun on 5/27/17.
 */

public class Param {

    /**
     * BaseRequest : {"Uin":"xxx","Sid":"xxx","Skey":"xxx","DeviceID":"xxx"}
     */

    public BaseRequestBean BaseRequest = new BaseRequestBean();

    public static class BaseRequestBean {
        /**
         * Uin : xxx
         * Sid : xxx
         * Skey : xxx
         * DeviceID : xxx
         */

        public String Uin;
        public String Sid;
        public String Skey;
        public String DeviceID;
    }
}
