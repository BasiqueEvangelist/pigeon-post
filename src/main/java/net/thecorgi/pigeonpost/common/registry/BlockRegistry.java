package net.thecorgi.pigeonpost.common.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.Registry;
import net.thecorgi.pigeonpost.PigeonPost;
import net.thecorgi.pigeonpost.common.block.BirdhouseBlock;
import net.thecorgi.pigeonpost.common.block.BirdhouseBlockEntity;


import static net.thecorgi.pigeonpost.PigeonPost.id;

public class BlockRegistry {
    public static Block BIRDHOUSE = new BirdhouseBlock(FabricBlockSettings.of(Material.BAMBOO).nonOpaque().strength(1.5F));
    public static BlockEntityType<BirdhouseBlockEntity> BIRDHOUSE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(BirdhouseBlockEntity::new, BIRDHOUSE).build(null);


    public static void init() {
        register(BIRDHOUSE, "birdhouse", true);
        register(BIRDHOUSE_BLOCK_ENTITY,"birdhouse_block_entity");
    }

    private static void register(Block block, String path, boolean item) {
        Registry.register(Registry.BLOCK, id(path), block);
        if (item) {
            Registry.register(Registry.ITEM, id(path), new BlockItem(block, new FabricItemSettings().group(PigeonPost.GENERAL)));
        }
    }

    private static void register(BlockEntityType type, String path) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id(path), type);
    }
}
