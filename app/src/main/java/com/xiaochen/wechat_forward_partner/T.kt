package com.xiaochen.wechat_forward_partner

import android.support.v4.util.Pair
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * Created by tanfujun on 6/7/17.
 */

class T {

    fun a() {
        Observable
                .just("yo")
                .flatMap<Any> { s ->
                    Observable.combineLatest(
                            Observable.just(s),
                            Observable.range(0, 100),
                            BiFunction<String, Int, Pair<String, Int>> { s, integer -> Pair(s, integer) })
                }
                .subscribe()
    }
}


