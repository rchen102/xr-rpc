package com.rchen.xrrpc.config;

import com.rchen.xrrpc.util.SHAUtil;

/**
 * @Author : crz
 * @Date: 2020/8/24
 */
public class ClientConfig {
    public static String VERIFY_CODE;

    public static long RPC_MAX_TIMEOUT;

    public void setVerifyCode(String code) {
        VERIFY_CODE = SHAUtil.getSHA256(code);
    }

    public void setRpcMaxTimeout(long timeout) {
        RPC_MAX_TIMEOUT = timeout;
    }
}
