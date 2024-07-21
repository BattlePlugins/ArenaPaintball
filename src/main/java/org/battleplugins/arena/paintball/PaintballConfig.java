package org.battleplugins.arena.paintball;

import org.battleplugins.arena.config.ArenaOption;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PaintballConfig {

    @ArenaOption(name = "paintballs", description = "The paintballs to be used in the arena.", required = true)
    private Map<String, Paintball> paintballs;

    @ArenaOption(name = "disable-pvp", description = "Whether pvp is disabled in the Paintball arena.", required = true)
    private boolean disablePvp;

    @ArenaOption(name = "send-cooldown-message", description = "Whether to send a cooldown message to the player.", required = true)
    private boolean sendCoooldownMessage;

    @Nullable
    public Paintball getPaintball(String name) {
        return this.paintballs == null ? null : this.paintballs.get(name);
    }

    public boolean isPvpDisabled() {
        return this.disablePvp;
    }

    public boolean shouldSendCooldownMessage() {
        return this.sendCoooldownMessage;
    }
}
