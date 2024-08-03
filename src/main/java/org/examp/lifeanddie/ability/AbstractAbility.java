package org.examp.lifeanddie.ability;

import org.bukkit.entity.Player;
import org.examp.lifeanddie.LifeAndDie;
import org.examp.lifeanddie.PlayerData;

public abstract class AbstractAbility implements Ability {
    protected final LifeAndDie plugin;
    protected final PlayerData playerData;

    public AbstractAbility(LifeAndDie plugin, PlayerData playerData) {
        this.plugin = plugin;
        this.playerData = playerData;
    }

    protected void sendCooldownMessage(Player player, String abilityName, long cooldownTimeLeft) {
        long secondsLeft = (cooldownTimeLeft / 1000) + 1;
        player.sendMessage(abilityName + " в откате! Осталось " + secondsLeft + " секунд.");
    }
}