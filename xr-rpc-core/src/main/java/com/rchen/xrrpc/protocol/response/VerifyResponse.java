package com.rchen.xrrpc.protocol.response;

import com.rchen.xrrpc.protocol.Packet;
import com.rchen.xrrpc.protocol.Protocol.Command;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class VerifyResponse extends Packet {
    @Override
    public Byte getCommand() {
        return Command.VERIFY_RESPONSE;
    }
}
