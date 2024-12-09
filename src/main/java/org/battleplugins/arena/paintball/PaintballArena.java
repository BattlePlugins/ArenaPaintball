package org.battleplugins.arena.paintball;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.util.CustomEffect;
import org.battleplugins.arena.util.ItemColor;
import org.battleplugins.arena.util.Util;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PaintballArena extends Arena {
    private static final String PAINTBALL_META_KEY = "paintball";

    public PaintballArena() {
        super();

        this.getEventManager().registerArenaResolver(ProjectileHitEvent.class, event -> {
            if (!event.getEntity().hasMetadata(PAINTBALL_META_KEY)) {
                return null;
            }

            if (event.getHitBlock() == null) {
                return null;
            }

            ProjectileSource shooter = event.getEntity().getShooter();
            if (!(shooter instanceof Player player)) {
                return null;
            }

            ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
            if (arenaPlayer == null || !(arenaPlayer.getArena() instanceof PaintballArena)) {
                return null;
            }

            return arenaPlayer.getCompetition();
        });
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        // Disable any pvp combat
        if (ArenaPaintball.getInstance().getMainConfig().isPvpDisabled() && event.getDamager() instanceof Player) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getDamager() instanceof Projectile projectile) || !projectile.hasMetadata(PAINTBALL_META_KEY)) {
            return;
        }

        Paintball paintball = (Paintball) projectile.getMetadata(PAINTBALL_META_KEY).get(0).value();
        if (paintball == null) {
            return;
        }

        if (projectile.getShooter() instanceof Player damager) {
            for (CustomEffect<?> effect : paintball.getHitEffects()) {
                effect.play(event.getEntity());
            }

            event.setDamage(paintball.getPaintballDamage());
            if (paintball.getHitSound() != null) {
                damager.playSound(damager.getLocation(), paintball.getHitSound(), 1, 1);
            }
        }
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event, ArenaPlayer player) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        NamespacedKey itemKey = ArenaPaintball.getInstance().getPaintballItemKey();
        if (!meta.getPersistentDataContainer().has(itemKey)) {
            return;
        }

        Paintball paintball = ArenaPaintball.getInstance().getMainConfig().getPaintball(meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING));
        if (paintball == null) {
            return;
        }

        event.setCancelled(true);

        PaintballCooldowns cooldowns = PaintballCooldowns.getOrCreate(player);
        if (!cooldowns.canUse(paintball)) {
            if (ArenaPaintball.getInstance().getMainConfig().shouldSendCooldownMessage()) {
                ArenaPaintball.COOLDOWN_MESSAGE.send(player.getPlayer(), Util.toTimeString(cooldowns.getRemainingTime(paintball)));
            }

            return;
        }

        cooldowns.use(paintball);
        if (paintball.shouldUseItem()) {
            event.getItem().subtract();
        }

        if (paintball.getLaunchSound() != null) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), paintball.getLaunchSound(), 1, 1);
        } else {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1, 0);
        }

        int projectileCount = paintball.getProjectileCount();
        List<Projectile> projectiles = new ArrayList<>();
        for (int i = 0; i < projectileCount; i++) {
            projectiles.add(this.launchPaintball(paintball, player));
        }

        if (paintball.getParticle() != null) {
            Particle particle = Registry.PARTICLE_TYPE.get(NamespacedKey.fromString(paintball.getParticle()));
            if (particle == null) {
                ArenaPaintball.getInstance().getSLF4JLogger().warn("Invalid particle {} for paintball {}. Not spawning particle.", paintball.getParticle(), paintball);
            } else {
                for (Projectile projectile : projectiles) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (projectile.isDead()) {
                                this.cancel();
                                return;
                            }

                            player.getPlayer().getWorld().spawnParticle(particle, projectile.getLocation(), 1, 0.0, 0.0, 0.0, 0);
                        }
                    }.runTaskTimer(ArenaPaintball.getInstance(), 0, 1);
                }
            }
        }
    }

    private Snowball launchPaintball(Paintball paintball, ArenaPlayer player) {
        return player.getPlayer().launchProjectile(Snowball.class, null, snowball -> {
            snowball.setMetadata(PAINTBALL_META_KEY, new FixedMetadataValue(ArenaPaintball.getInstance(), paintball));

            if (paintball.getSpreadHorizontal() != 0 || paintball.getSpreadVertical() != 0) {
                Vector mod = new Vector((Math.random() - 0.5D) * paintball.getSpreadHorizontal(), (Math.random() - 0.5D) * paintball.getSpreadVertical(), (Math.random() - 0.5D) * paintball.getSpreadHorizontal());
                snowball.setVelocity(snowball.getVelocity().add(mod));
            }

            if (paintball.getVelocity() != -1) {
                snowball.setVelocity(snowball.getVelocity().multiply(paintball.getVelocity()));
            }

            if (paintball.getDisplayItem() != null) {
                snowball.setItem(paintball.getDisplayItem());
            }
        });
    }

    // For the events below:
    //   We'd only ever capture this event if the conditions in the event resolvers
    //   are met, so we can safely run the below code without having to check for a
    //   lot of conditions that would otherwise be necessary.

    @ArenaEventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Paintball paintball = (Paintball) projectile.getMetadata(PAINTBALL_META_KEY).get(0).value();
        if (paintball == null) {
            return;
        }

        if (paintball.getDamageRadius() > 0) {
            for (Player player : projectile.getWorld().getNearbyEntitiesByType(Player.class, projectile.getLocation(), paintball.getDamageRadius())) {
                if (player.equals(projectile.getShooter())) {
                    continue;
                }

                for (CustomEffect<?> effect : paintball.getHitEffects()) {
                    effect.play(player);
                }

                player.damage(paintball.getPaintballDamage(), projectile);
                if (paintball.getHitSound() != null && projectile.getShooter() instanceof Player shooter) {
                    shooter.playSound(shooter.getLocation(), paintball.getHitSound(), 1, 1);
                }
            }
        }

        if (!paintball.shouldColorMap()) {
            return;
        }

        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer((Player) projectile.getShooter());
        Bounds bounds = arenaPlayer.getCompetition().getMap().getBounds();
        if (bounds == null || arenaPlayer.getTeam() == null) {
            return;
        }

        int radius = paintball.getColorRadius();
        Color color = arenaPlayer.getTeam().getColor();

        DyeColor closestColor = DyeColor.values()[0];
        double closestDistance = Double.MAX_VALUE;
        for (DyeColor dyeColor : DyeColor.values()) {
            Color dyeColorColor = new Color(dyeColor.getColor().asRGB());
            double distance = Math.pow(dyeColorColor.getRed() - color.getRed(), 2) +
                    Math.pow(dyeColorColor.getGreen() - color.getGreen(), 2) +
                    Math.pow(dyeColorColor.getBlue() - color.getBlue(), 2);

            if (distance < closestDistance) {
                closestColor = dyeColor;
                closestDistance = distance;
            }
        }

        Material material = ItemColor.get(closestColor, ItemColor.Category.WOOL);
        Block hitBlock = event.getHitBlock();

        // Get all blocks in a spherical radius
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) {
                        continue;
                    }

                    Location loc = hitBlock.getLocation().add(x, y, z);
                    Block block = loc.getBlock();
                    if (bounds.isInside(loc) && block.isSolid()) {
                        block.setType(material, false);
                    }
                }
            }
        }
    }
}
