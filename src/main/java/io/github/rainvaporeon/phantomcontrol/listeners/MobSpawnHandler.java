package io.github.rainvaporeon.phantomcontrol.listeners;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import io.github.rainvaporeon.phantomcontrol.EntryPoint;
import io.github.rainvaporeon.phantomcontrol.utils.PlayerSleepMap;
import io.github.rainvaporeon.phantomcontrol.utils.RegionSleepMap;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MobSpawnHandler implements Listener {

    @EventHandler
    public void onPhantomSpawning(PhantomPreSpawnEvent event) {
        World w = event.getSpawnLocation().getWorld();
        if (!(event.getSpawningEntity() instanceof Player ply)) return;

        boolean isGlobal = EntryPoint.getInstance().getConfig().getBoolean("global_sleep");

        int dayThreshold = EntryPoint.getInstance().getConfig().getInt("sleep_time");

        // note: we use -gt since waking up counts a day and i don't want to overcomplicate it
        if (isGlobal) {
            int worldTime = RegionSleepMap.DEFAULT.getLastSleptInWorld(w);

            if (dayThreshold > worldTime) {
                event.setShouldAbortSpawn(true);
                event.setCancelled(true);
            }
        } else {
            int playerTime = PlayerSleepMap.DEFAULT.getLastPlayerSlept(ply);

            if (dayThreshold > playerTime) {
                event.setShouldAbortSpawn(true);
                event.setCancelled(true);
            }
        }


    }
}
