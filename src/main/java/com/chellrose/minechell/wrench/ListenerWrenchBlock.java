package com.chellrose.minechell.wrench;

import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Wall;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ListenerWrenchBlock implements Listener {
    private static final Map<String, WrenchBlockClipboard> CLIPBOARDS = new HashMap<>();
    
    static {
        // Stairs
        CLIPBOARDS.put(Stairs.class.getName(), new WrenchBlockClipboard() {
            @Override
            public void paste(BlockData template, BlockData data) {
                Stairs stairs = (Stairs)template;
                Stairs target = (Stairs)data;
                target.setHalf(stairs.getHalf());
                target.setFacing(stairs.getFacing());
                target.setShape(stairs.getShape());
            }
        });

        // Slabs
        CLIPBOARDS.put(Slab.class.getName(), new WrenchBlockClipboard() {
            @Override
            public void paste(BlockData template, BlockData data) {
                Slab slab = (Slab)template;
                Slab target = (Slab)data;
                target.setType(slab.getType());
            }
        });

        // Fences
        CLIPBOARDS.put(Fence.class.getName(), new WrenchBlockClipboard() {
            @Override
            public void paste(BlockData template, BlockData data) {
                Fence fence = (Fence)template;
                Fence target = (Fence)data;
                for (BlockFace face : target.getAllowedFaces()) {
                    target.setFace(face, fence.hasFace(face));
                }
            }
        });

        // Walls
        CLIPBOARDS.put(Wall.class.getName(), new WrenchBlockClipboard() {
            @Override
            public void paste(BlockData template, BlockData data) {
                Wall wall = (Wall)template;
                Wall target = (Wall)data;
                for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP}) {
                    target.setHeight(face, wall.getHeight(face));
                }
            }
        });

        // Glass panes
        CLIPBOARDS.put(GlassPane.class.getName(), new WrenchBlockClipboard() {
            @Override
            public void paste(BlockData template, BlockData data) {
                GlassPane pane = (GlassPane)template;
                GlassPane target = (GlassPane)data;
                for (BlockFace face : target.getAllowedFaces()) {
                    target.setFace(face, pane.hasFace(face));
                }
            }
        });

        // Furnaces
        CLIPBOARDS.put(Furnace.class.getName(), new WrenchBlockClipboard() {
            @Override
            public void paste(BlockData template, BlockData data) {
                Furnace furnace = (Furnace)template;
                Furnace target = (Furnace)data;
                target.setFacing(furnace.getFacing());
                target.setLit(furnace.isLit());
            }
        });
    }

    private static interface WrenchBlockClipboard {
        /**
         * Pastes the given block data onto the specified block data.
         * Only allowed fields (ie, not waterlogged) will be pasted.
         *
         * @param template the block data to be pasted
         * @param data     the block data which should be pasted onto
         */
        public void paste(BlockData template, BlockData data);
    }

    private ItemWrench itemWrench;

    public ListenerWrenchBlock(ItemWrench itemWrench) {
        this.itemWrench = itemWrench;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (this.itemWrench.isWrench(item)) {
                ItemStack wrench = item;
                Block block = event.getClickedBlock();
                BlockData data = block.getBlockData();
                if (data != null) {
                    Class<?> iface = null;
                    WrenchBlockClipboard factory = null;
                    for (Class<?> clazzIface : data.getClass().getInterfaces()) {
                        WrenchBlockClipboard clazzFactory = CLIPBOARDS.get(clazzIface.getName());
                        if (clazzFactory != null) {
                            iface = clazzIface;
                            factory = clazzFactory;
                        }
                    }
                    if (iface != null && factory != null) {
                        event.setCancelled(true);
                        if (player.isSneaking()) {
                            // Copy data
                            this.itemWrench.setBlockData(wrench, iface, data);
                            BaseComponent msg = new TextComponent("Copied " + iface.getSimpleName().toLowerCase());
                            msg.setColor(ChatColor.LIGHT_PURPLE);
                            msg.setItalic(true);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, msg);
                            player.playSound(block.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
                        } else {
                            // Paste data
                            BlockData template = (BlockData)this.itemWrench.getBlockData(wrench, iface);
                            if (template == null) {
                                BaseComponent msg = new TextComponent("No block data to paste. Sneak-right-click an equivalent block type to copy");
                                msg.setColor(ChatColor.LIGHT_PURPLE);
                                msg.setItalic(true);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, msg);
                                player.playSound(block.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                            } else {
                                factory.paste(template, data);
                                block.setBlockData(data, false);
                                BaseComponent msg = new TextComponent("Pasted " + iface.getSimpleName().toLowerCase());
                                msg.setColor(ChatColor.LIGHT_PURPLE);
                                msg.setItalic(true);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, msg);
                                player.playSound(block.getLocation(), block.getBlockData().getSoundGroup().getPlaceSound(), 1.0f, 1.0f);
                                block.getWorld().spawnParticle(
                                    Particle.BLOCK,
                                    block.getLocation().add(0.5, 0.5, 0.5),
                                    10,
                                    0.25, 0.25, 0.25,
                                    block.getBlockData());
                            }
                        }
                        player.getInventory().setItemInMainHand(wrench); // Writeback nbt changes
                    }
                }
            }
        }
    }
}
