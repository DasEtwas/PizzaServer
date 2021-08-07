package io.github.willqi.pizzaserver.server.entity;

import io.github.willqi.pizzaserver.api.entity.Entity;
import io.github.willqi.pizzaserver.api.entity.meta.EntityMetaData;
import io.github.willqi.pizzaserver.api.player.Player;
import io.github.willqi.pizzaserver.api.utils.Location;
import io.github.willqi.pizzaserver.api.world.chunks.Chunk;
import io.github.willqi.pizzaserver.commons.utils.NumberUtils;
import io.github.willqi.pizzaserver.server.entity.meta.ImplEntityMetaData;
import io.github.willqi.pizzaserver.server.network.protocol.packets.RemoveEntityPacket;
import io.github.willqi.pizzaserver.server.network.protocol.packets.SetEntityDataPacket;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseEntity implements Entity {

    public static long ID;


    private final long id;
    private final Set<Player> spawnedTo = new HashSet<>();
    protected boolean spawned;

    private Location location = null;

    private EntityMetaData metaData = new ImplEntityMetaData();


    public BaseEntity() {
        this.id = ID++;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public Chunk getChunk() {
        return this.location.getChunk();
    }

    @Override
    public void setLocation(Location newLocation) {
        if (this.location != null) {
            Chunk oldChunk = this.getChunk();
            Chunk newChunk = newLocation.getChunk();

            this.location = newLocation;
            if (!oldChunk.equals(newChunk)) {
                oldChunk.removeEntity(this);
                newChunk.addEntity(this);
            }
        } else {
            this.location = newLocation;
            this.location.getChunk().addEntity(this);
        }
    }

    @Override
    public EntityMetaData getMetaData() {
        return this.metaData;
    }

    @Override
    public void setMetaData(EntityMetaData metaData) {
        this.metaData = metaData;

        SetEntityDataPacket entityDataPacket = new SetEntityDataPacket();
        entityDataPacket.setRuntimeId(this.getId());
        entityDataPacket.setData(this.getMetaData());
        for (Player player : this.getViewers()) {
            player.sendPacket(entityDataPacket);
        }
    }

    /**
     * Called when the entity is initially spawned into a world.
     * This is useful for entity initialization.
     */
    public void onSpawned() {
        this.spawned = true;
    }

    @Override
    public boolean hasSpawned() {
        return this.spawned;
    }

    @Override
    public boolean hasSpawnedTo(Player player) {
        return this.spawnedTo.contains(player);
    }

    @Override
    public void spawnTo(Player player) {
        this.spawnedTo.add(player);
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.spawnedTo.remove(player)) {
            RemoveEntityPacket entityPacket = new RemoveEntityPacket();
            entityPacket.setUniqueEntityId(this.getId());
            player.sendPacket(entityPacket);
        }
    }

    @Override
    public Set<Player> getViewers() {
        return this.spawnedTo;
    }

    @Override
    public int hashCode() {
        return 43 * (int)this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseEntity) {
            return NumberUtils.isNearlyEqual(((BaseEntity)obj).getId(), this.getId());
        }
        return false;
    }
}
