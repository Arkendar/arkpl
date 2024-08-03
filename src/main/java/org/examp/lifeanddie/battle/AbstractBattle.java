package org.examp.lifeanddie.battle;

import org.bukkit.entity.Player;
import java.util.List;

public abstract class AbstractBattle {
    protected List<Player> participants;
    protected BattleState state;
    protected BattleArena arena;
    protected long startTime;
    protected long endTime;
    protected BattleConfig config;

    public AbstractBattle(List<Player> participants, BattleArena arena, BattleConfig config) {
        this.participants = participants;
        this.arena = arena;
        this.config = config;
        this.state = BattleState.WAITING;
    }

    public abstract void start();
    public abstract void end();
    public boolean isFinished() {
        return state != BattleState.FINISHED;
    }
    protected abstract void teleportPlayers();
    protected abstract void resetPlayersState();

    protected void onPlayerKill(Player killer, Player victim) {
        //
    }

        public BattleState getState() {
        return state;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public BattleArena getArena() {
        return arena;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
