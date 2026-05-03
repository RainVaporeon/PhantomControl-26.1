package io.github.rainvaporeon.phantomcontrol.commands;

import io.github.rainvaporeon.phantomcontrol.EntryPoint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jspecify.annotations.NonNull;

import java.util.List;

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
        }

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

        return false;
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

    private static final List<String> layer1Args = List.of("global", "time");
    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 0) return layer1Args;
        if (args.length == 1) return layer1Args.stream().filter(s -> s.startsWith(args[0])).toList();
        return List.of();
    }
}
