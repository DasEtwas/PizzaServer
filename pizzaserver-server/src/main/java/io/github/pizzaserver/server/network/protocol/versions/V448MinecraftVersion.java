package io.github.pizzaserver.server.network.protocol.versions;

import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v448.Bedrock_v448;

import java.io.IOException;

public class V448MinecraftVersion extends V440MinecraftVersion {

    public static final int PROTOCOL = 448;
    public static final String VERSION = "1.17.10";


    public V448MinecraftVersion() throws IOException {}

    @Override
    public int getProtocol() {
        return PROTOCOL;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public BedrockPacketCodec getPacketCodec() {
        return Bedrock_v448.V448_CODEC;
    }

}