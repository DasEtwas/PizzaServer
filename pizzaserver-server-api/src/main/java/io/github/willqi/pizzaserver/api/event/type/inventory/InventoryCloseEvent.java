package io.github.willqi.pizzaserver.api.event.type.inventory;

import io.github.willqi.pizzaserver.api.entity.inventory.Inventory;
import io.github.willqi.pizzaserver.api.player.Player;

/**
 * Called after the player closes their inventory.
 */
public class InventoryCloseEvent extends BaseInventoryEvent {

    protected Player player;


    public InventoryCloseEvent(Player player, Inventory inventory) {
        super(inventory);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

}