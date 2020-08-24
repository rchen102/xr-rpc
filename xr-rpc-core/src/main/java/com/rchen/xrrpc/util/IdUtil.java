package com.rchen.xrrpc.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class IdUtil {
    public static AtomicInteger idCounter = new AtomicInteger();

    /**
     * 返回自增的 id，由于存在线程安全问题，因此 counter 是 atomic 类型
     * 为了防止 id 是取负值，需要将返回结果与 0x7FFFFFFF 做按位与操作，
     * 因此 id 取值范围是 [ 0, 2^31 - 1 ]，当 id 达到最大值，会重新从 0 开始自增
     * @return
     */
    public static int nextId() {
        return idCounter.getAndIncrement() & 0x7FFFFFFF;
    }
}
