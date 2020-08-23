package com.rchen.xrrpc.protocol.request;

import com.rchen.xrrpc.protocol.Packet;
import com.rchen.xrrpc.protocol.Protocol.Command;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class VerifyRequest extends Packet {
    @Override
    public Byte getCommand() {
        return Command.VERIFY_REQUEST;
    }
}
