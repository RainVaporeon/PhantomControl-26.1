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
    // holds True if: Night, and False if: Day
    private static final Map<UUID, Boolean> dayPassageMap = new HashMap<>(4, 0.25F);

    public static void init() {
        if (r != null) throw new IllegalStateException("DayPassage#init() invoked whilst active");
        r = Bukkit.getScheduler().runTaskTimer(
                EntryPoint.getInstance(),
                () -> EntryPoint.getInstance().getServer().getWorlds().forEach(w -> {
                    // ok let's brain this actually
                    // switching states when day = a day passes
                    // ok
                    if (w.isDayTime()) {
                        // get a true here if we were still in night!
                        if (dayPassageMap.getOrDefault(w.getUID(), false)) {
                            RegionSleepMap.DEFAULT.tickDayPassing(w);
                        }
                        // set to false now! it'll be false until night
                        dayPassageMap.put(w.getUID(), false);
                    } else {
                        // it's nighttime! let's set it to true now
                        dayPassageMap.put(w.getUID(), true);
                    }
                }),
                1000,
                1000
        );
    }
}
