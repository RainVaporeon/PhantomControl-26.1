package io.github.rainvaporeon.phantomcontrol.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSleepMap {
    private final Map<UUID, Integer> playerMap;

    public static final PlayerSleepMap DEFAULT = new PlayerSleepMap();

    public PlayerSleepMap() {
        this.playerMap = new HashMap<>(12, 0.25f);
    }

    public void registerPlayerSlept(Player player) {
        playerMap.put(player.getUniqueId(), 0);
    }

    /**
     * Gets days since a player slept on the world
     * @param player the world
     * @return days since a player last slept, or {@code 0} if this is first recorded
     */
    public int getLastPlayerSlept(Player player) {
        if (playerMap.get(player.getUniqueId()) == null) {
            registerPlayerSlept(player);
            return 0;
        }
        return playerMap.get(player.getUniqueId());
    }

    public void tickDayPassing(Player player) {
        playerMap.compute(player.getUniqueId(), (_, time) -> {
            if (time == null) return 1;
            return time + 1;
        });
    }
}
