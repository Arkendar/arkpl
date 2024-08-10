package org.examp.lifeanddie.fraction;

import org.bukkit.entity.Player;

public interface Fraction {
    String getName();
    void applyEffects(Player player);
    void removeEffects(Player player);
}