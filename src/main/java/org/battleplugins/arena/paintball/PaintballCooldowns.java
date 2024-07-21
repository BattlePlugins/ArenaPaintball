package org.battleplugins.arena.paintball;

import org.battleplugins.arena.ArenaPlayer;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PaintballCooldowns {
    private final Map<Paintball, Instant> lastUse = new HashMap<>();

    public boolean canUse(Paintball paintball) {
        if (paintball.getCooldown().isZero()) {
            return true;
        }

        Instant lastUse = this.lastUse.get(paintball);
        if (lastUse == null) {
            return true;
        }

        return lastUse.plus(paintball.getCooldown()).isBefore(Instant.now());
    }

    public void use(Paintball paintball) {
        if (paintball.getCooldown().isZero()) {
            return;
        }

        this.lastUse.put(paintball, Instant.now());
    }

    public void reset(Paintball paintball) {
        this.lastUse.remove(paintball);
    }

    public void resetAll() {
        this.lastUse.clear();
    }

    public Duration getRemainingTime(Paintball paintball) {
        Instant lastUse = this.lastUse.get(paintball);
        if (lastUse == null) {
            return Duration.ZERO;
        }

        return Duration.between(Instant.now(), lastUse.plus(paintball.getCooldown()).plus(1, TimeUnit.SECONDS.toChronoUnit()));
    }

    public static PaintballCooldowns getOrCreate(ArenaPlayer player) {
        PaintballCooldowns cooldowns = player.getMetadata(PaintballCooldowns.class);
        if (cooldowns == null) {
            cooldowns = new PaintballCooldowns();
            player.setMetadata(PaintballCooldowns.class, cooldowns);
        }

        return cooldowns;
    }
}
