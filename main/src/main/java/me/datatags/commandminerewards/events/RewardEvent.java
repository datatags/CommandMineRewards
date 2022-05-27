package me.datatags.commandminerewards.events;

import org.bukkit.event.Event;

import me.datatags.commandminerewards.Reward;

public abstract class RewardEvent extends Event {
	protected final Reward reward;
	public RewardEvent(Reward reward) {
		this.reward = reward;
	}

	public Reward getReward() {
		return reward;
	}
}
