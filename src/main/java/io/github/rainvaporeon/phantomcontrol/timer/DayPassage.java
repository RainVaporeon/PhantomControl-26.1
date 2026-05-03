package io.github.rainvaporeon.phantomcontrol.timer;

import io.github.rainvaporeon.phantomcontrol.EntryPoint;
import io.github.rainvaporeon.phantomcontrol.utils.RegionSleepMap;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DayPassage {
    private static BukkitTask r;
    // holds True if: Day, and False if: Night
    private static final Map<UUID, Boolean> dayPassageMap = new HashMap<>(4, 0.25F);

    public static void init() {
        if (r != null) throw new IllegalStateException("DayPassage#init() invoked whilst active");
        r = Bukkit.getScheduler().runTaskTimer(
                EntryPoint.getInstance(),
                () -> EntryPoint.getInstance().getServer().getWorlds().forEach(w -> {
                    if (w.isDayTime() && dayPassageMap.getOrDefault(w.getUID(), false)) {
                        dayPassageMap.put(w.getUID(), true);
                        RegionSleepMap.DEFAULT.tickDayPassing(w);
                    } else {
                        dayPassageMap.put(w.getUID(), false);
                    }
                }),
                1000,
                1000
        );
    }
}
