package me.datatags.commandminerewards.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.datatags.commandminerewards.CMRBlockManager;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.RGCacheListener;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.conversations.CMRPrompt;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class GUIManager implements RGCacheListener {
	private static GUIManager instance = null;
	public GUIManager() {
		CMRBlockManager.getInstance().registerListener(this);
	}
	private Map<UUID,GUIUserHolder> users = new HashMap<>();
	public static GUIManager getInstance() {
		if (instance == null) {
			instance = new GUIManager();
		}
		return instance;
	}
	public CMRGUI delayOpenGUI(GUIUserHolder holder, CMRGUI gui) {
		// you aren't supposed to open or close inventories while in the handler
		// of an inventory click event, so use this method instead.
		new BukkitRunnable() {
			@Override
			public void run() {
				gui.openFor(holder);
			}
		}.runTaskLater(CommandMineRewards.getInstance(), 1);
		return gui; // convenience
	}
	public void delayCloseGUI(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.closeInventory();
			}
		}.runTaskLater(CommandMineRewards.getInstance(), 1);
	}
	public void refreshAll() {
		refreshAllExcept(null);
	}
	public void refreshAllExcept(GUIUserHolder skip) {
		for (GUIUserHolder holder : users.values()) {
			if (holder.equals(skip)) continue;
			holder.updateGUI();
		}
	}
	public void removeUser(Player player) {
		GUIUserHolder ownerHolder = users.remove(player.getUniqueId());
		if (ownerHolder != null) {
			ownerHolder.clear();
			return;
		}
		for (GUIUserHolder holder : users.values()) {
			if (holder.containsUser(player)) {
				holder.removeHelper(player);
				return;
			}
		}
	}
	public GUIUserHolder getHolder(Player player) {
		for (GUIUserHolder holder : users.values()) {
			if (holder.containsUser(player)) {
				return holder;
			}
		}
		return null;
	}
	public GUIUserHolder getNewHolder(Player player, CMRGUI gui) {
		GUIUserHolder playerHolder = getHolder(player);
		boolean owner = playerHolder != null && playerHolder.getOwnerUUID().equals(player.getUniqueId());
		if (playerHolder != null && !owner) {
			playerHolder.removeHelper(player);
			playerHolder = null;
		}
		if (playerHolder == null) {
			playerHolder = new GUIUserHolder(player, gui);
			users.put(player.getUniqueId(), playerHolder);
		}
		return playerHolder;
	}
	@Override
	public void onGroupUnload(String group) {
		closeUnloadedRewards(group, null);
	}
	@Override
	public void onRewardUnload(RewardGroup group, String reward) {
		closeUnloadedRewards(group.getName(), reward);
	}
	public void closeUnloadedRewards(String group, String reward) {
		Set<GUIUserHolder> toDelete = new HashSet<>(users.values());
		toDelete.removeIf(h -> !h.isConversing() && !h.getGUI().isRewardInUse(group, reward));
		toDelete.removeIf(h -> h.isConversing() && !((CMRPrompt)h.getConversation().getContext().getSessionData("prompt")).isRewardInUse(group, reward));
		for (GUIUserHolder holder : toDelete) {
			closeHolder(holder);
			holder.getOwner().sendMessage(ChatColor.RED + "Your editing session has been terminated because the reward or reward section you were looking at has been deleted.");
		}
	}
	public void closeAll() {
		Set<GUIUserHolder> holders = new HashSet<>(users.values());
		for (GUIUserHolder holder : holders) {
			closeHolder(holder);
		}
	}
	public void closeHolder(GUIUserHolder holder) {
		if (holder.isConversing()) {
			holder.abandonConvo(); // AbandonListener will take care of the rest
		} else {
			holder.getOwner().closeInventory(); // GUIListener will take care of the rest
		}
	}
}
