package org.examp.lifeanddie;

import org.bukkit.World;

public class WorldManager {
    private static final String RESTRICTED_WORLD = "world"; // Замените на название вашего мира

    public static boolean isAbilityRestricted(World world) {
        return world.getName().equals(RESTRICTED_WORLD);
    }
}