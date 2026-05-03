package io.github.rainvaporeon.phantomcontrol.listeners;

import io.github.rainvaporeon.phantomcontrol.utils.PlayerSleepMap;
import io.github.rainvaporeon.phantomcontrol.utils.RegionSleepMap;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerSleepEventHandler implements Listener {

    @EventHandler
    public void onPlayerSleeping(PlayerDeepSleepEvent event) {
        RegionSleepMap.DEFAULT.registerPlayerSlept(event.getPlayer().getWorld());
        PlayerSleepMap.DEFAULT.registerPlayerSlept(event.getPlayer());
    }
}
