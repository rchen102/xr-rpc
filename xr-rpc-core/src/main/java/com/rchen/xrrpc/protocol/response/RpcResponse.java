package com.rchen.xrrpc.protocol.response;

import com.rchen.xrrpc.protocol.Packet;
import com.rchen.xrrpc.protocol.Protocol.Command;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@Getter
@Setter
public class RpcResponse extends Packet {

    /**
     * RPC request id，唯一标识
     */
    private String requestId;

    private Exception exception;

    private Object result;

    @Override
    public Byte getCommand() {
        return Command.RPC_RESPONSE;
    }
}
