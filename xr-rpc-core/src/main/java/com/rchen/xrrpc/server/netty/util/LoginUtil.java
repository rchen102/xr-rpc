package com.rchen.xrrpc.server.netty.util;


import com.rchen.xrrpc.server.netty.attr.Attributes;
import io.netty.channel.Channel;
import io.netty.util.Attribute;


/**
 * @Author : crz
 * @Date: 2020/8/24
 */
public class LoginUtil {
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        Attribute<Boolean> loginAttr = channel.attr(Attributes.LOGIN);
        return loginAttr.get() != null;
    }
}
