package io.github.pizzaserver.api.item.impl;

import io.github.pizzaserver.api.item.data.ArmorSlot;
import io.github.pizzaserver.api.item.data.ItemID;

public class ItemLeatherBoots extends ItemArmor {

    public ItemLeatherBoots() {
        this(1);
    }

    public ItemLeatherBoots(int count) {
        this(count, 0);
    }

    public ItemLeatherBoots(int count, int meta) {
        super(ItemID.LEATHER_BOOTS, count, meta);
    }

    @Override
    public String getName() {
        return "Leather Boots";
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public ArmorSlot getArmorSlot() {
        return ArmorSlot.BOOTS;
    }

    @Override
    public int getProtection() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 65;
    }
}
