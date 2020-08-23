package com.rchen.xrrpc.protocol.codec;

import com.rchen.xrrpc.protocol.Packet;
import com.rchen.xrrpc.protocol.Protocol;
import com.rchen.xrrpc.protocol.Protocol.Command;
import com.rchen.xrrpc.protocol.Protocol.SerializerAlgorithm;
import com.rchen.xrrpc.protocol.codec.serialization.Serializer;
import com.rchen.xrrpc.protocol.codec.serialization.impl.JSONSerializer;
import com.rchen.xrrpc.protocol.codec.serialization.impl.ProtobufSerializer;
import com.rchen.xrrpc.protocol.request.RpcRequest;
import com.rchen.xrrpc.protocol.request.VerifyRequest;
import com.rchen.xrrpc.protocol.response.RpcResponse;
import com.rchen.xrrpc.protocol.response.VerifyResponse;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : crz
 * @Date: 2020/8/23
 */
public class PacketCodec {
    private static final int MAGIC_NUMBER = Protocol.MAGIC_NUMBER;

    /**
     * 单例模式
     */
    private static final PacketCodec INSTANCE = new PacketCodec();

    private final Map<Byte, Class<? extends Packet>> packetTypeMap;

    private final Map<Byte, Serializer> serializerMap;

    private PacketCodec() {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(Command.VERIFY_REQUEST, VerifyRequest.class);
        packetTypeMap.put(Command.VERIFY_RESPONSE, VerifyResponse.class);
        packetTypeMap.put(Command.RPC_REQUEST, RpcRequest.class);
        packetTypeMap.put(Command.RPC_RESPONSE, RpcResponse.class);

        serializerMap = new HashMap<>();
        serializerMap.put(SerializerAlgorithm.JSON, new JSONSerializer());
        serializerMap.put(SerializerAlgorithm.PROTOBUF, new ProtobufSerializer());
    }

    /**
     * 将 Packet 根据协议进行编码
     * @param byteBuf
     * @param packet
     */
    public void encode(ByteBuf byteBuf, Packet packet) {
        Serializer serializer = Serializer.DEFAULT;
        // 1. 序列化 Packet 对象
        byte[] bytes = serializer.serialize(packet);

        // 2. 构造 ByteBuf 对象
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(serializer.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }


    /**
     * 根据协议，从 ByteBuf 中解码 Packet
     * @param byteBuf
     * @return
     */
    public Packet decode(ByteBuf byteBuf) {
        // 跳过 MagicNumber
        byteBuf.skipBytes(4);

        // 跳过 版本号
        byteBuf.skipBytes(1);

        // 序列化算法
        byte serializerAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 长度
        int length = byteBuf.readInt();

        // 数据
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        // 反序列化
        Serializer serializer = getSerializer(serializerAlgorithm);
        Class<? extends Packet> packetType = getPacketType(command);

        if (packetType != null && serializer != null) {
            return serializer.deserialize(packetType, bytes);
        }
        return null;
    }

    private Serializer getSerializer(byte serializerAlgorithm) {
        return serializerMap.get(serializerAlgorithm);
    }

    private Class<? extends Packet> getPacketType(byte command) {
        return packetTypeMap.get(command);
    }
}
