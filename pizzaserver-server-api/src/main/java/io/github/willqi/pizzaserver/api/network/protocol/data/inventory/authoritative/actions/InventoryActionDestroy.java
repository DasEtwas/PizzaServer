package io.github.willqi.pizzaserver.api.network.protocol.data.inventory.authoritative.actions;

import io.github.willqi.pizzaserver.api.network.protocol.data.inventory.authoritative.AuthoritativeInventorySlot;

/**
 * Used for server authoritative inventories.
 * Created when a player tries to destroy an item by dragging it into the creative inventory
 */
public class InventoryActionDestroy implements InventoryAction {

    private final AuthoritativeInventorySlot source;
    private final int amount;


    public InventoryActionDestroy(AuthoritativeInventorySlot source, int amount) {
        this.source = source;
        this.amount = amount;
    }

    @Override
    public InventoryActionType getType() {
        return InventoryActionType.DESTROY;
    }

    public AuthoritativeInventorySlot getSlot() {
        return this.source;
    }

    public int getAmount() {
        return this.amount;
    }

}