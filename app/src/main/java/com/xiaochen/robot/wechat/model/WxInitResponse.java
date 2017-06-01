package com.xiaochen.robot.wechat.model;

import java.util.List;

/**
 * Created by tanfujun on 6/2/17.
 */

public class WxInitResponse {

    /**
     * BaseResponse : {"Ret":0,"ErrMsg":""}
     * Count : 11
     * ContactList : ["..."]
     * SyncKey : {"Count":4,"List":[{"Key":1,"Val":635705559},"..."]}
     * User : {"Uin":"xxx","UserName":"xxx","NickName":"xxx","HeadImgUrl":"xxx","RemarkName":"","PYInitial":"","PYQuanPin":"","RemarkPYInitial":"","RemarkPYQuanPin":"","HideInputBarFlag":0,"StarFriend":0,"Sex":1,"Signature":"Apt-get install B","AppAccountFlag":0,"VerifyFlag":0,"ContactFlag":0,"WebWxPluginSwitch":0,"HeadImgFlag":1,"SnsFlag":17}
     * ChatSet : xxx
     * SKey : xxx
     * ClientVersion : 369297683
     * SystemTime : 1453124908
     * GrayScale : 1
     * InviteStartCount : 40
     * MPSubscribeMsgCount : 2
     * MPSubscribeMsgList : ["..."]
     * ClickReportInterval : 600000
     */

    public BaseResponseBean BaseResponse;
    public int Count;
    public SyncKeyBean SyncKey;
    public UserBean User;
    public String ChatSet;
    public String SKey;
    public int ClientVersion;
    public int SystemTime;
    public int GrayScale;
    public int InviteStartCount;
    public int MPSubscribeMsgCount;
    public int ClickReportInterval;
    public List<String> ContactList;
    public List<String> MPSubscribeMsgList;

    public static class BaseResponseBean {
        /**
         * Ret : 0
         * ErrMsg :
         */

        public int Ret;
        public String ErrMsg;
    }

    public static class SyncKeyBean {
        /**
         * Count : 4
         * List : [{"Key":1,"Val":635705559},"..."]
         */

        public int Count;
        public java.util.List<ListBean> List;

        public static class ListBean {
            /**
             * Key : 1
             * Val : 635705559
             */

            public int Key;
            public int Val;
        }
    }

    public static class UserBean {
        /**
         * Uin : xxx
         * UserName : xxx
         * NickName : xxx
         * HeadImgUrl : xxx
         * RemarkName :
         * PYInitial :
         * PYQuanPin :
         * RemarkPYInitial :
         * RemarkPYQuanPin :
         * HideInputBarFlag : 0
         * StarFriend : 0
         * Sex : 1
         * Signature : Apt-get install B
         * AppAccountFlag : 0
         * VerifyFlag : 0
         * ContactFlag : 0
         * WebWxPluginSwitch : 0
         * HeadImgFlag : 1
         * SnsFlag : 17
         */

        public String Uin;
        public String UserName;
        public String NickName;
        public String HeadImgUrl;
        public String RemarkName;
        public String PYInitial;
        public String PYQuanPin;
        public String RemarkPYInitial;
        public String RemarkPYQuanPin;
        public int HideInputBarFlag;
        public int StarFriend;
        public int Sex;
        public String Signature;
        public int AppAccountFlag;
        public int VerifyFlag;
        public int ContactFlag;
        public int WebWxPluginSwitch;
        public int HeadImgFlag;
        public int SnsFlag;
    }
}
