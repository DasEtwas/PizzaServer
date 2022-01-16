package io.github.pizzaserver.api.event.type.block;

import io.github.pizzaserver.api.block.Block;
import io.github.pizzaserver.api.player.Player;

/**
 * Called when a player breaks a block.
 */
public class BlockBreakEvent extends BaseBlockEvent.Cancellable {

    protected Player player;

    public BlockBreakEvent(Player player, Block block) {
        super(block);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
