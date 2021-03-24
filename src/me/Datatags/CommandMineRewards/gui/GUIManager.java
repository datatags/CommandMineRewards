package me.Datatags.CommandMineRewards.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RGCacheListener;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.gui.conversations.AbandonListener;
import me.Datatags.CommandMineRewards.gui.conversations.CMRPrompt;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public class GUIManager implements RGCacheListener {
	private static GUIManager instance;
	private Map<Class<? extends CMRGUI>, Map<String /* RewardGroup */,Map<String /* Reward */,CMRGUI>>> guiCache = new HashMap<>();
	private ConversationFactory cf;
	private GUIManager() {
		this.cf = new ConversationFactory(CommandMineRewards.getInstance());
		cf.thatExcludesNonPlayersWithMessage(ChatColor.RED + "Only players can use the GUI");
		cf.withEscapeSequence("cancel");
		cf.withModality(false);
		cf.withLocalEcho(false);
	}
	public static GUIManager getInstance() {
		if (instance == null) {
			instance = new GUIManager();
		}
		return instance;
	}
	public CMRGUI getGUI(Class<? extends CMRGUI> cls, RewardGroup group, Reward reward) {
		Map<String,Map<String,CMRGUI>> sectionLevel = guiCache.computeIfAbsent(cls, k -> new HashMap<>());
		Map<String,CMRGUI> rewardLevel = sectionLevel.computeIfAbsent(group == null ? null :group.getName(), k -> new HashMap<>());
		return rewardLevel.computeIfAbsent(reward == null ? null : reward.getName(), k -> {
			@SuppressWarnings("unchecked")
			Constructor<? extends CMRGUI> con = (Constructor<? extends CMRGUI>) cls.getConstructors()[0];
			CMRGUI gui;
			try {
				if (con.getParameterCount() == 0) {
					gui = con.newInstance();
				} else if (con.getParameterCount() == 1) {
					gui = con.newInstance(group);
				} else if (con.getParameterCount() == 2) {
					gui = con.newInstance(group, reward);
				} else {
					CommandMineRewards.getInstance().getLogger().severe("Couldn't understand constructor for " + cls.getName());
					return null;
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
			return gui;
		});
	}
	public void startConversation(Player player, CMRPrompt prompt) {
		delayCloseGUI(player);
		Conversation convo = cf.addConversationAbandonedListener(new AbandonListener()).withFirstPrompt(prompt).buildConversation(player);
		convo.getContext().setSessionData("gcm", GlobalConfigManager.getInstance());
		convo.getContext().setSessionData("prompt", prompt); // used for AbandonListener
		player.sendMessage(ChatColor.RED + "Type 'cancel' to cancel");
		convo.begin();
	}
	public void delayCloseGUI(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.closeInventory();
			}
		}.runTaskLater(CommandMineRewards.getInstance(), 1);
	}
	public void reloadCache() {
		guiCache.clear();
	}
	public void unloadSection(String group) {
		guiCache.values().forEach(r -> r.remove(group));
	}
	public void reloadGroup(RewardGroup group) {
		unloadSection(group.getName());
	}
	
	public void unloadReward(RewardGroup group, String rewardName) {
		guiCache.values().forEach(r -> r.get(group.getName()).remove(rewardName));
	}
	public void reloadReward(RewardGroup group, Reward reward) {
		unloadReward(group, reward.getName());
	}
}
