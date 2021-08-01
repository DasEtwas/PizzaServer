package io.github.willqi.pizzaserver.server.network.protocol.packets;

public class AdventureSettingsPacket extends ImplBedrockPacket {

    public static final int ID = 0x37;

    public static final int FLAGS_FLYING_TEMP = 0x200 | 0x40;

    protected long eid;

    public AdventureSettingsPacket(long eid) {
        super(ID);
        this.eid = eid;
    }

    public long getEntityID() {
        return eid;
    }
}
