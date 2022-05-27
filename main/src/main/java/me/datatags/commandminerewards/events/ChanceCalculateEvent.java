package me.datatags.commandminerewards.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.datatags.commandminerewards.Reward;

public class ChanceCalculateEvent extends RewardEvent {
    private static final HandlerList handlers = new HandlerList();
    protected final Player player;
    protected double chance;
    public ChanceCalculateEvent(Reward reward, Player player) {
        super(reward);
        this.player = player;
        this.chance = reward.getChance();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double newChance) {
        this.chance = newChance;
    }

    public Player getPlayer() {
        return player;
    }
}
