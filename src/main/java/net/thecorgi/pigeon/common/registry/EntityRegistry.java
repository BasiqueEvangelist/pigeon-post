package net.thecorgi.pigeon.common.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.registry.Registry;
import net.thecorgi.pigeon.common.entity.NestEntity;
import net.thecorgi.pigeon.common.entity.PigeonEntity;

import static net.thecorgi.pigeon.PigeonPost.id;

public class EntityRegistry {
    public static final EntityType<PigeonEntity> PIGEON = Registry.register(
            Registry.ENTITY_TYPE,
            id("pigeon"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PigeonEntity::new).trackRangeBlocks(10).dimensions(EntityDimensions.fixed(0.65f, 0.65f)).build()
    );

    public static final EntityType<NestEntity> NEST = Registry.register(
            Registry.ENTITY_TYPE,
            id("nest"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, NestEntity::new).dimensions(EntityDimensions.fixed(0.35f, 0.78f)).build()
    );


    public static void init() {
        register(PIGEON, PigeonEntity.createPigeonAttributes());
        register(NEST, NestEntity.createLivingAttributes());
    }

    private static void register(EntityType entityType, DefaultAttributeContainer.Builder builder) {
        FabricDefaultAttributeRegistry.register(entityType, builder);
    }
}
