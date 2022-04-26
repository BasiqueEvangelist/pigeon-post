package net.thecorgi.pigeon;

import com.ibm.icu.impl.duration.impl.Utils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.thecorgi.pigeon.common.entity.PigeonEntity;
import net.thecorgi.pigeon.common.handler.EnvelopeScreenHandler;
import net.thecorgi.pigeon.common.inventory.EnvelopeInventoryInterface;
import net.thecorgi.pigeon.common.item.EnvelopeItem;
import net.thecorgi.pigeon.common.registry.BlockRegistry;
import net.thecorgi.pigeon.common.registry.EntityRegistry;
import net.thecorgi.pigeon.common.registry.ItemRegistry;
import net.thecorgi.pigeon.common.registry.SpawnRegistry;
import software.bernie.geckolib3.GeckoLib;

import static net.thecorgi.pigeon.common.registry.ItemRegistry.ENVELOPE;

public class PigeonPost implements ModInitializer {
    public static String ModID = "pigeon";

    public static Identifier id(String path) {
        return new Identifier(ModID, path);
    }

    public static final ItemGroup GENERAL = FabricItemGroupBuilder.create(
                    id("general"))
            .icon(() -> new ItemStack(ENVELOPE))
            .build();

    @Override
    public void onInitialize() {
        ItemRegistry.init();
        BlockRegistry.init();
        EntityRegistry.init();
        SpawnRegistry.init();
    }
}
