package org.examp.lifeanddie;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import org.bukkit.Material;

import java.util.*;

import static org.bukkit.Particle.FLAME;

public class AbilityUseListener implements Listener {

    private final Map<UUID, Long> dashCooldowns = new HashMap<>();
    private final Map<UUID, Long> fortressCooldowns = new HashMap<>();
    private final Map<UUID, Long> fireStrikeCooldowns = new HashMap<>();
    private final Map<UUID, Double> magicStaffCooldowns = new HashMap<>();
    private final Map<UUID, Long> flyCooldowns = new HashMap<>();
    private final Map<UUID, Long> spellDeathCooldowns = new HashMap<>();
    private final Map<UUID, Long> eruptionCooldowns = new HashMap<>();
    private final Map<UUID, Long> phaseArrowCooldowns = new HashMap<>();
    private final Map<UUID, Long> astralSphereCooldowns = new HashMap<>();
    private final Map<UUID, Long> earthJawsCooldowns = new HashMap<>();
    private final Map<UUID, Long> lightningStormCooldowns = new HashMap<>();
    private static final Map<UUID, Long> iceWaveCooldowns = new HashMap<>();
    private final Map<UUID, Long> chaosBearerCooldowns = new HashMap<>();
    private final Map<UUID, Long> burialCooldowns = new HashMap<>();
    private final Map<UUID, Long> lightHeavenCooldowns = new HashMap<>();
    private final Map<UUID, Long> iceCooldowns = new HashMap<>();
    private final Map<UUID, Long> dashTeleportCooldowns = new HashMap<>();
    private final Map<UUID, Long> skyfallCooldowns = new HashMap<>();
    private final Map<UUID, Long> wrathStormCooldowns = new HashMap<>();


    private static final long DASH_COOLDOWN = 7 * 1000;
    private static final long FORTRESS_COOLDOWN = 25 * 1000;
    private static final long FIRE_STRIKE_COOLDOWN = 15 * 1000;
    private static final double MAGIC_STAFF_COOLDOWN = 1.5 * 1000;
    private static final long FLY_COOLDOWN = 15 * 1000;
    private static final long SPELL_DEATH_COOLDOWN = 30 * 1000;
    private static final long ERUPTION_COOLDOWN = 25 * 1000;
    private static final long PHASE_ARROW_COOLDOWN = 20 * 1000;
    private static final long ASTRAL_SPHERE_COOLDOWN = 30 * 1000; // 30 секунд
    private static final long EARTH_JAWS_COOLDOWN = 20 * 1000; // 20 секунд
    private static final long LIGHTNING_STORM_COOLDOWN = 30 * 1000;
    private static final long ICE_WAVE_COOLDOWN = 30 * 1000; // 30 секунд
    private static final long CHAOS_BEARER_COOLDOWN = 5 * 1000; // 30 секунд
    private static final long BURIAL_COOLDOWN = 15 * 1000;
    private static final long LIGHT_HEAVEN_COOLDOWN = 15 * 1000;
    private static final long ICE_COOLDOWN = 15 * 1000;
    private static final long DASH_TELEPORT_COOLDOWN = 7 * 1000;
    private static final long SKYFALL_COOLDOWN = 7 * 1000;
    private static final long WRATH_STORM_COOLDOWN = 7 * 1000;


    @EventHandler
    public void onPlayerUseAbility(PlayerInteractEvent event) {
        if (WorldManager.isWorldRestricted(event.getPlayer().getWorld())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Вы не можете использовать умения в этом мире!");
        } else {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if (item == null || item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) {
                return;
            }

            String displayName = item.getItemMeta().getDisplayName();

            switch (displayName) {
                case "§eЗемляные Челюсти": // ARCHER
                    useEarthJaws(player);
                    break;
                case "§aРывок": //WARRIOR
                    useDash(player);
                    break;
                case "§bКрепость": //WARRIOR
                    useFortress(player);
                    break;
                case "§cОгненная кара": //WARRIOR
                    useFireStrike(player);
                    break;
                case "§bПосох": //MAGE
                    useMagicStaff(player);
                    break;
                case "§fВзлёт": //MAGE
                    useFly(player);
                    break;
                case "§8Туча": //MAGE
                    useSpellDeath(player);
                    break;
                case "§6Извержение": //MAGE
                    useEruption(player);
                    break;
                case "§dАстральная Сфера": //MAGE
                    useAstralSphere(player);
                    break;
                case "§bПронзающая стрела": //ARCHER
                    usePhaseArrow(player);
                    break;
                case "§bГроза": //MAGE
                    useLightningStorm(player);
                    break;
                case "§bЛедяная волна": //MAGE
                    useIceWave(player);
                    break;
                case "§4Несущий хаос": //WARRIOR
                    useChaosBearerAbility(player);
                    break;
                case "§7Погребение": //MAGE
                    useBurial(player);
                    break;
                case "§3Свет Небес": //MAGE
                    useLightHeaven(player);
                    break;
                case "§bЛёд": //MAGE
                    useIce(player);
                    break;
                case "§9Уклонение":
                    useDashTeleport(player); //WARRIOR
                    break;
                case "§4Падение": //WARRIOR
                    useSkyfall(player);
                    break;
                case "§cБуря Гнева": //WARRIOR
                    useWrathStorm(player);
                    break;
                default:
                    break;
            }
        }
    }

    public void useEarthJaws(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (earthJawsCooldowns.containsKey(playerId)) {
            long lastUseTime = earthJawsCooldowns.get(playerId);
            long cooldownTimeLeft = EARTH_JAWS_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Челюсти в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        earthJawsCooldowns.put(playerId, currentTime);
        Location targetLocation = player.getTargetBlock(null, 15).getLocation();
        summonJaws(player, targetLocation);

    }

    private void summonJaws(Player player, Location location) {
        World world = player.getWorld();

        // Спавним челюсти
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Location fangLocation = location.clone().add(new Vector(x, 1, z));

                // Проверяем, есть ли под челюстями блок
                Block blockBelow = fangLocation.getBlock().getRelative(BlockFace.DOWN);
                if (blockBelow.getType().isSolid()) {
                    // Спавним челюсти призывателя
                    world.spawnEntity(fangLocation, EntityType.EVOKER_FANGS);

                    world.getNearbyEntities(fangLocation, 1, 1, 1).forEach(entity -> {
                        if (entity instanceof Player && entity != player) {
                            Player targetPlayer = (Player) entity;
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3)); // Замедление на 3 секунды
                            targetPlayer.damage(4.0, player);
                        }
                    });
                }
            }
        }
    }


    private void useDash(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (dashCooldowns.containsKey(playerId)) {
            long lastUseTime = dashCooldowns.get(playerId);
            long cooldownTimeLeft = DASH_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Рывок в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        dashCooldowns.put(playerId, currentTime);

        removeFallDamage(player, 60);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 15) { // Спавним частицы в течение 40 тиков (2 секунды)
                    this.cancel();
                    return;
                }
                Location particleLocation = player.getLocation().clone();
                particleLocation.setY(particleLocation.getY() + 1.0);

                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 1, 0.3, 0.3, 0.3, 0.05);
                player.getWorld().spawnParticle(Particle.END_ROD, particleLocation, 10, 1, 0, 1, 0);
                count++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 2L);

        Vector direction = player.getLocation().getDirection().normalize();
        direction.multiply(5); // Рывок на 5 блоков

        Location newLocation = player.getLocation().add(direction);
        //dash
        double dashStrenght = 0.35;
        Vector dashVector = direction.multiply(dashStrenght);

        player.setVelocity(dashVector);
        player.getWorld().playSound(newLocation, Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
    }

    private void useFortress(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (fortressCooldowns.containsKey(playerId)) {
            long lastUseTime = fortressCooldowns.get(playerId);
            long cooldownTimeLeft = FORTRESS_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Крепость в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        fortressCooldowns.put(playerId, currentTime);

        Location location = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        diamondResist(player, location);
        player.getWorld().playSound(location, Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
    }

    private void useFireStrike(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (fireStrikeCooldowns.containsKey(playerId)) {
            long lastUseTime = fireStrikeCooldowns.get(playerId);
            long cooldownTimeLeft = FIRE_STRIKE_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Огненная кара в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        fireStrikeCooldowns.put(playerId, currentTime);

        Location startLocation = player.getLocation();
        Vector direction = startLocation.getDirection();
        startLocation.add(direction.multiply(2));

        for (int i = 0; i < 10; i++) {
            Location particleLocation = startLocation.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(FLAME, particleLocation, 20, 1, 1, 1, 0);
            player.getWorld().playSound(particleLocation, Sound.ITEM_FIRECHARGE_USE, 1.0F, 1.0F);

            for (Entity entity : player.getWorld().getNearbyEntities(particleLocation, 2, 2, 2)) {
                if (entity instanceof Player && entity != player) {
                    Player target = (Player) entity;
                    target.damage(5.0);
                    target.setFireTicks(60); // Поджог на 3 секунды
                }
            }
        }
    }

    public void diamondResist(Player player, Location location) {
        PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 1, true, false);
        player.addPotionEffect(resistance);
        PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, 300, 1, true, false);
        player.addPotionEffect(glowing);
        player.spawnParticle(Particle.TOTEM, location, 20, 1, 1, 1, 0);

        // Список для хранения задач и щитов
        List<BukkitRunnable> tasks = new ArrayList<>();
        List<ArmorStand> shields = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ArmorStand shield = location.getWorld().spawn(location.clone().add(0, 1, 0), ArmorStand.class);
            shield.setVisible(false);
            shield.setGravity(false);
            shield.setMarker(true);
            shield.setHelmet(new ItemStack(Material.SHIELD));
            shields.add(shield);

            int finalI = i;
            BukkitRunnable task = new BukkitRunnable() {
                double angle = finalI * 90;

                @Override
                public void run() {
                    if (!player.isOnline() || shield.isDead()) {
                        shield.remove();
                        cancel();
                        return;
                    }

                    Location playerLocation = player.getLocation().add(0, 1, 0);
                    angle += 5;  // Угол поворота
                    double radians = Math.toRadians(angle);
                    double radius = 1.5;  // Радиус вращения
                    double x = playerLocation.getX() + radius * Math.cos(radians);
                    double z = playerLocation.getZ() + radius * Math.sin(radians);
                    Location newLocation = new Location(playerLocation.getWorld(), x, playerLocation.getY(), z);
                    shield.teleport(newLocation);
                }
            };
            tasks.add(task);
            task.runTaskTimer(LifeAndDie.getInstance(), 0, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (BukkitRunnable task : tasks) {
                    task.cancel();
                }
                for (ArmorStand shield : shields) {
                    shield.remove();
                }
            }
        }.runTaskLater(LifeAndDie.getInstance(), 300); // 300 тиков = 15 секунд
    }

    private void useMagicStaff(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (magicStaffCooldowns.containsKey(playerId) && (currentTime - magicStaffCooldowns.get(playerId) < MAGIC_STAFF_COOLDOWN)) {
            //player.sendMessage("Заряжается");
            return;
        }

        magicStaffCooldowns.put(playerId, (double) currentTime);

        Location startLocation = player.getEyeLocation(); // Начинаем с уровня глаз игрока
        Vector direction = startLocation.getDirection().normalize();
        World world = player.getWorld();

        for (int i = 0; i < 10; i++) {
            Location particleLocation = startLocation.clone().add(direction.clone().multiply(i));
            if (particleLocation.getBlock().getType().isSolid()) {
                break;
            }

            world.spawnParticle(Particle.FLAME, particleLocation, 20, 0, 1, 0, 0);
            world.playSound(particleLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

            for (Entity entity : world.getNearbyEntities(particleLocation, 1, 1, 1)) {
                if (entity instanceof LivingEntity && entity != player) {
                    LivingEntity target = (LivingEntity) entity;
                    target.damage(6.0, player);
                    return;
                }
            }
        }
    }

    private void useFly(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (flyCooldowns.containsKey(playerId)) {
            long lastUseTime = flyCooldowns.get(playerId);
            long cooldownTimeLeft = FLY_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Взлёт в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        flyCooldowns.put(playerId, currentTime);

        Vector direction = new Vector(0, 1, 0);
        direction.multiply(1.5); // Уменьшаем силу рывка для большего контроля

        Location newLocation = player.getLocation().add(direction);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);

        // Взлёт
        player.setVelocity(direction);
        player.getWorld().playSound(newLocation, Sound.ITEM_ELYTRA_FLYING, 1.0F, 1.0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 0)); // 40 тиков = 3 секунды
        removeFallDamage(player, 100);
        Bukkit.getScheduler().runTaskLater(LifeAndDie.getInstance(), () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 0)); // 40 тиков = 3 секунды
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
        }, 70L); // 40 тиков = 2 секунды

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 10) {
                    this.cancel();
                    return;
                }
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.3, 0.3, 0.3, 0.05);
                count++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 4L);
    }

    private void removeFallDamage(Player player, int durationTicks) {
        player.setFallDistance(0);
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }
                player.setFallDistance(0);
                ticks++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 1L);
    }

    private void useSpellDeath(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (spellDeathCooldowns.containsKey(playerId)) {
            long lastUseTime = spellDeathCooldowns.get(playerId);
            long cooldownTimeLeft = SPELL_DEATH_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Смерть в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        spellDeathCooldowns.put(playerId, currentTime);
        Location targetLocation = player.getTargetBlock(null, 15).getLocation();

        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0)); // 60 тиков = 3 секунды

        player.playSound(player.getLocation(), Sound.ENTITY_VEX_DEATH, 2.0f, 2.0f);

        new BukkitRunnable() {
            int count = 0;
            final Random random = new Random();
            final Location cloudLocation = targetLocation.clone().add(0, 10, 0); // Локация для темной тучи

            @Override
            public void run() {
                if (count >= 20) { // Спавним 20 зелий (можно изменить)
                    this.cancel();
                    return;
                }

                double offsetX = (random.nextDouble() - 0.5) * 5;
                double offsetZ = (random.nextDouble() - 0.5) * 5;

                Location potionLocation = targetLocation.clone().add(offsetX, 10, offsetZ);

                ItemStack potionItem = new ItemStack(Material.SPLASH_POTION);
                PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
                meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 1), true);
                potionItem.setItemMeta(meta);

                ThrownPotion thrownPotion = potionLocation.getWorld().spawn(potionLocation, ThrownPotion.class);
                thrownPotion.setItem(potionItem);

                // Устанавливаем траекторию зелья вниз
                thrownPotion.setVelocity(new Vector(0, -0.5, 0));

                // Воспроизводим звук и частицы
                potionLocation.getWorld().playSound(potionLocation, Sound.ENTITY_WITCH_THROW, 0.5f, 1.0f);
                potionLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, potionLocation, 10, 0.2, 0.2, 0.2, 0.1);

                // Создаем темную тучу из частиц
                double radius = 3.0; // Радиус кругов
                double centerX1 = 0.0; // Центр первого круга по оси X
                double centerZ1 = 0.0; // Центр первого круга по оси Z


                for (double x = -radius; x <= radius; x += 0.25) {
                    for (double z = -radius; z <= radius; z += 0.25) {
                        double distanceFromCenter1 = Math.sqrt((x - centerX1) * (x - centerX1) + (z - centerZ1) * (z - centerZ1));
                        if (distanceFromCenter1 <= radius) {
                            Location particleLoc1 = cloudLocation.clone().add(x, 0, z);
                            cloudLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particleLoc1, 1, 0.2, 0.2, 0.2, 0.03);
                        }
                    }
                }


                count++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 5L); // Запускаем каждые 5 тиков (0.25 секунды)
    }

    private void useEruption(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (eruptionCooldowns.containsKey(playerId)) {
            long lastUseTime = eruptionCooldowns.get(playerId);
            long cooldownTimeLeft = ERUPTION_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Извержение в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        eruptionCooldowns.put(playerId, currentTime);

        Location location = player.getLocation();
        World world = player.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            final int DURATION = 140; // 10 секунд (20 тиков в секунду)

            public void run() {
                if (ticks >= DURATION) {
                    this.cancel();
                    return;
                }

                // Основной столб огня
                for (double y = 0; y < 7; y += 0.5) {
                    Location flameLoc = location.clone().add(0, y, 0);
                    world.spawnParticle(Particle.FLAME, flameLoc, 2, 0.3, 0, 0.3, 0.01);
                    world.spawnParticle(Particle.SMOKE_LARGE, flameLoc, 1, 0.3, 0, 0.3, 0.01);
                }

                // Разлетающиеся частицы
                for (int i = 0; i < 10; i++) {
                    double angle = Math.random() * 2 * Math.PI;
                    double radius = Math.random() * 3;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    double y = Math.random() * 7;

                    Location particleLoc = location.clone().add(x, y, z);
                    world.spawnParticle(Particle.LAVA, particleLoc, 1, 0, 0, 0, 0);

                    if (Math.random() < 0.1) { // 10% шанс появления дополнительных частиц
                        world.spawnParticle(Particle.FLAME, particleLoc, 1, 0.1, 0.1, 0.1, 0.05);
                    }
                }

                // Поджигаем игроков в радиусе каждый тик
                for (Entity entity : world.getNearbyEntities(location, 5, 5, 5)) {
                    if (entity instanceof Player && entity != player) {
                        entity.setFireTicks(40); // Поджигаем на 2 секунды
                    }
                }

                // Воспроизводим звук каждые 20 тиков (1 секунду)
                if (ticks % 20 == 0) {
                    world.playSound(location, Sound.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);
                    world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.5f);
                }

                ticks++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0, 1);

        // Начальный звук
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
    }

    private final Set<UUID> phaseArrowActive = new HashSet<>();

    private void usePhaseArrow(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (phaseArrowCooldowns.containsKey(playerId)) {
            long lastUseTime = phaseArrowCooldowns.get(playerId);
            long cooldownTimeLeft = PHASE_ARROW_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Фазовая стрела в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }


        phaseArrowCooldowns.put(playerId, currentTime);
        phaseArrowActive.add(playerId);
        player.sendMessage("Фазовая стрела активирована. Ваша следующая стрела пройдет сквозь стены.");
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID playerId = player.getUniqueId();

        if (phaseArrowActive.contains(playerId) && event.getProjectile() instanceof Arrow) {
            phaseArrowActive.remove(playerId);
            Arrow arrow = (Arrow) event.getProjectile();
            float force = event.getForce(); // Сила натяжения лука (от 0.0 до 1.0)

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks >= 200 || arrow.isDead()) { // Максимум 10 секунд или пока стрела не исчезнет
                        arrow.remove();
                        this.cancel();
                        return;
                    }

                    // Перемещаем стрелу вперед, игнорируя блоки
                    Location newLocation = arrow.getLocation().add(arrow.getVelocity());
                    arrow.teleport(newLocation);

                    // Спавним частицы
                    arrow.getWorld().spawnParticle(Particle.END_ROD, arrow.getLocation(), 5, 0.1, 0.1, 0.1, 0.01);

                    // Проверяем попадание в сущности
                    for (Entity entity : arrow.getNearbyEntities(0.5, 0.5, 0.5)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            LivingEntity target = (LivingEntity) entity;
                            // Вычисляем урон на основе силы натяжения лука
                            double damage = 2 + (force * 8); // От 2 до 10 урона
                            target.damage(damage, player);
                            arrow.remove();
                            this.cancel();
                            return;
                        }
                    }

                    ticks++;
                }
            }.runTaskTimer(LifeAndDie.getInstance(), 0L, 1L);
        }

    }

    private void useAstralSphere(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (astralSphereCooldowns.containsKey(playerId)) {
            long lastUseTime = astralSphereCooldowns.get(playerId);
            long cooldownTimeLeft = ASTRAL_SPHERE_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Астральная Сфера в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        astralSphereCooldowns.put(playerId, currentTime);
        player.sendMessage("Вы активировали Астральную Сферу!");

        Location sphereLocation = player.getLocation();

        new BukkitRunnable() {
            int ticks = 0;
            final double radius = 5.0;
            int particleCounter = 0;

            @Override
            public void run() {
                if (ticks >= 32) { // 10 секунд (4 тика в секунду)
                    this.cancel();
                    return;
                }

                // Отталкиваем ближайших врагов
                for (Entity entity : sphereLocation.getWorld().getNearbyEntities(sphereLocation, radius, radius, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Vector direction = entity.getLocation().subtract(sphereLocation).toVector().normalize();
                        entity.setVelocity(direction.multiply(1));
                    }
                }

                // Восстанавливаем здоровье игрока
                if (ticks % 4 == 0) { // Каждую секунду (каждые 4 тика при 5 тиках обновления)
                    double newHealth = Math.min(player.getHealth() + 1, player.getMaxHealth());
                    player.setHealth(newHealth);
                }

                // Создаем вращающиеся частицы
                if (particleCounter % 3 == 0) { // Каждые 15 тиков
                    for (double phi = 0; phi <= Math.PI; phi += Math.PI / 15) {
                        double y = radius * Math.cos(phi);
                        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 30) {
                            double x = radius * Math.cos(theta) * Math.sin(phi);
                            double z = radius * Math.sin(theta) * Math.sin(phi);

                            Location particleLoc = sphereLocation.clone().add(x, y + 1, z);
                            sphereLocation.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
                        }
                    }
                }

                particleCounter++;
                ticks++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 4L); // Обновление каждые 5 тиков

        // Воспроизводим звук активации
        player.getWorld().playSound(sphereLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
    }

    public void useLightningStorm(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (lightningStormCooldowns.containsKey(playerId)) {
            long lastUseTime = lightningStormCooldowns.get(playerId);
            long cooldownTimeLeft = LIGHTNING_STORM_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Гроза молний в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }
        lightningStormCooldowns.put(playerId, currentTime);
        player.sendMessage("Вы вызвали Грозу молний!");

        Location startLocation = player.getLocation();
        Vector direction = player.getLocation().getDirection().setY(0).normalize();

        new BukkitRunnable() {
            int ticks = 0;
            final Location currentLocation = startLocation.clone();

            @Override
            public void run() {
                if (ticks >= 20) { // 5 секунд (4 удара молнии в секунду)
                    this.cancel();
                    return;
                }

                // Создаем молнию
                currentLocation.add(direction.clone().multiply(2)); // Двигаемся на 2 блока вперед
                World world = currentLocation.getWorld();
                world.strikeLightningEffect(currentLocation);

                // Наносим урон и отбрасываем игроков в радиусе 3 блоков
                for (Entity entity : world.getNearbyEntities(currentLocation, 3, 3, 3)) {
                    if (entity instanceof Player && entity != player) {
                        Player targetPlayer = (Player) entity;
                        targetPlayer.damage(4.0, player); // Наносим 5 единиц урона

                        // Отбрасываем игрока
                        Vector knockback = targetPlayer.getLocation().subtract(currentLocation).toVector().normalize().multiply(1.5).setY(0.5);
                        targetPlayer.setVelocity(knockback);
                    }
                }

                // Создаем эффект частиц
                world.spawnParticle(Particle.FALLING_DUST, currentLocation, 50, 1, 1, 1, 0.1);

                ticks++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 5L); // Запускаем каждые 5 тиков (4 раза в секунду)

        // Воспроизводим звук активации
        player.getWorld().playSound(startLocation, Sound.ENTITY_LIGHTNING_THUNDER, 1.0f, 1.0f);
    }

    public static void useIceWave(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (iceWaveCooldowns.containsKey(playerId)) {
            long lastUseTime = iceWaveCooldowns.get(playerId);
            long cooldownTimeLeft = ICE_WAVE_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("§cЛедяная волна еще на перезарядке! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        iceWaveCooldowns.put(playerId, currentTime);
        player.sendMessage("§bВы активировали Ледяную волну!");

        Location center = player.getLocation();

        new BukkitRunnable() {
            double radius = 0.5;
            int ticks = 0;

            @Override
            public void run() {
                if (radius > 10 || ticks > 100) { // Максимальный радиус 10 блоков или 5 секунд
                    this.cancel();
                    return;
                }

                for (double angle = 0; angle < 360; angle += 10) {
                    double radians = Math.toRadians(angle);
                    double x = radius * Math.cos(radians);
                    double z = radius * Math.sin(radians);
                    Location particleLoc = center.clone().add(x, 0.1, z);

                    player.getWorld().spawnParticle(Particle.SNOWBALL, particleLoc, 5, 0.1, 0.1, 0.1, 0);
                    player.getWorld().spawnParticle(Particle.SNOW_SHOVEL, particleLoc, 1, 0, 0, 0, 0);
                }

                for (Entity entity : player.getWorld().getNearbyEntities(center, radius, 2, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity target = (LivingEntity) entity;
                        int slownessDuration = 100 + ticks; // Увеличиваем длительность с каждым тиком
                        int slownessAmplifier = Math.min(30, ticks / 5); // Увеличиваем силу замедления, максимум до 4 уровня

                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slownessDuration, slownessAmplifier));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, slownessDuration, 128));


                        target.damage(1); // Наносим небольшой урон

                        Vector knockback = target.getLocation().toVector().subtract(center.toVector()).normalize().multiply(0.3);
                        knockback.setY(0.1);
                        target.setVelocity(knockback);
                    }
                }

                player.getWorld().playSound(center, Sound.BLOCK_GLASS_BREAK, 0.5f, 1.0f);

                radius += 0.2;
                ticks++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 1L);
    }

    private void useChaosBearerAbility(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (chaosBearerCooldowns.containsKey(playerId)) {
            long lastUseTime = chaosBearerCooldowns.get(playerId);
            long cooldownTimeLeft = CHAOS_BEARER_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Несущий Хаос в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        chaosBearerCooldowns.put(playerId, currentTime);

        Location startLocation = player.getLocation();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1, false, false));
        removeFallDamage(player, 80);

        new BukkitRunnable() {
            final int dashes = 3; // Количество рывков
            int currentDash = 0;

            @Override
            public void run() {
                if (currentDash < dashes) {
                    Vector direction = player.getLocation().getDirection().normalize();
                    direction.multiply(5); // Рывок на 5 блоков

                    Location newLocation = player.getLocation().add(direction);


                        // Рывок
                        double dashStrength = 0.35;
                        Vector dashVector = direction.multiply(dashStrength);
                        player.setVelocity(dashVector);

                        player.getWorld().playSound(newLocation, Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

                        new BukkitRunnable() {
                            int count = 0;

                            @Override
                            public void run() {
                                if (count >= 15) { // Спавним частицы в течение 30 тиков (1.5 секунды)
                                    this.cancel();
                                    return;
                                }
                                player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getLocation(), 3, 0.3, 0.3, 0.3, 0.05);
                                count++;

                                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 1, 1, 1)) {
                                    if (entity instanceof Player && entity != player) {
                                        Location particleLocation = player.getLocation().clone();
                                        particleLocation.setY(particleLocation.getY() + 1.0);
                                        Player target = (Player) entity;
                                        target.damage(2.0);
                                        player.getWorld().spawnParticle(Particle.FALLING_DUST, particleLocation, 30, 1, 2, 1, 0.5);
                                    }
                                }
                            }
                        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 2L);

                    currentDash++;
                } else {
                    player.teleport(startLocation);
                    cancel();
                }
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0, 20); // Делает рывок каждую секунду (20 тиков)
    }

    private void useBurial(Player caster) {
        UUID casterId = caster.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (burialCooldowns.containsKey(casterId)) {
            long lastUseTime = burialCooldowns.get(casterId);
            long cooldownTimeLeft = BURIAL_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                caster.sendMessage("Погребение в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        Player target = getTargetPlayer(caster, 10); // 10 блоков - максимальная дистанция

        if (target == null) {
            caster.sendMessage("Цель не найдена!");
            return;
        }

        burialCooldowns.put(casterId, currentTime);

        Location originalLocation = target.getLocation().clone();
        Location teleportLocation = originalLocation.clone().add(0, -4, 0);

        // Телепортация цели вниз
        target.teleport(teleportLocation);

        // Задержка перед телепортацией обратно
        new BukkitRunnable() {
            @Override
            public void run() {
                target.teleport(originalLocation);

                // Дополнительные эффекты после возвращения
                target.getWorld().spawnParticle(Particle.FALLING_DUST, target.getLocation(), 50, 0.2, 2.2, 0.2, 0.1);
            }
        }.runTaskLater(LifeAndDie.getInstance(), 60); // 3 секунды (60 тиков)
    }

    private Player getTargetPlayer(Player caster, int maxDistance) {
        List<Entity> nearbyEntities = caster.getNearbyEntities(maxDistance, maxDistance, maxDistance);
        Vector direction = caster.getLocation().getDirection();
        Player target = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player && entity != caster) {
                Vector toEntity = entity.getLocation().toVector().subtract(caster.getLocation().toVector());
                double angle = direction.angle(toEntity);
                double distance = toEntity.length();

                if (angle < Math.PI / 8 && distance < closestDistance) { // угол примерно 22.5 градуса
                    closestDistance = distance;
                    target = (Player) entity;
                }
                entity.getWorld().spawnParticle(Particle.FALLING_DUST, entity.getLocation(), 50, 0.3, 2.2, 0.3, 0.1);
            }
        }

        return target;
    }

    private void useLightHeaven(Player player) {
        UUID playerUniqueId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (lightHeavenCooldowns.containsKey(playerUniqueId)) {
            long lastUseTime = lightHeavenCooldowns.get(playerUniqueId);
            long cooldownTimeLeft = LIGHT_HEAVEN_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Свет Небес в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }


        lightHeavenCooldowns.put(playerUniqueId, currentTime);

        // Определяем точку, где будет призыв
        Location targetLocation = player.getTargetBlock(null, 100).getLocation().add(0, 1, 0); // Призыв на блок, на который смотрит игрок

        // Создаем светящийся столб и эффекты
        new BukkitRunnable() {
            int duration = 0;

            @Override
            public void run() {
                if (duration >= 20) { // 10 секунд (10 * 20 тиков)
                    this.cancel();
                    return;
                }
                int PARTICLE_COUNT = 20;
                int CIRCLE_COUNT = 20;
                // Создание частиц
                for (int circle = 0; circle < CIRCLE_COUNT; circle++) {
                    double altitude = circle * 0.5; // Высота для каждого круга
                    double radius = 1.0 - (circle * 0.05); // Уменьшаем радиус на 0.1 с каждым новым кругом
                    for (int i = 0; i < PARTICLE_COUNT; i++) {
                        double angle = (2 * Math.PI / PARTICLE_COUNT) * i; // Угол для каждой частицы
                        double x = Math.cos(angle) * radius; // Используем уменьшающийся радиус
                        double z = Math.sin(angle) * radius; // Используем уменьшающийся радиус

                        // Позиция частицы
                        Location particleLocation = targetLocation.clone().add(x, altitude, z);
                        targetLocation.getWorld().spawnParticle(Particle.END_ROD, particleLocation, 1, 0, 0, 0, 0); // Создаем частицы
                    }
                }

                // Лечение союзников и урон противникам
                for (Entity entity : targetLocation.getWorld().getNearbyEntities(targetLocation, 2, 5, 2)) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        if (livingEntity.getUniqueId().equals(playerUniqueId)) {
                            // Лечим игрока
                            livingEntity.setHealth(Math.min(livingEntity.getHealth() + 2, livingEntity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue())); // Исцеление на 2 (10% от 20)
                        } else {
                            // Наносим урон врагам
                            livingEntity.damage(1); // Урон на 1 (5% от 20)
                        }
                    }
                }

                duration++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 10L);
        Location downTarget = targetLocation;
        for(int t = 0; t <= 3; t++) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 8) {
                double x = t * Math.cos(theta);
                double y = 2 * Math.exp(-0.1 * t) * Math.sin(t) + 0.5; // Уменьшили высоту до 0.5
                double z = t * Math.sin(theta);
                downTarget.add(x, y, z);
                player.spawnParticle(Particle.END_ROD, downTarget, 1, 0, 0, 0, 0);
                downTarget.subtract(x, y, z);
            }
            //downTarget.add(0, 0.5, 0); // Уменьшили увеличение высоты до 0.5
        }
    }
    private void useIce(Player player) {
        UUID playerUniqueId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (iceCooldowns.containsKey(playerUniqueId)) {
            long lastUseTime = iceCooldowns.get(playerUniqueId);
            long cooldownTimeLeft = ICE_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Лёд в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        iceCooldowns.put(playerUniqueId, currentTime);

        Location playerLocation = player.getLocation();
        Location targetLocation = player.getTargetBlock(null, 20).getLocation();

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20; // Увеличили количество тиков для достижения земли

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    this.cancel();
                    return;
                }

                double radius = 8 + Math.sin(ticks * 0.2) * 2;
                double angle = ticks * 0.5;

                for (int i = 0; i < 5; i++) {
                    double x = Math.cos(angle + i * Math.PI * 0.4) * radius;
                    double z = Math.sin(angle + i * Math.PI * 0.4) * radius;
                    Location startLocation = playerLocation.clone().add(x, 10 + Math.random() * 2, z);
                    Location landingLocation = targetLocation.clone().add(Math.random() * 4 - 2, 0, Math.random() * 4 - 2);

                    Vector direction = landingLocation.toVector().subtract(startLocation.toVector()).normalize().multiply(0.6); // Увеличили скорость

                    new BukkitRunnable() {
                        double t = 0;

                        @Override
                        public void run() {
                            if (t >= 40) {
                                this.cancel();
                                return;
                            }

                            Location particleLocation = startLocation.clone().add(direction.clone().multiply(t));
                            player.getWorld().spawnParticle(Particle.SNOWBALL, particleLocation, 1, 0, 0, 0, 0);
                            player.getWorld().spawnParticle(Particle.SPELL_INSTANT, particleLocation, 1, 0, 0, 0, 0);

                            for (Entity entity : particleLocation.getWorld().getNearbyEntities(particleLocation, 1, 1, 1)) {
                                if (entity instanceof LivingEntity && entity != player) {
                                    ((LivingEntity) entity).damage(5);
                                }
                            }

                            t += 1;
                        }
                    }.runTaskTimer(LifeAndDie.getInstance(), 0L, 1L);
                }

                ticks++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 3L);

        player.sendMessage("Вы использовали умение Лёд!");
    }

    private void useDashTeleport(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (dashTeleportCooldowns.containsKey(playerId)) {
            long lastUseTime = dashTeleportCooldowns.get(playerId);
            long cooldownTimeLeft = DASH_TELEPORT_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Телепортация в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }


        dashTeleportCooldowns.put(playerId, currentTime);

        // Определяем направление движения игрока
        Vector direction = player.getLocation().getDirection().normalize();

        // Количество телепортаций
        int teleportCount = 10;

        // Задержка между телепортациями
        int teleportDelay = 5; // Например, 5 тиков (0.25 секунды)

        new BukkitRunnable() {
            int currentTeleport = 0;

            @Override
            public void run() {
                if (currentTeleport >= teleportCount) {
                    this.cancel();
                    return;
                }

                // Определяем точку телепортации
                Location targetLocation = player.getLocation().clone();
                double teleportDistance = 1 + (Math.random() * 3); //дальность телепортации

                // Вычисляем вектор для телепортации в сторону
                Vector sideVector = direction.crossProduct(new Vector(0, 1, 0)).normalize().multiply(teleportDistance);

                // Определяем направление телепортации (влево или вправо)
                boolean teleportRight = Math.random() < 0.5;
                if (teleportRight) {
                    targetLocation.add(sideVector);
                } else {
                    targetLocation.subtract(sideVector);
                }

                // Проверяем, не пересекается ли точка телепортации со стеной или землей
                if (targetLocation.getBlock().getType() != Material.AIR) {
                    // Ищем ближайшую свободную точку для телепортации, двигаясь назад
                    targetLocation.subtract(sideVector); // Возвращаемся к исходной позиции
                    for (int i = 1; i <= 5; i++) {
                        if (targetLocation.add(sideVector.multiply(0.5)).getBlock().getType() == Material.AIR) { // Двигаемся с шагом 0.5
                            break;
                        }
                    }
                }

                // Телепортируем игрока
                player.teleport(targetLocation);

                // Создаем частицы телепортации
                player.getWorld().spawnParticle(Particle.SPELL_WITCH, targetLocation, 50, 0.3, 2, 0.3, 0.01);

                // Добавляем звук телепортации
                player.getWorld().playSound(targetLocation, Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

                currentTeleport++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, teleportDelay);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 10));
    }

    private void useSkyfall(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Проверяем наличие кулдауна
        if (skyfallCooldowns.containsKey(playerId)) {
            long lastUseTime = skyfallCooldowns.get(playerId);
            long cooldownTimeLeft = SKYFALL_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                // Преобразуем время в секунды
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Умение в откате! Осталось " + secondsLeft + " секунд.");
                return; // Выходим из метода, если кулдаун активен
            }
        }

        // Если кулдаун отсутствует, продолжаем выполнение
        skyfallCooldowns.put(playerId, currentTime);

        Player target = null;
        double closestAngle = Double.MAX_VALUE;

        Vector playerDirection = player.getLocation().getDirection().normalize();

        // Поиск ближайшего игрока по направлению взгляда
        for (Player potentialTarget : player.getWorld().getPlayers()) {
            if (potentialTarget.equals(player)) continue;

            Vector directionToTarget = potentialTarget.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            double angle = playerDirection.angle(directionToTarget);

            if (angle < closestAngle) {
                closestAngle = angle;
                target = potentialTarget;
            }
        }

        if (target == null) {
            player.sendMessage("Нет доступных целей для использования умения.");
            return;
        }

        Location targetLocation = target.getLocation().clone();
        targetLocation.setY(targetLocation.getY() + 10); // Телепортируем игрока на 5 блоков выше цели

        player.teleport(targetLocation);

        // Снимаем урон от падения на время действия умения
        removeFallDamage(player, 60);

        // Запуск визуальных эффектов и механики падения
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 20) { // Спавним частицы в течение 40 тиков (2 секунды)
                    this.cancel();
                    return;
                }

                Location particleLocation = player.getLocation().clone();
                particleLocation.setY(particleLocation.getY() + 1.0);

                player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation(), 30, 0.1, 0.1, 0.1, 0.01);
                player.getWorld().spawnParticle(Particle.LAVA, particleLocation, 10, 0.5, 0, 0.5, 0);

                for (Entity entity : particleLocation.getWorld().getNearbyEntities(particleLocation, 2, 2, 2)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).damage(3);
                    }
                }
                count++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 2L);
    }

    private void useWrathStorm(Player player) {
        UUID playerUniqueId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (wrathStormCooldowns.containsKey(playerUniqueId)) {
            long lastUseTime = wrathStormCooldowns.get(playerUniqueId);
            long cooldownTimeLeft = WRATH_STORM_COOLDOWN - (currentTime - lastUseTime);

            if (cooldownTimeLeft > 0) {
                long secondsLeft = (cooldownTimeLeft / 1000) + 1;
                player.sendMessage("Буря Гнева в откате! Осталось " + secondsLeft + " секунд.");
                return;
            }
        }

        wrathStormCooldowns.put(playerUniqueId, currentTime);

        Location playerLocation = player.getLocation();

        // Расчет радиуса и угла для каждого луча
        int numBeams = 5; // Количество лучей
        double angleStep = 360.0 / numBeams;

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20;

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    this.cancel();
                    return;
                }

                // Получение направления взгляда игрока
                Vector direction = player.getEyeLocation().getDirection();

                for (int i = 0; i < numBeams; i++) {
                    // Расчет начальной и конечной точек луча
                    double angle = angleStep * i;
                    double radius = 2 + Math.sin(ticks * 0.2) * 2.5; // Увеличиваем максимальный радиус
                    double xOffset = radius * Math.cos(Math.toRadians(angle));
                    double zOffset = radius * Math.sin(Math.toRadians(angle));

                    Location startLocation = playerLocation.clone().add(xOffset, 1 + Math.random() * 3, zOffset);
                    Location landingLocation = startLocation.clone().add(direction.clone().multiply(10)); // Используем направление взгляда

                    // Создание частиц и нанесение урона
                    for (double t = 0; t < 20; t += 1) {
                        Location particleLocation = startLocation.clone().add(direction.clone().multiply(t));
                        player.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 3, 0, 0, 0, 0);
                        //player.getWorld().spawnParticle(Particle.BLOCK_DUST, particleLocation, 3, 0, 0, 0, 0.5);
                        player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, particleLocation, 0, 3, 1, 3, 0.01);

                        for (Entity entity : particleLocation.getWorld().getNearbyEntities(particleLocation, 1, 1, 1)) {
                            if (entity instanceof LivingEntity && entity != player) {
                                ((LivingEntity) entity).damage(2);
                            }
                        }
                    }
                }

                ticks++;
            }
        }.runTaskTimer(LifeAndDie.getInstance(), 0L, 3L);

        player.sendMessage("Вы использовали умение Буря Гнева!");
    }
}