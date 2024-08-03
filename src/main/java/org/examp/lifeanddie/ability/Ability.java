package org.examp.lifeanddie.ability;

import org.bukkit.entity.Player;
import org.examp.lifeanddie.PlayerData;

public interface Ability {
    void use(Player player, PlayerData playerData);
    String getName();
}