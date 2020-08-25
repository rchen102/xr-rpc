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
public class VerifyRequest extends Packet {

    private String verifyCode;

    @Override
    public Byte getCommand() {
        return Command.VERIFY_REQUEST;
    }
}
