package com.rchen.xrrpc.protocol;

/**
 * 客户端与服务端的交互协议
 * | MagicNumber | Version | Serialization | Command | Length of packet | Packet |
 * |    4 byte   | 1 byte  |     1 byte    | 1 byte  |     4 byte       | N byte |
 *
 * @Author : crz
 * @Date: 2020/8/23
 */
public interface Protocol {

    /**
     * 数据包长度位的偏移是 7 bytes
     */
    int LENGTH_FIELD_OFFSET = 7;

    /**
     * 数据包长度位需要占据 4 bytes
     */
    int LENGTH_FIELD_LENGTH = 4;

    int MAGIC_NUMBER = 0x12345678;

    Byte version = 1;

    interface SerializerAlgorithm {
        /**
         * JSON 序列化算法
         */
        Byte JSON = 1;

        /**
         * Protobuf 序列化算法
         */
        Byte PROTOBUF = 2;
    }

    interface Command {
        /**
         * 安全验证请求
         */
        Byte VERIFY_REQUEST = 1;

        /**
         * 安全验证回复
         */
        Byte VERIFY_RESPONSE = 2;

        /**
         * RPC 请求
         */
        Byte RPC_REQUEST = 3;

        /**
         * RPC 回复
         */
        Byte RPC_RESPONSE = 4;
    }
}
