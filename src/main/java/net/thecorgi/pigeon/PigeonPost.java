package net.thecorgi.pigeon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.thecorgi.pigeon.block.BirdStandBlock;
import net.thecorgi.pigeon.block.BirdStandBlockEntity;
import net.thecorgi.pigeon.entity.PigeonEntity;

public class PigeonPost implements ModInitializer {
    public static String ModID = "pigeon";

    public static Identifier id(String path) {
        return new Identifier(ModID, path);
    }

    public static final BirdStandBlock BIRD_STAND = new BirdStandBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque());
    public static BlockEntityType<BirdStandBlockEntity> BIRD_STAND_BLOCK_ENTITY;

    public static final EntityType<PigeonEntity> PIGEON = Registry.register(
            Registry.ENTITY_TYPE,
            id("pigeon"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PigeonEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, id("bird_stand"), BIRD_STAND);
        Registry.register(Registry.ITEM, id("bird_stand"), new BlockItem(BIRD_STAND, new FabricItemSettings().group(ItemGroup.MISC)));

        BIRD_STAND_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "pigeon:bird_stand_block_entity", FabricBlockEntityTypeBuilder.create(BirdStandBlockEntity::new, BIRD_STAND).build(null));

        FabricDefaultAttributeRegistry.register(PIGEON, PigeonEntity.createPigeonAttributes());
    }
}
