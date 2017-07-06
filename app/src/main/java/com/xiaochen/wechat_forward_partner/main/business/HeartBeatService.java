package com.xiaochen.wechat_forward_partner.main.business;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by tanfujun on 7/6/17.
 */

public class HeartBeatService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
