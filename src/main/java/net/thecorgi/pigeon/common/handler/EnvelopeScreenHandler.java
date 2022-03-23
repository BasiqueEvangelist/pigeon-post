package net.thecorgi.pigeon.common.handler;

import com.google.common.collect.Sets;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.item.EnvelopeItem;

import java.util.Set;

public class EnvelopeScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PlayerInventory playerInventory;
    public final int inventoryWidth;
    public final int inventoryHeight;
    public final String customTitle;
    public static final Set<Item> SHULKER_BOXES;

    static {
        SHULKER_BOXES = Sets.newHashSet(Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX, Items.BLUE_SHULKER_BOX,
                Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
                Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.LIME_SHULKER_BOX,
                Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.RED_SHULKER_BOX,
                Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.PURPLE_SHULKER_BOX);
    }

    public EnvelopeScreenHandler(final int syncId, final PlayerInventory playerInventory, final Inventory inventory, final int inventoryWidth, final int inventoryHeight, final Hand hand, String customTitle) {
        super(null, syncId);
        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.inventoryWidth = inventoryWidth;
        this.inventoryHeight = inventoryHeight;
        this.customTitle = customTitle;

        checkSize(inventory, inventoryWidth * inventoryHeight);
        inventory.onOpen(playerInventory.player);
        setupSlots(false);
    }

    public class EnvelopeSlot extends Slot {
        public EnvelopeSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return stackMovementIsAllowed(getStack());
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stackMovementIsAllowed(stack);
        }

        public boolean stackMovementIsAllowed(ItemStack stack) {
            Item item = stack.getItem();
            if(stack.getItem() instanceof EnvelopeItem ||
                    SHULKER_BOXES.contains(item)){
                return false;
            }

            return true;
        }
    }

    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        inventory.onClose(player);
    }

    public void setupSlots(final boolean includeChestInventory) {
        int i = (this.inventoryHeight - 4) * 18;

        int n;
        int m;
        for(n = 0; n < 5; ++n) {
            if (n != 2) {
                this.addSlot(new Slot(inventory, n, 44 + n * 18, 20));
            }
        }

        for(n = 0; n < 3; ++n) {
            for(m = 0; m < 9; ++m) {
                this.addSlot(new EnvelopeSlot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i));
            }

        }

        for(n = 0; n < 9; ++n) {
            this.addSlot(new EnvelopeSlot(playerInventory, n, 8 + n * 18, 161 + i));
        }
    }

    @Override
    public boolean canUse(final PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        ItemStack originalStack = slot.getStack();
        Item item = originalStack.getItem();

        if(item instanceof EnvelopeItem ||
                SHULKER_BOXES.contains(item)) {
            return ItemStack.EMPTY;
        }

        if (slot.hasStack()) {
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            else {
                slot.markDirty();
            }
        }

        return newStack;
    }
}

//    public static final int SLOT_COUNT = 5;
//    private final Inventory inventory;
//    private final PlayerInventory playerInventory;
//    public final int inventoryWidth;
//    public final int inventoryHeight;
//    public final String customTitle;
//    public final String customTitle;

    //    public EnvelopeScreenHandler(int syncId, PlayerInventory playerInventory) {
    //        this(syncId, playerInventory, new SimpleInventory(5));
    //    }

//    public EnvelopeScreenHandler(final int syncId, final PlayerInventory playerInventory, final Inventory inventory, final int inventoryWidth, final int inventoryHeight, final Hand hand, String customTitle) {
//        super(null, syncId);
//        this.inventory = inventory;
//        this.playerInventory = playerInventory;
//        this.inventoryWidth = inventoryWidth;
//        this.inventoryHeight = inventoryHeight;
//        this.customTitle = customTitle;
//
//        checkSize(inventory, inventoryWidth * inventoryHeight);
//        inventory.onOpen(playerInventory.player);
//        setupSlots(false);
//    }
//    public EnvelopeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
//        super(null, syncId);
//        this.inventory = inventory;
//        this.playerInventory = playerInventory;
//        this.inventoryWidth = inventoryWidth;
//        this.inventoryHeight = inventoryHeight;
//        this.customTitle = customTitle;
//
//        checkSize(inventory, inventoryWidth * inventoryHeight);
//        inventory.onOpen(playerInventory.player);
//        setupSlots(false);
////        this.customTitle = customTitle;
//        checkSize(inventory, 5);
//    }

//    public void setupSlots(final boolean includeChestInventory) {
//        inventory.onOpen(playerInventory.player);
//
//        int j;
//        for(j = 0; j < SLOT_COUNT; ++j) {
//            if (j != 2) {
//                this.addSlot(new Slot(inventory, j, 44 + j * 18, 20));
//            }
//        }
//
//        for(j = 0; j < 3; ++j) {
//            for(int k = 0; k < 9; ++k) {
//                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, j * 18 + 51));
//            }
//        }
//
//        for(j = 0; j < 9; ++j) {
//            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 109));
//        }
//    }
//
//    public boolean canUse(PlayerEntity player) {
//        return this.inventory.canPlayerUse(player);
//    }
//
//    public ItemStack transferSlot(PlayerEntity player, int index) {
//        ItemStack itemStack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(index);
//        if (slot != null && slot.hasStack()) {
//            ItemStack itemStack2 = slot.getStack();
//            itemStack = itemStack2.copy();
//            if (index < this.inventory.size()) {
//                if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)) {
//                    return ItemStack.EMPTY;
//                }
//            } else if (!this.insertItem(itemStack2, 0, this.inventory.size(), false)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (itemStack2.isEmpty()) {
//                slot.setStack(ItemStack.EMPTY);
//            } else {
//                slot.markDirty();
//            }
//        }
//
//        return itemStack;
//    }
//
////    public void close(PlayerEntity player) {
////        super.close(player);
////        this.inventory.onClose(player);
////    }
//}