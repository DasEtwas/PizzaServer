package io.github.willqi.pizzaserver.api.item.types;

import io.github.willqi.pizzaserver.api.entity.Entity;
import io.github.willqi.pizzaserver.api.item.ItemStack;
import io.github.willqi.pizzaserver.api.level.world.blocks.Block;
import io.github.willqi.pizzaserver.api.level.world.blocks.BlockFace;
import io.github.willqi.pizzaserver.api.level.world.blocks.types.BaseBlockType;
import io.github.willqi.pizzaserver.api.player.Player;

import java.util.Collections;
import java.util.Set;

public abstract class BaseItemType implements ItemType {

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean isAllowedInOffHand() {
        return false;
    }

    @Override
    public boolean isHandEquipped() {
        return false;    // TODO: Should this be false by default?
    }

    @Override
    public boolean canClickOnLiquids() {
        return false;
    }

    @Override
    public boolean hasFoil() {
        return false;
    }

    @Override
    public boolean isMirroredArt() {
        return false;
    }

    @Override
    public ItemType.UseAnimationType getUseAnimationType() {
        return ItemType.UseAnimationType.NONE;
    }

    @Override
    public int getUseDuration() {
        return 32;
    }

    @Override
    public boolean isStackedByDamage() {
        return false;
    }

    @Override
    public int getDamage() {
        return 1;
    }

    @Override
    public int getMiningSpeed() {
        return 1;
    }

    @Override
    public Set<BaseBlockType> getOnlyBlocksCanBreak() {
        return Collections.emptySet();
    }

    @Override
    public boolean onInteract(Player player, ItemStack itemStack, Block block, BlockFace blockFace) {
        return true;
    }

    @Override
    public void onInteract(Player player, ItemStack itemStack, Entity entity) {}

    @Override
    public ItemStack create() {
        return this.create(1);
    }

    @Override
    public ItemStack create(int amount) {
        return this.create(amount, 0);
    }

    @Override
    public ItemStack create(int amount, int damage) {
        return new ItemStack(this, amount, damage);
    }

}