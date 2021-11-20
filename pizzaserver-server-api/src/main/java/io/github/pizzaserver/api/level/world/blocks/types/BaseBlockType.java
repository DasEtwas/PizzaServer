package io.github.pizzaserver.api.level.world.blocks.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nukkitx.nbt.NbtMap;
import io.github.pizzaserver.api.item.data.ToolType;
import io.github.pizzaserver.api.item.data.ToolTypeID;
import io.github.pizzaserver.api.level.world.blocks.Block;
import io.github.pizzaserver.api.level.world.blocks.BlockLoot;
import io.github.pizzaserver.api.level.world.blocks.BlockRegistry;
import io.github.pizzaserver.api.level.world.blocks.types.data.PushResponse;
import io.github.pizzaserver.api.entity.Entity;
import io.github.pizzaserver.api.item.ItemRegistry;
import io.github.pizzaserver.api.item.ItemStack;
import io.github.pizzaserver.api.item.ToolTypeRegistry;
import io.github.pizzaserver.api.player.Player;
import io.github.pizzaserver.api.utils.BoundingBox;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class BaseBlockType implements BlockType {

    private static final HashBiMap<NbtMap, Integer> EMPTY_BLOCK_STATES = HashBiMap.create(new HashMap<NbtMap, Integer>() {
        {
            this.put(NbtMap.EMPTY, 0);
        }
    });

    @Override
    public Block create() {
        return new Block(this);
    }

    @Override
    public Block create(int blockStateIndex) {
        Block block = new Block(this);
        block.setBlockStateIndex(blockStateIndex);
        return block;
    }

    @Override
    public BiMap<NbtMap, Integer> getBlockStates() {
        return EMPTY_BLOCK_STATES;
    }

    @Override
    public NbtMap getBlockState(int index) {
        return this.getBlockStates().inverse().getOrDefault(index, null);
    }

    @Override
    public int getBlockStateIndex(NbtMap compound) {
        return this.getBlockStates().getOrDefault(compound, -1);
    }

    @Override
    public BoundingBox getBoundingBox(int index) {
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.setHeight(1f);
        boundingBox.setWidth(1f);
        return boundingBox;
    }

    @Override
    public List<ItemStack> getDrops(int index) {
        return Collections.singletonList(ItemRegistry.getItem(this.getBlockId(), 1, index));
    }

    @Override
    public int getLightAbsorption() {
        return 0;
    }

    @Override
    public float getLightEmission() {
        return 0;
    }

    @Override
    public PushResponse getPushResponse() {
        return PushResponse.ALLOW;
    }

    @Override
    public boolean hasOxygen() {
        return true;
    }

    @Override
    public boolean isLiquid() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public float[] getOrigin() {
        return new float[]{ -8f, 0f, -8f };
    }

    @Override
    public float getHeight() {
        return 16f;
    }

    @Override
    public float getWidth() {
        return 16f;
    }

    @Override
    public float getLength() {
        return 16f;
    }

    @Override
    public float getBlastResistance() {
        return 0;
    }

    @Override
    public int getBurnOdds() {
        return 0;
    }

    @Override
    public int getFlameOdds() {
        return 0;
    }

    @Override
    public float getFriction() {
        return 0.6f;
    }

    @Override
    public String getGeometry() {
        return null;
    }

    @Override
    public String getMapColour() {
        return null;
    }

    @Override
    public float[] getRotation() {
        return new float[]{ 0, 0, 0 };
    }

    @Override
    public boolean hasGravity() {
        return false;
    }

    @Override
    public float getFallDamageReduction() {
        return 0;
    }

    @Override
    public Set<ToolType> getCorrectTools() {
        return Collections.singleton(ToolTypeRegistry.getToolType(ToolTypeID.NONE));
    }

    @Override
    public Set<ToolType> getBestTools() {
        return Collections.singleton(ToolTypeRegistry.getToolType(ToolTypeID.NONE));
    }

    @Override
    public Set<BlockLoot> getLoot(Player player) {
        return Collections.emptySet();
    }

    @Override
    public Set<BaseBlockType> getPlaceableOnlyOn() {
        return Collections.emptySet();
    }

    @Override
    public Block getResultBlock() {
        return BlockRegistry.getBlock(BlockTypeID.AIR);
    }

    @Override
    public boolean onInteract(Player player, Block block) {
        return true;
    }

    @Override
    public void onWalkedOn(Entity entity, Block block) {}

    @Override
    public void onWalkedOff(Entity entity, Block block) {}

    @Override
    public void onStandingOn(Entity entity, Block block) {}

    @Override
    public void onUpdate(Block block) {}

}