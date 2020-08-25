package com.rchen.xrrpc.protocol.response;

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
public class VerifyResponse extends Packet {

    private boolean isSuccess;

    private String errorMsg;

    @Override
    public Byte getCommand() {
        return Command.VERIFY_RESPONSE;
    }
}
