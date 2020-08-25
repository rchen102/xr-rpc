package com.rchen.xrrpc.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用于加密敏感信息，如身份验证信息
 *
 * @Author : crz
 * @Date: 2020/8/24
 */
public class SHAUtil {
    public static String getSHA256(String str) {
        MessageDigest md;
        String encodestr = "";
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
