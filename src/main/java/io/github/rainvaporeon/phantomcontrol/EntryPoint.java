package io.github.rainvaporeon.phantomcontrol;

import io.github.rainvaporeon.phantomcontrol.commands.PhantomControlCommand;
import io.github.rainvaporeon.phantomcontrol.listeners.MobSpawnHandler;
import io.github.rainvaporeon.phantomcontrol.listeners.PlayerSleepEventHandler;
import io.github.rainvaporeon.phantomcontrol.timer.DayPassage;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class EntryPoint extends JavaPlugin {
    private static JavaPlugin INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        configureDefaults();
        this.registerCommandExecutor("phantom", new PhantomControlCommand());
        this.registerEvents(
                new PlayerSleepEventHandler(),
                new MobSpawnHandler()
        );

        DayPassage.init();
    }

    private void configureDefaults() {
        File f = new File("./plugins/PhantomControl.yml");
        FileConfiguration cfg = this.getConfig();

        try {
            cfg.load(f);
            return;
            // everywhere else is load default
        } catch (IOException ex) {
            this.getLogger().log(
                    Level.SEVERE, "Could not load configurations", ex
            );
        } catch (InvalidConfigurationException ex) {
            this.getLogger().log(Level.WARNING, "Could not load configurations", ex);
        }

        if (!f.exists()) {
            cfg.set("sleep_time", 3);
            cfg.set("global_sleep", false);
        }
        cfg.addDefault("sleep_time", 3);
        cfg.setComments("sleep_time", List.of(
                "The number of days before a player may start spawning Phantoms"
        ));
        cfg.addDefault("global_sleep", false);
        cfg.setComments("global_sleep", List.of(
                "Set to `true` if any player sleeping re-sets the insomnia timer for everyone"
        ));
        if (!f.exists()) {
            saveConfig();
        }


    }

    @Override
    public void onDisable() {
        this.saveConfig();
        super.onDisable();
    }

    public @NonNull FileConfiguration getConfig() {
        return super.getConfig();
    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }

    private void registerEvents(Listener... listeners) {
        for (Listener l : listeners) this.getServer().getPluginManager().registerEvents(l, this);
    }

    @SuppressWarnings("SameParameterValue")
    private void registerCommandExecutor(String name, CommandExecutor exec) {
        PluginCommand c = this.getCommand(name);
        if (c == null) {
            this.getLogger().warning(
                    String.format("Could not find command %s registered. Is it present in plugin.yml?", name)
            );
            return;
        }
        c.setExecutor(exec);
    }
}
