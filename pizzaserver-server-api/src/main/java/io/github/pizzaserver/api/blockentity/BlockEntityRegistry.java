package io.github.pizzaserver.api.blockentity;

import io.github.pizzaserver.api.Server;
import io.github.pizzaserver.api.block.types.BlockType;
import io.github.pizzaserver.api.blockentity.types.BlockEntityType;

import java.util.Optional;

public interface BlockEntityRegistry {

    BlockEntityType getBlockEntityType(String blockEntityId);

    /**
     * Retrieves the block entity type equivalent of a block type if one exists.
     * @param blockType block type to look for
     * @return block entity type if any exists
     */
    Optional<BlockEntityType> getBlockEntityType(BlockType blockType);

    boolean hasBlockEntityType(String blockEntityId);

    static BlockEntityRegistry getInstance() {
        return Server.getInstance().getBlockEntityRegistry();
    }

}