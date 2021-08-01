package io.github.willqi.pizzaserver.server.network.protocol.versions.v419.handlers;

import io.github.willqi.pizzaserver.format.mcworld.utils.VarInts;
import io.github.willqi.pizzaserver.server.network.protocol.packets.AdventureSettingsPacket;
import io.github.willqi.pizzaserver.server.network.protocol.versions.BasePacketHelper;
import io.github.willqi.pizzaserver.server.network.protocol.versions.BaseProtocolPacketHandler;
import io.netty.buffer.ByteBuf;

public class V419AdventureSettingsPacketHandler extends BaseProtocolPacketHandler<AdventureSettingsPacket> {

    @Override
    public void encode(AdventureSettingsPacket packet, ByteBuf buffer, BasePacketHelper helper) {
        VarInts.writeUnsignedInt(buffer, AdventureSettingsPacket.FLAGS_FLYING_TEMP);
        VarInts.writeUnsignedInt(buffer, 4);
        VarInts.writeUnsignedInt(buffer, 0);
        VarInts.writeUnsignedInt(buffer, 4);
        VarInts.writeUnsignedInt(buffer, 0);
        buffer.writeLongLE(packet.getEntityID());
    }

    @Override
    public AdventureSettingsPacket decode(ByteBuf buffer, BasePacketHelper helper) {
        // *Jazz Music*
        VarInts.readUnsignedInt(buffer);
        VarInts.readUnsignedInt(buffer);
        VarInts.readUnsignedInt(buffer);
        VarInts.readUnsignedInt(buffer);
        VarInts.readUnsignedInt(buffer);
        return new AdventureSettingsPacket(buffer.readLongLE());
    }
}
