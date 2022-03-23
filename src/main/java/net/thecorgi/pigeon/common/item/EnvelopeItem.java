package net.thecorgi.pigeon.common.item;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecorgi.pigeon.common.inventory.EnvelopeInventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.thecorgi.pigeon.PigeonPost.id;

public class EnvelopeItem extends Item {
    public EnvelopeItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);

        if(!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(id("envelope"), user, buf -> {
                ItemStack stack = user.getStackInHand(hand);
                buf.writeItemStack(stack);
                buf.writeInt(hand == Hand.MAIN_HAND ? 0 : 1);
                buf.writeString(stack.getName().asString());
            });
        }

        return super.use(world, user, hand);
    }

    public static EnvelopeInventory getInventory(ItemStack stack, Hand hand, PlayerEntity player) {
        if(!stack.hasNbt()) {
            stack.setNbt(new NbtCompound());
        }

        if(!stack.getNbt().contains("envelope")) {
            stack.getNbt().put("envelope", new NbtCompound());
        }

        return new EnvelopeInventory(stack.getNbt().getCompound("envelope"), hand, player);
    }




    private boolean containsAddress(ItemStack stack) {
        if (stack.getNbt() == null) {
            return false;
        }
        return !stack.getNbt().isEmpty() && stack.getNbt().contains("Address");
    }

    public boolean hasGlint(ItemStack stack) {
        return containsAddress(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<net.minecraft.text.Text> tooltip, TooltipContext context) {
        if (containsAddress(stack)) {
            long pos = stack.getOrCreateNbt().getLong("Address");
            int x = BlockPos.unpackLongX(pos);
            int y = BlockPos.unpackLongY(pos);
            int z = BlockPos.unpackLongZ(pos);
            tooltip.add(new TranslatableText("item.pigeon.envelope.contains_address", Integer.toString(x), Integer.toString(y), Integer.toString(z)).formatted(Formatting.GRAY));
        } else {
            tooltip.add(new TranslatableText("item.pigeon.envelope.empty").formatted(Formatting.GRAY));
        }
    }
}
