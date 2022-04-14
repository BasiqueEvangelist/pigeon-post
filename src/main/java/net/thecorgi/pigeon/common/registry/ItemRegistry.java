package net.thecorgi.pigeon.common.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.registry.Registry;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.item.EnvelopeItem;

import static net.thecorgi.pigeon.PigeonPost.id;

public class ItemRegistry {
    public static Item ENVELOPE = new EnvelopeItem(new FabricItemSettings().maxCount(1));
    public static Item PIGEON_SPAWN_EGG = new SpawnEggItem(EntityRegistry.PIGEON, 0x8e8f9b, 0xb9b9b9, new FabricItemSettings().group(PigeonPost.GENERAL));

    public static void init() {
        register(ENVELOPE,"envelope");
        register(PIGEON_SPAWN_EGG,"pigeon_spawn_egg");
    }

    private static void register(Item item, String path) {
        Registry.register(Registry.ITEM, id(path), item);
    }
}
