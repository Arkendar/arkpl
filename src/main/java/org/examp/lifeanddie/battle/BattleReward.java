package org.examp.lifeanddie.battle;
import org.bukkit.entity.Player;

public class BattleReward {
    private int experienceReward;
    private double moneyReward;

    public BattleReward(int experienceReward, double moneyReward) {
        this.experienceReward = experienceReward;
        this.moneyReward = moneyReward;
    }

    public void giveReward(Player winner) {
        // Даем опыт
        winner.giveExp(experienceReward);

        // Даем деньги (предполагая, что у вас есть экономический плагин)
        // Пример для Vault:
        // if (economy != null) {
        //     economy.depositPlayer(winner, moneyReward);
        // }

        winner.sendMessage("You have received a reward for winning the battle!");
        winner.sendMessage("Experience: " + experienceReward);
        winner.sendMessage("Money: " + moneyReward);
    }
}