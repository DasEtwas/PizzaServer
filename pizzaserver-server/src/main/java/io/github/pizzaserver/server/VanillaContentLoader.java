package io.github.pizzaserver.server;

import io.github.pizzaserver.api.entity.EntityRegistry;
import io.github.pizzaserver.api.entity.definition.components.handlers.*;
import io.github.pizzaserver.api.entity.definition.components.impl.*;
import io.github.pizzaserver.api.level.world.blocks.types.impl.*;
import io.github.pizzaserver.api.entity.definition.impl.CowEntityDefinition;
import io.github.pizzaserver.api.entity.definition.impl.HumanEntityDefinition;
import io.github.pizzaserver.api.entity.definition.impl.ItemEntityDefinition;
import io.github.pizzaserver.api.level.world.blocks.BlockRegistry;

import java.util.ArrayList;

public class VanillaContentLoader {

    public static void load() {
        loadItems();
        loadBlocks();
        loadEntityComponents();
        loadEntities();
    }

    private static void loadItems() {

    }

    private static void loadBlocks() {
        BlockRegistry.register(new BlockTypeAir());
        BlockRegistry.register(new BlockTypeDirt());
        BlockRegistry.register(new BlockTypeGrass());
        BlockRegistry.register(new BlockTypeStone());
        BlockRegistry.register(new BlockTypeWater());
        BlockRegistry.register(new BlockTypeFlowingWater());
    }

    private static void loadEntityComponents() {
        EntityRegistry.registerComponent(EntityScaleComponent.class, new EntityScaleComponent(1), new EntityScaleComponentHandler());
        EntityRegistry.registerComponent(EntityCollisionBoxComponent.class, new EntityCollisionBoxComponent(0f, 0f), new EntityCollisionBoxComponentHandler());
        EntityRegistry.registerComponent(EntityHealthComponent.class, new EntityHealthComponent(0, 0, 0), new EntityHealthComponentHandler());
        EntityRegistry.registerComponent(EntityDamageSensorComponent.class, new EntityDamageSensorComponent(new EntityDamageSensorComponent.Sensor[0]), new EntityEmptyComponentHandler<>());
        EntityRegistry.registerComponent(EntityLootComponent.class, new EntityLootComponent(new ArrayList<>()), new EntityLootComponentHandler());
        EntityRegistry.registerComponent(EntityDeathMessageComponent.class, new EntityDeathMessageComponent(false), new EntityDeathMessageComponentHandler());
        EntityRegistry.registerComponent(EntityPhysicsComponent.class, new EntityPhysicsComponent(new EntityPhysicsComponent.Properties()
                .setCollision(true)
                .setGravity(true)
                .setPushable(true)
                .setPistonPushable(true)
                .setGravityForce(0.08f)
                .setDragForce(0.02f)
                .setApplyDragBeforeGravity(false)), new EntityPhysicsComponentHandler());
        EntityRegistry.registerComponent(EntityBossComponent.class, new EntityBossComponent(null, -1, false), new EntityBossComponentHandler());
        EntityRegistry.registerComponent(EntityBurnsInDaylightComponent.class, new EntityBurnsInDaylightComponent(), new EntityEmptyComponentHandler<>());
        EntityRegistry.registerComponent(EntityBreathableComponent.class, new EntityBreathableComponent(new EntityBreathableComponent.Properties()
                .setGenerateBubblesInWater(true)
                .setSuffocationInterval(10)
                .setInhaleTime(5)
                .setTotalSupplyTime(15)
        ), new EntityBreathableComponentHandler());
    }

    private static void loadEntities() {
        EntityRegistry.registerDefinition(new HumanEntityDefinition());
        EntityRegistry.registerDefinition(new ItemEntityDefinition());
        EntityRegistry.registerDefinition(new CowEntityDefinition());
    }

}