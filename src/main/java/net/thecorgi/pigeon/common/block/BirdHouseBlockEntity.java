package net.thecorgi.pigeon.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.thecorgi.pigeon.common.entity.PigeonEntity;
import net.thecorgi.pigeon.common.registry.BlockRegistry;

public class BirdHouseBlockEntity extends BlockEntity {
    NbtCompound envelope;
    boolean pigeon;

    public BirdHouseBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.BIRD_HOUSE_BLOCK_ENTITY, pos, state);
        this.envelope = new NbtCompound();
        this.pigeon = false;
    }

    public NbtCompound getEnvelope() {
        return this.envelope;
    }

    public void setEnvelope(NbtCompound nbt) {
        this.envelope = nbt;
    }

    public boolean getPigeon() {
        return this.pigeon;
    }

    public void setPigeon(boolean value) {
        this.pigeon = value;
    }
}
