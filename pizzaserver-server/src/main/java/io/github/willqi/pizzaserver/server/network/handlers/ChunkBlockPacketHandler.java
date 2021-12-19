package io.github.willqi.pizzaserver.server.network.handlers;

import io.github.willqi.pizzaserver.api.item.ItemStack;
import io.github.willqi.pizzaserver.api.network.protocol.data.inventory.transactions.InventoryTransactionType;
import io.github.willqi.pizzaserver.api.network.protocol.data.inventory.transactions.data.InventoryTransactionUseItemData;
import io.github.willqi.pizzaserver.api.player.data.Gamemode;
import io.github.willqi.pizzaserver.commons.utils.Vector3;
import io.github.willqi.pizzaserver.server.network.BaseBedrockPacketHandler;
import io.github.willqi.pizzaserver.api.network.protocol.packets.*;
import io.github.willqi.pizzaserver.server.player.ImplPlayer;
import io.github.willqi.pizzaserver.api.event.type.block.BlockBreakEvent;
import io.github.willqi.pizzaserver.api.event.type.block.BlockStartBreakEvent;
import io.github.willqi.pizzaserver.api.event.type.block.BlockStopBreakEvent;
import io.github.willqi.pizzaserver.api.level.world.blocks.Block;
import io.github.willqi.pizzaserver.api.utils.BlockLocation;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles any packets regarding chunk/block interactions.
 */
public class ChunkBlockPacketHandler extends BaseBedrockPacketHandler {

    private final ImplPlayer player;


    public ChunkBlockPacketHandler(ImplPlayer player) {
        this.player = player;
    }

    @Override
    public void onPacket(RequestChunkRadiusPacket packet) {
        int newChunkRadius = Math.min(packet.getChunkRadiusRequested(), this.player.getServer().getConfig().getChunkRadius());
        this.player.setChunkRadius(newChunkRadius);

        ChunkRadiusUpdatedPacket chunkRadiusUpdatedPacket = new ChunkRadiusUpdatedPacket();
        chunkRadiusUpdatedPacket.setRadius(this.player.getChunkRadius());
        this.player.sendPacket(chunkRadiusUpdatedPacket);
    }

    @Override
    public void onPacket(PlayerActionPacket packet) {
        if (!this.player.isAlive()) {
            return;
        }

        if (this.player.canReach(packet.getVector3(), 12)) {
            Block block = this.player.getWorld().getBlock(packet.getVector3());
            switch (packet.getActionType()) {
                case START_BREAK:
                    BlockLocation location = new BlockLocation(this.player.getWorld(), packet.getVector3());
                    if (location.getBlock().isSolid() && this.player.getAdventureSettings().canMine()) {
                        BlockStartBreakEvent blockStartBreakEvent = new BlockStartBreakEvent(this.player, block);
                        this.player.getServer().getEventManager().call(blockStartBreakEvent);
                        if (!blockStartBreakEvent.isCancelled()) {
                            this.player.getBlockBreakData().startBreaking(location);
                        }
                    } else {
                        // Player was not able to break that block
                        this.player.getWorld().sendBlock(this.player, packet.getVector3());
                    }
                    break;
                case CONTINUE_BREAK:
                    if (this.player.getBlockBreakData().isBreakingBlock()) {
                        this.player.getBlockBreakData().sendUpdatedBreakProgress();
                    }
                    break;
                case ABORT_BREAK:
                    if (this.player.getBlockBreakData().isBreakingBlock()) {
                        BlockStopBreakEvent blockStopBreakEvent = new BlockStopBreakEvent(this.player, block);
                        this.player.getServer().getEventManager().call(blockStopBreakEvent);

                        this.player.getBlockBreakData().stopBreaking();
                    }
                    break;
            }
        }
    }

    // Handles breaking the block
    @Override
    public void onPacket(InventoryTransactionPacket packet) {
        if (!this.player.isAlive()) {
            return;
        }

        if (packet.getType() == InventoryTransactionType.ITEM_USE) {
            InventoryTransactionUseItemData useItemData = (InventoryTransactionUseItemData) packet.getData();

            boolean isValidBreakBlockRequest = this.player.canReach(useItemData.getBlockCoordinates().toVector3(), this.player.getGamemode().equals(Gamemode.CREATIVE) ? 13 : 7)
                    && useItemData.getAction() == InventoryTransactionUseItemData.Action.BREAK_BLOCK;

            if (isValidBreakBlockRequest) {
                Optional<Block> blockMining = this.player.getBlockBreakData().getBlock();
                boolean isCorrectBlockCoordinates = blockMining.isPresent() && useItemData.getBlockCoordinates().equals(blockMining.get().getLocation());
                boolean canBreakBlock = isCorrectBlockCoordinates && this.player.getBlockBreakData().canBreakBlock();

                if (!isCorrectBlockCoordinates) {
                    this.player.getServer().getLogger().debug(String.format("%s tried to destroy a block while not breaking the block.", this.player.getUsername()));
                }

                if (canBreakBlock) {
                    BlockBreakEvent blockBreakEvent = new BlockBreakEvent(this.player, this.player.getWorld().getBlock(useItemData.getBlockCoordinates()));
                    this.player.getServer().getEventManager().call(blockBreakEvent);

                    if (!blockBreakEvent.isCancelled()) {
                        if (blockBreakEvent.isBlockDropsEnabled()) {
                            for (ItemStack itemStack : blockMining.get().getDrops()) {
                                Vector3 velocity = new Vector3(0.1f * (ThreadLocalRandom.current().nextFloat() * 2 - 1), 0.3f, 0.1f * (ThreadLocalRandom.current().nextFloat() * 2 - 1));
                                Vector3 position = blockMining.get().getLocation().toVector3().add(0.5f, 0.5f, 0.5f);
                                this.player.getWorld().addItemEntity(itemStack, position, velocity);
                            }
                        }
                        this.player.getBlockBreakData().breakBlock();
                        return;
                    }
                } else {
                    this.player.getServer().getLogger().debug(String.format("%s tried to destroy a block but was not allowed.", this.player.getUsername()));
                }

                this.player.getWorld().sendBlock(this.player, useItemData.getBlockCoordinates());
            }
        }
    }

}