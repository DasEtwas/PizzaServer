package io.github.willqi.pizzaserver.server.network.protocol.versions.v419.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.willqi.pizzaserver.format.mcworld.utils.VarInts;
import io.github.willqi.pizzaserver.server.network.protocol.data.PlayerAnimateAction;
import io.github.willqi.pizzaserver.server.network.protocol.packets.PlayerAnimatePacket;
import io.github.willqi.pizzaserver.server.network.protocol.versions.BasePacketHelper;
import io.github.willqi.pizzaserver.server.network.protocol.versions.BaseProtocolPacketHandler;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;

public class V419PlayerAnimatePacketHandler extends BaseProtocolPacketHandler<PlayerAnimatePacket> {

    protected final BiMap<PlayerAnimateAction, Integer> actions = HashBiMap.create(new HashMap<PlayerAnimateAction, Integer>() {
        {
            this.put(PlayerAnimateAction.NO_ACTION, 0);
            this.put(PlayerAnimateAction.SWING_ARM, 1);
            this.put(PlayerAnimateAction.WAKE_UP, 3);
            this.put(PlayerAnimateAction.CRITICAL_HIT, 4);
            this.put(PlayerAnimateAction.MAGIC_CRITICAL_HIT, 5);
            this.put(PlayerAnimateAction.ROW_RIGHT, 128);
            this.put(PlayerAnimateAction.ROW_LEFT, 129);
        }
    });

    @Override
    public PlayerAnimatePacket decode(ByteBuf buffer, BasePacketHelper helper) {
        PlayerAnimatePacket playerAnimatePacket = new PlayerAnimatePacket();
        PlayerAnimateAction action = actions.inverse().get(VarInts.readInt(buffer));
        playerAnimatePacket.setAction(action);
        playerAnimatePacket.setEntityRuntimeID(VarInts.readLong(buffer));
        if(action == PlayerAnimateAction.ROW_LEFT || action == PlayerAnimateAction.ROW_RIGHT) playerAnimatePacket.setRowingTime(buffer.readFloatLE());
        return playerAnimatePacket;
    }

    @Override
    public void encode(PlayerAnimatePacket packet, ByteBuf buffer, BasePacketHelper helper) {
        VarInts.writeInt(buffer, actions.get(packet.getAction()));
        VarInts.writeLong(buffer, packet.getEntityRuntimeID());
        if(packet.getAction() == PlayerAnimateAction.ROW_LEFT || packet.getAction() == PlayerAnimateAction.ROW_RIGHT) buffer.writeFloatLE(packet.getRowingTime());
    }

}