package org.battleplugins.arena.paintball;

import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.BattleArenaReloadEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArenaPaintball extends JavaPlugin implements Listener {
    public static Message COOLDOWN_MESSAGE = Messages.error("paintball-cooldown", "This item is on cooldown for {}!");
    public static EventActionType<GivePaintballAction> GIVE_PAINTBALL = EventActionType.create("give-paintball", GivePaintballAction.class, GivePaintballAction::new);

    private static ArenaPaintball instance;

    private PaintballConfig config;

    private final NamespacedKey paintballItemKey = new NamespacedKey(this, "paintball_item");

    @Override
    public void onEnable() {
        instance = this;

        this.getServer().getPluginManager().registerEvents(this, this);

        this.loadConfig(false);

        BattleArena.getInstance().registerArena(this, "Paintball", PaintballArena.class, PaintballArena::new);
    }

    @EventHandler
    public void onBattleArenaReload(BattleArenaReloadEvent event) {
        this.loadConfig(true);
    }

    public void loadConfig(boolean reload) {
        this.saveDefaultConfig();

        File configFile = new File(this.getDataFolder(), "config.yml");
        Configuration config = YamlConfiguration.loadConfiguration(configFile);
        try {
            this.config = ArenaConfigParser.newInstance(configFile.toPath(), PaintballConfig.class, config);
        } catch (ParseException e) {
            ParseException.handle(e);

            this.getSLF4JLogger().error("Failed to load Paintball configuration!");
            if (!reload) {
                this.getServer().getPluginManager().disablePlugin(this);
            }

            return;
        }

        Path dataFolder = this.getDataFolder().toPath();
        Path arenasPath = dataFolder.resolve("arenas");
        if (Files.notExists(arenasPath)) {
            this.saveResource("arenas/paintball.yml", false);
        }
    }

    public PaintballConfig getMainConfig() {
        return this.config;
    }

    public NamespacedKey getPaintballItemKey() {
        return this.paintballItemKey;
    }

    public static ArenaPaintball getInstance() {
        return instance;
    }
}
