package org.examp.lifeanddie.fraction.fractions;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.examp.lifeanddie.fraction.Fraction;

public class FortressGuardiansFraction implements Fraction {
    private static final String NAME = "Стражи крепости";
    private static final double EXTRA_HEALTH = 8.0;
    private static final float KNOCKBACK_RESISTANCE = 0.3f;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void applyEffects(Player player) {
        // Увеличение здоровья
        setAttribute(player, Attribute.GENERIC_MAX_HEALTH, EXTRA_HEALTH);

        // Устойчивость к отбрасыванию
        player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(KNOCKBACK_RESISTANCE);
    }

    @Override
    public void removeEffects(Player player) {
        // Вернуть здоровье к исходному значению
        setAttribute(player, Attribute.GENERIC_MAX_HEALTH, -EXTRA_HEALTH);

        // Сбросить устойчивость к отбрасыванию
        player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
    }

    private void setAttribute(Player player, Attribute attribute, double amount) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(attributeInstance.getBaseValue() + amount);
        }
    }
}