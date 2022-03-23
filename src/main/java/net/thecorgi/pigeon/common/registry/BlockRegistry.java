package net.thecorgi.pigeon.common.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.Registry;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.block.BirdHouseBlock;
import net.thecorgi.pigeon.common.block.BirdHouseBlockEntity;

import static net.thecorgi.pigeon.PigeonPost.id;

public class BlockRegistry {
    public static Block BIRD_HOUSE = new BirdHouseBlock(FabricBlockSettings.of(Material.BAMBOO).nonOpaque().breakByHand(true));
    public static BlockEntityType<BirdHouseBlockEntity> BIRD_HOUSE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(BirdHouseBlockEntity::new, BIRD_HOUSE).build(null);


    public static void init() {
        register(BIRD_HOUSE, "bird_house", true);
        register(BIRD_HOUSE_BLOCK_ENTITY,"bird_house_block_entity");
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
