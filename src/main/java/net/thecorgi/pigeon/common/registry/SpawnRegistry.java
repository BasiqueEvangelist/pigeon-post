package net.thecorgi.pigeon.common.registry;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;

public class SpawnRegistry {
    public static void init() {
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.PLAINS, Biome.Category.BEACH, Biome.Category.FOREST, Biome.Category.MOUNTAIN), SpawnGroup.CREATURE,
                EntityRegistry.PIGEON, 1, 2, 7);
    }
}
