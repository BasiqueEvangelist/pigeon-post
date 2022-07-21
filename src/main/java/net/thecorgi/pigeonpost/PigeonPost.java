package net.thecorgi.pigeonpost;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.thecorgi.pigeonpost.common.envelope.EnvelopeGuiDescription;
import net.thecorgi.pigeonpost.common.envelope.EnvelopeItem;
import net.thecorgi.pigeonpost.common.registry.BlockRegistry;
import net.thecorgi.pigeonpost.common.registry.EntityRegistry;
import net.thecorgi.pigeonpost.common.registry.ItemRegistry;

import static net.thecorgi.pigeonpost.common.registry.ItemRegistry.ENVELOPE;

public class PigeonPost implements ModInitializer {
    public static String ModID = "pigeonpost";
    public static Identifier ADDRESS_PACKET_ID = id("address_packet");

    public static Identifier id(String path) {
        return new Identifier(ModID, path);
    }

    public static final ItemGroup GENERAL = FabricItemGroupBuilder.create(
                    id("general"))
            .icon(() -> new ItemStack(ENVELOPE))
            .build();

    public static ScreenHandlerType<EnvelopeGuiDescription> SCREEN_HANDLER_TYPE;

    @Override
    public void onInitialize() {
        ItemRegistry.init();
        BlockRegistry.init();
        EntityRegistry.init();

        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.PLAINS, Biome.Category.BEACH, Biome.Category.FOREST, Biome.Category.MOUNTAIN), SpawnGroup.CREATURE,
                EntityRegistry.PIGEON, 1, 2, 7);

        SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(EnvelopeItem.ID, (syncId, inventory) -> new EnvelopeGuiDescription(syncId, inventory, ENVELOPE.getDefaultStack()));

        ServerPlayNetworking.registerGlobalReceiver(ADDRESS_PACKET_ID, new ServerPlayNetworking.PlayChannelHandler() {
            @Override
            public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                                PacketByteBuf buf, PacketSender responseSender) {

                if (player.getWorld().isClient()) return;

                long pos = buf.readLong();
                ItemStack stack = player.getStackInHand(player.getActiveHand());

                if (stack.isOf(ENVELOPE)) {
                    NbtCompound nbtCompound = stack.getOrCreateNbt();
                    nbtCompound.putLong(EnvelopeItem.ADDRESS_KEY, pos);
                    stack.setNbt(nbtCompound);
                }
            }
        });

    }
}
