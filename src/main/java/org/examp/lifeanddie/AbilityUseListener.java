package org.examp.lifeanddie;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.examp.lifeanddie.ability.*;
import org.examp.lifeanddie.player.PlayerData;

import java.util.*;

public class AbilityUseListener implements Listener {

    private final Map<String, Ability> abilities = new HashMap<>();
    private final PlayerData playerData;
    private final LifeAndDie plugin;
    private final CooldownDisplayManager cooldownDisplayManager;

    public AbilityUseListener(PlayerData playerData, LifeAndDie plugin, CooldownDisplayManager cooldownDisplayManager) {
        this.playerData = playerData;
        this.plugin = plugin;
        this.cooldownDisplayManager = cooldownDisplayManager;
        registerAbilities();
    }


    private void registerAbilities() {
        addAbility(new EarthJaws(plugin, playerData));
        addAbility(new Dash(plugin, playerData));
        addAbility(new Fortress(plugin, playerData));
        addAbility(new FireStrike(plugin, playerData));
        addAbility(new MagicStaff(plugin, playerData));
        addAbility(new Fly(plugin, playerData));
        addAbility(new Cloud(plugin, playerData));
        addAbility(new Eruption(plugin, playerData));
        addAbility(new AstralSphere(plugin, playerData));
        addAbility(new WaveArrows(plugin, playerData));
        addAbility(new LightningStorm(plugin, playerData));
        addAbility(new IceWave(plugin, playerData));
        addAbility(new ChaosBearer(plugin, playerData));
        addAbility(new Burial(plugin, playerData));
        addAbility(new LightHeaven(plugin, playerData));
        addAbility(new Ice(plugin, playerData));
        addAbility(new DashTeleport(plugin, playerData));
        addAbility(new Skyfall(plugin, playerData));
        addAbility(new WrathStorm(plugin, playerData));
        //new Можно рывок
    }

    private void addAbility(Ability ability) {
        abilities.put(ability.getName(), ability);
    }

    @EventHandler
    public void onPlayerUseAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = item.getItemMeta().getDisplayName();
        Ability ability = getAbilityByDisplayName(displayName);

        if (ability != null) {
            if (WorldManager.isAbilityRestricted(player.getWorld())) {
                event.setCancelled(true);
                player.sendMessage("Вы не можете использовать умения в этом мире!");
                return;
            }

            String abilityName = ability.getName();
            if (!playerData.isInCooldown(abilityName, player.getUniqueId())) {
                plugin.getLogger().info("Player " + player.getName() + " used ability " + abilityName);
                ability.use(player, playerData);

                // Начинаем отображение кулдауна
                int slot = player.getInventory().getHeldItemSlot();
                cooldownDisplayManager.startCooldownDisplay(player, abilityName, slot);
            } else {
                long timeLeft = playerData.getCooldownTimeLeft(abilityName, player.getUniqueId()) / 1000;
                player.sendMessage("Умение " + abilityName + " еще на кулдауне. Осталось " + timeLeft + " секунд.");
            }
        }
    }

    private Ability getAbilityByDisplayName(String displayName) {
        switch (displayName) {
            case "§eЗемляные Челюсти":
                return abilities.get("EARTH_JAWS");
            case "§aРывок":
                return abilities.get("DASH");
            case "§bКрепость":
                return abilities.get("FORTRESS");
            case "§cОгненная кара":
                return abilities.get("FIRE_STRIKE");
            case "§bПосох":
                return abilities.get("MAGIC_STAFF");
            case "§fВзлёт":
                return abilities.get("FLY");
            case "§8Туча":
                return abilities.get("CLOUD");
            case "§6Извержение":
                return abilities.get("ERUPTION");
            case "§dАстральная Сфера":
                return abilities.get("ASTRAL_SPHERE");
            case "§bВолна стрел":
                return abilities.get("WAVE_ARROWS");
            case "§bГроза":
                return abilities.get("LIGHTNING_STORM");
            case "§bЛедяная волна":
                return abilities.get("ICE_WAVE");
            case "§4Несущий хаос":
                return abilities.get("CHAOS_BEARER");
            case "§7Погребение":
                return abilities.get("BURIAL");
            case "§3Свет Небес":
                return abilities.get("LIGHT_HEAVEN");
            case "§bЛёд":
                return abilities.get("ICE");
            case "§9Дымка":
                return abilities.get("DASH_TELEPORT");
            case "§4Падение":
                return abilities.get("SKYFALL");
            case "§cБуря Гнева":
                return abilities.get("WRATH_STORM");
            default:
                return null;
        }
    }
}