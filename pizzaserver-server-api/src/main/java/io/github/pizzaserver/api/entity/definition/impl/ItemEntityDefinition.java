package io.github.pizzaserver.api.entity.definition.impl;

import io.github.pizzaserver.api.entity.definition.BaseEntityDefinition;
import io.github.pizzaserver.api.entity.definition.components.EntityComponent;
import io.github.pizzaserver.api.entity.Entity;
import io.github.pizzaserver.api.entity.definition.components.EntityComponentGroup;
import io.github.pizzaserver.api.entity.definition.components.impl.EntityCollisionBoxComponent;
import io.github.pizzaserver.api.entity.definition.components.impl.EntityPhysicsComponent;

public class ItemEntityDefinition extends BaseEntityDefinition {

    public static final String ID = "minecraft:item";


    public ItemEntityDefinition() {
        this.registerComponentGroup(new EntityComponentGroup("minecraft:default", new EntityComponent[]{
                new EntityCollisionBoxComponent(0.25f, 0.25f),
                new EntityPhysicsComponent(new EntityPhysicsComponent.Properties()
                        .setPushable(false)
                        .setPistonPushable(true)
                        .setGravity(true)
                        .setCollision(false)
                        .setGravityForce(0.04f)
                        .setDragForce(0.02f)
                        .setApplyDragBeforeGravity(true)) }));
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Item";
    }

    @Override
    public boolean isSummonable() {
        return false;
    }

    @Override
    public boolean hasSpawnEgg() {
        return false;
    }

    @Override
    public void onCreation(Entity entity) {
        entity.addComponentGroup("minecraft:default");
        entity.setVulnerable(false);
    }

}