package io.github.willqi.pizzaserver.api.item.types;

import io.github.willqi.pizzaserver.api.level.world.blocks.types.BaseBlockType;

public interface BlockItemType extends ItemType {

    /**
     * Get the block type this item represents
     * @return block type
     */
    BaseBlockType getBlockType();

}
