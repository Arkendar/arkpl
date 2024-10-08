package org.examp.lifeanddie.player;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.examp.lifeanddie.LifeAndDie;

import java.util.*;
import static org.bukkit.Material.*;


public class PlayerClassManager implements Listener {


    private final PlayerData playerData;

    public PlayerClassManager(LifeAndDie plugin) {
        this.playerData = plugin.getPlayerData();
    }

    public void setPlayerClass(Player player, PlayerClass playerClass) {
        playerData.setPlayerClass(player.getUniqueId(), playerClass);
        equipPlayer(player, playerClass);
    }

    public boolean hasChosenClass(Player player) {
        return playerData.hasChosenClass(player);
    }

    private void equipPlayer(Player player, PlayerClass playerClass) {
        switch (playerClass) {
            case DEMON_SLAYER:
                equipDemonSlayer(player);
                break;
            case PHANTOM_HUNTER:
                equipPhantomHunter(player);
                break;
            case ARCHMAGE:
                equipArchmage(player);
                break;
            case LIGHTNING:
                equipLightning(player);
                break;
            case HEIR_ANCIENTS:
                equipHeirAncients(player);
                break;
            case MERCY:
                equipMercy(player);
                break;
            case ANGEL_OF_DEATH:
                equipAngelOfDeath(player);
                break;
            case GUARDIAN:
                equipGuardian(player);
                break;
        }
    }

    public ItemStack createEarthJaws() {
        ItemStack EarthJaws = new ItemStack(SHEARS);
        ItemMeta EarthJawsMeta = EarthJaws.getItemMeta();
        List<String> loreEarthJaws = new ArrayList<>();
        loreEarthJaws.add("§eСоздаёт челюсти из земли по направлению взгляда.");
        loreEarthJaws.add("§eОткат: 20 секунд.");
        EarthJawsMeta.setDisplayName("§eЗемляные Челюсти");
        EarthJawsMeta.setLore(loreEarthJaws);
        EarthJaws.setItemMeta(EarthJawsMeta);
        return EarthJaws;
    }

    public ItemStack createDashSkill() {
        ItemStack feather = new ItemStack(FEATHER);
        ItemMeta featherMeta = feather.getItemMeta();
        List<String> loreFeather = new ArrayList<>();
        loreFeather.add("§eДелает рывок по направлению взгляда.");
        loreFeather.add("§eОткат: 7 секунд.");
        featherMeta.setDisplayName("§aРывок");
        featherMeta.setLore(loreFeather);
        feather.setItemMeta(featherMeta);
        return feather;
    }

    public ItemStack createFortressSkill() {
        ItemStack diamond = new ItemStack(DIAMOND);
        ItemMeta diamondMeta = diamond.getItemMeta();
        diamondMeta.setDisplayName("§bКрепость");
        List<String> loreDiamond = new ArrayList<>();
        loreDiamond.add("§eДает сопротивление урону и свечение.");
        loreDiamond.add("§eОткат: 25 секунд. Время действия 15 секунд.");
        diamondMeta.setLore(loreDiamond);
        diamond.setItemMeta(diamondMeta);
        return diamond;
    }

    public ItemStack createFireStrikeSkill() {
        ItemStack blazePowder = new ItemStack(BLAZE_POWDER);
        ItemMeta blazePowderMeta = blazePowder.getItemMeta();
        List<String> loreBlaze = new ArrayList<>();
        blazePowderMeta.setDisplayName("§cОгненная кара");
        loreBlaze.add("§eПо направлению взгляда наносит урон и ненадолго поджигает");
        loreBlaze.add("§eОткат 15 секунд.");
        blazePowderMeta.setLore(loreBlaze);
        blazePowder.setItemMeta(blazePowderMeta);
        return blazePowder;
    }

    public ItemStack createFlightSkill() {
        ItemStack quartz = new ItemStack(QUARTZ);
        ItemMeta quartzMeta = quartz.getItemMeta();
        quartzMeta.setDisplayName("§fВзлёт");
        List<String> loreQuartz = new ArrayList<>();
        loreQuartz.add("§eПодкидывает вверх и удерживает недолго наверху.");
        loreQuartz.add("§eОткат: 15 секунд.");
        quartzMeta.setLore(loreQuartz);
        quartz.setItemMeta(quartzMeta);
        return quartz;
    }

    public ItemStack createDeathSkill() {
        ItemStack spellPotion = new ItemStack(ENCHANTED_BOOK);
        ItemMeta spellPotionMeta = spellPotion.getItemMeta();
        spellPotionMeta.setDisplayName("§8Туча");
        List<String> loreSpellPotion = new ArrayList<>();
        loreSpellPotion.add("§eСпавнит по направлению взгляда зелья вреда");
        loreSpellPotion.add("§eОткат: 30 секунд.");
        spellPotionMeta.setLore(loreSpellPotion);
        spellPotion.setItemMeta(spellPotionMeta);
        return spellPotion;
    }

    public ItemStack createEruptionSkill() {
        ItemStack spellEruption = new ItemStack(SULPHUR);
        ItemMeta spellEruptionMeta = spellEruption.getItemMeta();
        spellEruptionMeta.setDisplayName("§6Извержение");
        List<String> loreSpellEruption = new ArrayList<>();
        loreSpellEruption.add("§eПризывает огненный столб, который поджигает всех вокруг в течение 7 секунд");
        loreSpellEruption.add("§eОткат: 25 секунд.");
        spellEruptionMeta.setLore(loreSpellEruption);
        spellEruption.setItemMeta(spellEruptionMeta);
        return spellEruption;
    }

    public ItemStack createPhaseArrow() {
        ItemStack PhaseArrow = new ItemStack(PRISMARINE_SHARD);
        ItemMeta PhaseArrowMeta = PhaseArrow.getItemMeta();
        PhaseArrowMeta.setDisplayName("§bВолна стрел");
        List<String> lorePhaseArrow = new ArrayList<>();
        lorePhaseArrow.add("§eВыпускает веер стрел");
        lorePhaseArrow.add("§eОткат: 20 секунд.");
        PhaseArrowMeta.setLore(lorePhaseArrow);
        PhaseArrow.setItemMeta(PhaseArrowMeta);
        return PhaseArrow;
    }

    public ItemStack createAstralSphere(){
        ItemStack AstralSphere = new ItemStack(MAGMA_CREAM);
        ItemMeta AstralSphereMeta = AstralSphere.getItemMeta();
        AstralSphereMeta.setDisplayName("§dАстральная Сфера");
        List<String> loreAstralSphere = new ArrayList<>();
        loreAstralSphere.add("§eСоздает астральную сферу, которая медленно исцеляет тебя и отталкивает врагов.");
        loreAstralSphere.add("§eОткат: 30 секунд.");
        AstralSphereMeta.setLore(loreAstralSphere);
        AstralSphere.setItemMeta(AstralSphereMeta);
        return AstralSphere;
    }

    public ItemStack createLightningStorm() {
        ItemStack LightningStorm = new ItemStack(NETHER_STAR);
        ItemMeta LightningStormMeta = LightningStorm.getItemMeta();
        LightningStormMeta.setDisplayName("§bГроза");
        List<String> loreLightningStorm = new ArrayList<>();
        loreLightningStorm.add("§eПризывает молнии, которые идут по направлению взгляда, бьют и отталкивают.");
        loreLightningStorm.add("§eОткат 30 секунд.");
        LightningStormMeta.setLore(loreLightningStorm);
        LightningStorm.setItemMeta(LightningStormMeta);
        return LightningStorm;
    }

    public ItemStack createIceWave() {
        ItemStack IceWave = new ItemStack(SNOW);
        ItemMeta IceWaveMeta = IceWave.getItemMeta();
        IceWaveMeta.setDisplayName("§bЛедяная волна");
        List<String> loreIceWave = new ArrayList<>();
        loreIceWave.add("§eПризывает холод, который замораживает врагов");
        loreIceWave.add("§eОткат 30 секунд.");
        IceWaveMeta.setLore(loreIceWave);
        IceWave.setItemMeta(IceWaveMeta);
        return IceWave;
    }

    public ItemStack createChaosBearer() {
        ItemStack ChaosBearer = new ItemStack(RECORD_11);
        ItemMeta ChaosBearerMeta = ChaosBearer.getItemMeta();
        ChaosBearerMeta.setDisplayName("§4Несущий хаос");
        List<String> loreChaosBearer = new ArrayList<>();
        loreChaosBearer.add("§eНесколько рывков, которые наносят урон, а затем возвращение в начальную точку");
        loreChaosBearer.add("§eОткат 10 секунд.");
        ChaosBearerMeta.setLore(loreChaosBearer);
        ChaosBearer.setItemMeta(ChaosBearerMeta);
        return ChaosBearer;
    }

    public ItemStack createBurial() {
        ItemStack Burial = new ItemStack(MELON_SEEDS);
        ItemMeta BurialMeta = Burial.getItemMeta();
        BurialMeta.setDisplayName("§7Погребение");
        List<String> loreBurial = new ArrayList<>();
        loreBurial.add("§eИгрок, на которого наведен взор, уйдет под землю, а затем вернется");
        loreBurial.add("§eОткат 15 секунд.");
        BurialMeta.setLore(loreBurial);
        Burial.setItemMeta(BurialMeta);
        return Burial;
    }

    public ItemStack createLightHeaven() {
        ItemStack LightHeaven = new ItemStack(SUGAR);
        ItemMeta LightHeavenMeta = LightHeaven.getItemMeta();
        LightHeavenMeta.setDisplayName("§3Свет Небес");
        List<String> loreLightHeaven = new ArrayList<>();
        loreLightHeaven.add("§eПризывает в указанное место столб света исцеляющий союзников и вредящий противникам.");
        loreLightHeaven.add("§eОткат 20 секунд.");
        LightHeavenMeta.setLore(loreLightHeaven);
        LightHeaven.setItemMeta(LightHeavenMeta);
        return LightHeaven;
    }

    public ItemStack createIce() {
        ItemStack Ice = new ItemStack(Material.INK_SACK, 1, (short) 7);

        ItemMeta IceMeta = Ice.getItemMeta();
        IceMeta.setDisplayName("§bЛёд");
        List<String> loreIce = new ArrayList<>();
        loreIce.add("§eПризывает град из льда, который наносит урон противнику.");
        loreIce.add("§eОткат 20 секунд.");
        IceMeta.setLore(loreIce);
        Ice.setItemMeta(IceMeta);
        return Ice;
    }

    public ItemStack createDashTeleport() {
        ItemStack DashTeleport = new ItemStack(PAPER);

        ItemMeta DashTeleportMeta = DashTeleport.getItemMeta();
        DashTeleportMeta.setDisplayName("§9Дымка");
        List<String> loreDashTeleport = new ArrayList<>();
        loreDashTeleport.add("§eПризывает туман, рандомно телепортирует вокруг ослепленного противника.");
        loreDashTeleport.add("§eПрименение: не дальше 20ти блоков");
        loreDashTeleport.add("§eОткат 15 секунд.");
        DashTeleportMeta.setLore(loreDashTeleport);
        DashTeleport.setItemMeta(DashTeleportMeta);
        return DashTeleport;
    }

    public ItemStack createSkyfall() {
        ItemStack Skyfall = new ItemStack(GOLD_NUGGET);

        ItemMeta SkyfallMeta = Skyfall.getItemMeta();
        SkyfallMeta.setDisplayName("§4Падение");
        List<String> loreSkyfall = new ArrayList<>();
        loreSkyfall.add("§eТелепортирует над игроком и наносит урон при приземлении.");
        loreSkyfall.add("§eОткат 15 секунд.");
        SkyfallMeta.setLore(loreSkyfall);
        Skyfall.setItemMeta(SkyfallMeta);
        return Skyfall;
    }

    public ItemStack createWrathStorm() {
        ItemStack WrathStorm = new ItemStack(Material.INK_SACK, 1, (short) 1);

        ItemMeta WrathStormMeta = WrathStorm.getItemMeta();
        WrathStormMeta.setDisplayName("§cБуря Гнева");
        List<String> loreWrathStorm = new ArrayList<>();
        loreWrathStorm.add("§eУничтожает противника разрезающими снарядами");
        loreWrathStorm.add("§eУправляется направлением взгляда и из точки использования");
        loreWrathStorm.add("§eОткат 15 секунд.");
        WrathStormMeta.setLore(loreWrathStorm);
        WrathStorm.setItemMeta(WrathStormMeta);
        return WrathStorm;
    }

    public ItemStack createSword() {
        ItemStack sword = new ItemStack(IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName("§aЖелезный меч");
        List<String> loreSword = new ArrayList<>();
        loreSword.add("§eЭтот меч был создан с любовью");
        swordMeta.setLore(loreSword);
        swordMeta.setUnbreakable(true);
        sword.setItemMeta(swordMeta);
        return sword;
    }

    public ItemStack createStaff() {
        ItemStack staff = new ItemStack(BLAZE_ROD);
        ItemMeta staffMeta = staff.getItemMeta();
        staffMeta.setDisplayName("§bПосох");
        List<String> loreStaff = new ArrayList<>();
        loreStaff.add("§eЭтот посох не был создан с любовью");
        staffMeta.setLore(loreStaff);
        staff.setItemMeta(staffMeta);
        return staff;
    }

    public ItemStack createBow() {
        ItemStack bow = new ItemStack(BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName("§aЛук");
        List<String> lorebow = new ArrayList<>();
        lorebow.add("§e Неизвестно был ли этот лук создан с любовью");
        bowMeta.setLore(lorebow);
        bowMeta.setUnbreakable(true);
        bow.setItemMeta(bowMeta);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        return bow;
    }

    private void equipWarrior(Player player) {
        player.getInventory().clear();

        //sword
        player.getInventory().setItem(0,createSword());
        //Рывок
        player.getInventory().setItem(1, createDashSkill());
        //Крепость
        player.getInventory().setItem(2, createFortressSkill());
        //Огненная кара
        player.getInventory().setItem(3, createFireStrikeSkill());

        player.getInventory().setHelmet(new ItemStack(IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(IRON_BOOTS));
    }

    private void equipMage(Player player){
        player.getInventory().clear();

        //посох
        player.getInventory().setItem(0,createStaff());

        //ботинки на невесомость
        ItemStack ironBoots = new ItemStack(IRON_BOOTS);
        ItemMeta ironBootsMeta = ironBoots.getItemMeta();
        ironBootsMeta.setUnbreakable(true);
        ironBoots.setItemMeta(ironBootsMeta);
        ironBoots.addEnchantment(Enchantment.PROTECTION_FALL, 4);

        //Взлёт
        player.getInventory().setItem(1, createFlightSkill());
        //Туча
        player.getInventory().setItem(2, createDeathSkill());
        //Извержение
        player.getInventory().setItem(3, createEruptionSkill());

        player.getInventory().setBoots(ironBoots);
    }

    private void equipArcher(Player player){
        player.getInventory().clear();

        player.getInventory().setItem(0, createBow());

        //Пронзающая стрела
        player.getInventory().setItem(1, createPhaseArrow());
        //Астральная сфера
        player.getInventory().setItem(2, createAstralSphere());

        player.getInventory().setItem(3, createEarthJaws());

        player.getInventory().setItem(player.getInventory().firstEmpty(), new ItemStack(ARROW));

    }

    private void equipDemonSlayer(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createSword());
        player.getInventory().setItem(1, createDashSkill());
        player.getInventory().setItem(2, createFireStrikeSkill());
        player.getInventory().setItem(3, createSkyfall());
        player.getInventory().setItem(4, createDashTeleport());
    }

    private void equipPhantomHunter(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createBow());
        player.getInventory().setItem(1, createFlightSkill());
        player.getInventory().setItem(2, createDashTeleport());
        player.getInventory().setItem(3, createPhaseArrow());
        player.getInventory().setItem(4, createAstralSphere());

        player.getInventory().setItem(player.getInventory().firstEmpty(), new ItemStack(ARROW));
    }

    private void equipArchmage(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createStaff());
        player.getInventory().setItem(1, createFlightSkill());
        player.getInventory().setItem(2, createIce());
        player.getInventory().setItem(3, createFireStrikeSkill());
        player.getInventory().setItem(4, createEruptionSkill());
    }

    private void equipLightning(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createSword());
        player.getInventory().setItem(1, createDashTeleport());
        player.getInventory().setItem(2, createChaosBearer());
        player.getInventory().setItem(3, createLightningStorm());
        player.getInventory().setItem(4, createDeathSkill());
    }

    private void equipHeirAncients(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createStaff());
        player.getInventory().setItem(1, createFlightSkill());
        player.getInventory().setItem(2, createEarthJaws());
        player.getInventory().setItem(3, createWrathStorm());
        player.getInventory().setItem(4, createLightningStorm());
    }

    private void equipMercy(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createStaff());
        player.getInventory().setItem(1, createAstralSphere());
        player.getInventory().setItem(2, createBurial());
        player.getInventory().setItem(3, createLightHeaven());
        player.getInventory().setItem(4, createIceWave());
    }

    private void equipAngelOfDeath(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createSword());
        player.getInventory().setItem(1, createBurial());
        player.getInventory().setItem(2, createEruptionSkill());
        player.getInventory().setItem(3, createFireStrikeSkill());
        player.getInventory().setItem(4, createDeathSkill());
    }

    private void equipGuardian(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createSword());
        player.getInventory().setItem(1, createDashSkill());
        player.getInventory().setItem(2, createFortressSkill());
        player.getInventory().setItem(3, createSkyfall());
        player.getInventory().setItem(4, createWrathStorm());
    }
}
