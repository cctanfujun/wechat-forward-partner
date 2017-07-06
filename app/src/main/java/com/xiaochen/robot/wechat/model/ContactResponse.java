package com.xiaochen.robot.wechat.model;

import java.util.List;

/**
 * Created by tanfujun on 6/7/17.
 */

public class ContactResponse {



    public BaseResponseBean BaseResponse;
    public int MemberCount;
    public int Seq;
    public List<MemberListBean> MemberList;

    public static class BaseResponseBean {
        /**
         * Ret : 0
         * ErrMsg :
         */

        public int Ret;
        public String ErrMsg;
    }

    public static class MemberListBean {
        /**
         * Uin : 0
         * UserName : @db6e466467faee69d0e5a021e8e76e29
         * NickName : 小爽～
         * HeadImgUrl : /cgi-bin/mmwebwx-bin/webwxgeticon?seq=440049&username=@db6e466467faee69d0e5a021e8e76e29&skey=@crypt_30c2122e_e6729554b4f4853245682fd6ff0c840c
         * ContactFlag : 3
         * MemberCount : 0
         * MemberList : []
         * RemarkName : 二爽
         * HideInputBarFlag : 0
         * Sex : 0
         * Signature : 摧毁一切的时间，拯救一切的是记忆……
         * VerifyFlag : 0
         * OwnerUin : 0
         * PYInitial : XS
         * PYQuanPin : xiaoshuang
         * RemarkPYInitial : ES
         * RemarkPYQuanPin : ershuang
         * StarFriend : 0
         * AppAccountFlag : 0
         * Statues : 0
         * AttrStatus : 102587
         * Province : 湖北
         * City : 武汉
         * Alias :
         * SnsFlag : 0
         * UniFriend : 0
         * DisplayName :
         * ChatRoomId : 0
         * KeyWord : qq5
         * EncryChatRoomId :
         * IsOwner : 0
         */

        public int Uin;
        public String UserName;
        public String NickName;
        public String HeadImgUrl;
        public int ContactFlag;
        public int MemberCount;
        public String RemarkName;
        public int HideInputBarFlag;
        public int Sex;
        public String Signature;
        public int VerifyFlag;
        public int OwnerUin;
        public String PYInitial;
        public String PYQuanPin;
        public String RemarkPYInitial;
        public String RemarkPYQuanPin;
        public int StarFriend;
        public int AppAccountFlag;
        public int Statues;
        public long AttrStatus;
        public String Province;
        public String City;
        public String Alias;
        public int SnsFlag;
        public int UniFriend;
        public String DisplayName;
        public int ChatRoomId;
        public String KeyWord;
        public String EncryChatRoomId;
        public int IsOwner;
        public List<?> MemberList;
    }
}
