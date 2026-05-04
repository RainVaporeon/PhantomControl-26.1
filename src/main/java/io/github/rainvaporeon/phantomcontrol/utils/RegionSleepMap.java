package io.github.rainvaporeon.phantomcontrol.utils;

import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionSleepMap {
    private final Map<UUID, Integer> worldMap;

    public static final RegionSleepMap DEFAULT = new RegionSleepMap();

    public RegionSleepMap() {
        this.worldMap = new HashMap<>(4, 0.25f);
    }

    public void registerPlayerSlept(World world) {
        worldMap.put(world.getUID(), 0);
    }

    /**
     * Gets days since a player slept on the world
     * @param world the world
     * @return days since a player last slept, or {@code 0} if this is first recorded
     */
    public int getLastSleptInWorld(World world) {
        if (worldMap.get(world.getUID()) == null) {
            registerPlayerSlept(world);
            return 0;
        }
        return worldMap.get(world.getUID());
    }

    /**
     * Ticks for a day's passing
     * @param world the world which had a day passed, or {@code null}
     *              to denote passing of all worlds
     */
    public void tickDayPassing(@Nullable World world) {
        if (world == null) {
            worldMap.replaceAll((_, day) -> day + 1); return;
        }
        worldMap.compute(world.getUID(), (_, days) -> days == null ? 1 : days + 1);
        world.getPlayers().forEach(PlayerSleepMap.DEFAULT::tickDayPassing);
    }

}
