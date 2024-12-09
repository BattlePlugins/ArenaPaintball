package org.battleplugins.arena.paintball;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.util.CustomEffect;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class Paintball {

    @ArenaOption(name = "item", description = "The item to be used as a paintball.", required = true)
    private ItemStack item;

    @ArenaOption(name = "display-item", description = "The display item to be used as a paintball.")
    private ItemStack displayItem;

    @ArenaOption(name = "launch-sound", description = "The launch sound that is played when a player uses the paintball.")
    private String launchSound;

    @ArenaOption(name = "hit-sound", description = "The hit sound that is played when a player gets hit by the paintball.")
    private String hitSound;

    @ArenaOption(name = "particle", description = "The particle that will follow a paintball.")
    private String particle;

    @ArenaOption(name = "projectile-count", description = "The number of paintballs to launch.")
    private int projectileCount = 1;

    @ArenaOption(name = "spread-horizontal", description = "The horizontal spread of the paintball.")
    private double spreadHorizontal;

    @ArenaOption(name = "spread-vertical", description = "The vertical spread of the paintball.")
    private double spreadVertical;

    @ArenaOption(name = "velocity", description = "The velocity of the paintball.")
    private double velocity = -1;

    @ArenaOption(name = "use-item", description = "Whether the item should be used when thrown")
    private boolean useItem = true;

    @ArenaOption(name = "cooldown", description = "The cooldown between each paintball launch.")
    private Duration cooldown = Duration.ZERO;

    @ArenaOption(name = "color-map", description = "Whether paintballs should change the color of the map.")
    private boolean colorMap;

    @ArenaOption(name = "color-radius", description = "The radius of the color change when a paintball hits the ground.")
    private int colorRadius = 3;

    @ArenaOption(name = "damage-radius", description = "The radius of the damage when a paintball hits the ground.")
    private double damageRadius;

    @ArenaOption(name = "paintball-damage", description = "The amount of damage a paintball does.")
    private double paintballDamage = 10000;

    @ArenaOption(name = "hit-effects", description = "The effects that will be played when a player is hit by a paintball.")
    private List<CustomEffect<?>> hitEffects;

    public ItemStack getItem() {
        return this.item;
    }

    @Nullable
    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    @Nullable
    public String getLaunchSound() {
        return this.launchSound;
    }

    @Nullable
    public String getHitSound() {
        return this.hitSound;
    }

    @Nullable
    public String getParticle() {
        return this.particle;
    }

    public int getProjectileCount() {
        return this.projectileCount;
    }

    public double getSpreadHorizontal() {
        return this.spreadHorizontal;
    }

    public double getSpreadVertical() {
        return this.spreadVertical;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public boolean shouldUseItem() {
        return this.useItem;
    }

    public Duration getCooldown() {
        return this.cooldown;
    }

    public boolean shouldColorMap() {
        return this.colorMap;
    }

    public int getColorRadius() {
        return this.colorRadius;
    }

    public double getDamageRadius() {
        return this.damageRadius;
    }

    public double getPaintballDamage() {
        return this.paintballDamage;
    }

    public List<CustomEffect<?>> getHitEffects() {
        return this.hitEffects == null ? List.of() : List.copyOf(this.hitEffects);
    }
}
