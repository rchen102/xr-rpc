package com.rchen.xrrpc.protocol;

import lombok.Data;

/**
 * 客户端与服务端通信的基本数据包 Java 对象
 *
 * @Author : crz
 * @Date: 2020/8/23
 */
@Data
public abstract class Packet {

    /**
     * 协议版本
     */
    private Byte version = Protocol.version;

    /**
     * 该数据包中的指令类型
     * @return
     */
    public abstract Byte getCommand();
}
