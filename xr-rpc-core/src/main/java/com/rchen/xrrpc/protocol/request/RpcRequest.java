package com.rchen.xrrpc.protocol.request;

import com.rchen.xrrpc.protocol.Packet;
import com.rchen.xrrpc.protocol.Protocol.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
@Data
@Builder
@AllArgsConstructor
public class RpcRequest extends Packet {

    /**
     * RPC request id，唯一标识
     */
    private String requestId;
    /**
     * interface name + version
     */
    private String serviceName;

    /**
     * 具体的 RPC 方法
     */
    private String methodName;

    /**
     * RPC 参数类型
     */
    private Class<?>[] paramsType;

    /**
     * RPC 具体参数
     */
    private Object[] params;


    @Override
    public Byte getCommand() {
        return Command.RPC_REQUEST;
    }
}
