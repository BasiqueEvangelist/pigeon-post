package net.thecorgi.pigeonpost.common.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.registry.Registry;
import net.thecorgi.pigeonpost.common.entity.PigeonEntity;

import static net.thecorgi.pigeonpost.PigeonPost.id;

public class EntityRegistry {
    public static final EntityType<PigeonEntity> PIGEON = Registry.register(
            Registry.ENTITY_TYPE,
            id("pigeon"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PigeonEntity::new).trackRangeBlocks(10).dimensions(EntityDimensions.fixed(0.65f, 0.65f)).build()
    );

    public static void init() {
        register(PIGEON, PigeonEntity.createPigeonAttributes());
    }

    private static void register(EntityType entityType, DefaultAttributeContainer.Builder builder) {
        FabricDefaultAttributeRegistry.register(entityType, builder);
    }
}
