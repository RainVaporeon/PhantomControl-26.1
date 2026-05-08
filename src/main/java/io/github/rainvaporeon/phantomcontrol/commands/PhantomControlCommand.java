package io.github.rainvaporeon.phantomcontrol.commands;

import com.google.common.collect.ImmutableList;
import io.github.rainvaporeon.phantomcontrol.EntryPoint;
import io.github.rainvaporeon.phantomcontrol.utils.PlayerSleepMap;
import io.github.rainvaporeon.phantomcontrol.utils.RegionSleepMap;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("deprecation") // ignore paper
public class PhantomControlCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 0) return false;
        if (args.length == 1) {
            if ("global".equalsIgnoreCase(args[0])) {
                boolean enabled = toggleGlobalSleepStatReset();
                sender.sendMessage(
                        "[PhantomControl] Now, sleeping " +
                                (enabled ? "will" : "will no longer") +
                                " reset the phantom timer for everyone."
                );
                return true;
            }
            if ("time".equalsIgnoreCase(args[0])) {
                sender.sendMessage(
                        "[PhantomControl] Currently, it requires " +
                                this.getSleepDayThreshold() + " days of insomnia " +
                                "for phantoms to start spawning."
                );
                return true;
            }
            if ("query".equalsIgnoreCase(args[0])) {
                int playerTime = (sender instanceof Player p) ? PlayerSleepMap.DEFAULT.getLastPlayerSlept(p) : -1;
                int worldTime = (sender instanceof Entity e) ? RegionSleepMap.DEFAULT.getLastSleptInWorld(e.getWorld()) : -1;
                boolean printed = false;
                if (playerTime != -1) {
                    sender.sendMessage(
                            "[PhantomControl] You last slept " + playerTime + " days ago."
                    );
                    printed = true;
                }
                if (worldTime != -1) {
                    sender.sendMessage(
                            "[PhantomControl] The last time anyone slept in this world was " + worldTime + " days ago."
                    );
                    printed = true;
                }
                if (!printed) {
                    sender.sendMessage(
                            "[PhantomControl] Use /" + command.getName() + " query <user|all> to query available times."
                    );
                }
                return true;
            }
            if ("reload".equalsIgnoreCase(args[0])) {
                EntryPoint.getInstance().reloadConfig();
                sender.sendMessage("[PhantomControl] Reloaded!");
                return true;
            }
        }
        // args.length >= 2

        if ("time".equalsIgnoreCase(args[0])) {
            int days;
            try {
                days = Integer.parseInt(args[1]);
                if (days < 0) throw new NumberFormatException(); // lazy bounds check
            } catch (NumberFormatException ex) {
                sender.sendMessage("[PhantomControl] Please input a valid and positive integer.");
                return true;
            }

            setSleepDayThreshold(days);
            sender.sendMessage("[PhantomControl] Now, it will require " +
                    days + " days of insomnia for phantoms to start spawning.");
            return true;
        }

        if ("query".equalsIgnoreCase(args[0])) {
            String params = args[1];
            if (params.startsWith("!")) {
                if (searchWorld(params, sender)) return true;
            }
            if ("@all".equalsIgnoreCase(params)) {
                searchAll(sender);
                return true;
            }
            if (searchPlayer(params, sender)) return true;
            sender.sendMessage("[PhantomControl] Unrecognized query: " + params);
            return true;
        }

        return false;
    }

    private boolean searchWorld(String params, CommandSender sender) {
        if (!params.startsWith("!")) return false;
        String worldKey = params.substring(1);
        try {
            World w = sender.getServer().getWorld(NamespacedKey.fromString(worldKey));
            if (w == null) return false;

            int lastTime = RegionSleepMap.DEFAULT.getLastSleptInWorld(w);
            sender.sendMessage(
                    "[PhantomControl] Last time a player has slept in " + w.getName() + " was " + lastTime + " days ago."
            );
            return true;
        } catch (NullPointerException ex) {
            return false; // invalid key or invalid world; not concerned either way
        }
    }

    private boolean searchPlayer(String params, CommandSender sender) {
        Player searchPlayer = sender.getServer().getPlayerExact(params);
        if (searchPlayer == null) return false;
        int lastTime = PlayerSleepMap.DEFAULT.getLastPlayerSlept(searchPlayer);
        sender.sendMessage(
                "[PhantomControl] Last time " + searchPlayer.getName() + " slept was " + lastTime + " days ago."
        );
        return true;

    }

    private void searchAll(CommandSender sender) {
        Server srv = sender.getServer();

        List<World> worlds = srv.getWorlds();

        for (World w : worlds) {
            sender.sendMessage(
                    "[PhantomControl] In world " + w.getName() + ChatColor.GRAY + " (" + w.getKey() + ")" + ChatColor.RESET + ":"
            );
            sender.sendMessage(
                    "[PhantomControl] Last time a player has slept in the world: " + RegionSleepMap.DEFAULT.getLastSleptInWorld(w) + " days ago."
            );
            List<Player> ps = w.getPlayers();
            if (ps.isEmpty()) {
                sender.sendMessage(
                        "[PhantomControl] No players are currently in the world."
                );
                continue;
            }
            sender.sendMessage(
                    "[PhantomControl] For players:"
            );
            for (Player p : ps) {
                sender.sendMessage(
                        "- " + p.getName() + ": Last slept " + PlayerSleepMap.DEFAULT.getLastPlayerSlept(p) + " days ago."
                );
            }
        }
    }

    private int getSleepDayThreshold() {
        return EntryPoint.getInstance().getConfig().getInt("sleep_time");
    }

    private void setSleepDayThreshold(int days) {
        EntryPoint.getInstance().getConfig().set("sleep_time", days);
    }

    private boolean toggleGlobalSleepStatReset() {
        boolean b = !EntryPoint.getInstance().getConfig().getBoolean("global_sleep");
        EntryPoint.getInstance().getConfig().set("global_sleep", b);
        return b;
    }

    private static final List<String> layer1Args = List.of("global", "time", "query", "reload");
    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 0) return layer1Args;
        if (args.length == 1) return layer1Args.stream().filter(s -> s.startsWith(args[0])).toList();
        if (args.length == 2 && args[0].equalsIgnoreCase("query")) {
            return Stream.of(
                    sender.getServer().getWorlds().stream().map(
                            world -> String.format("!%s", world.getKey())
                    ).sorted(),
                    ImmutableList.copyOf(sender.getServer().getOnlinePlayers()).stream().map(
                            Player::getName
                    ).sorted(),
                    Stream.of("@all")
            ).flatMap(Function.identity()).filter(s -> s.startsWith(args[1])).toList();
        }
        return List.of();
    }
}
