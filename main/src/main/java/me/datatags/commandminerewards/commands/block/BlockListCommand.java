package me.datatags.commandminerewards.commands.block;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class BlockListCommand extends BlockCommand {
	@Override
	public String getName() {
		return "list";
	}
	
	@Override
	public String getBasicDescription() {
		return "List the blocks that trigger rewards";
	}
	
	@Override
	public String getExtensiveDescription() {
		return "Lists all blocks (including block data) that trigger rewards in a specified reward group.";
	}
	
	@Override
	public String getUsage() {
		return "<rewardSection>";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"genericRewards"};
	}
	
	@Override
	public int getMinArgs() {
		return 1;
	}
	
	@Override
	public int getMaxArgs() {
		return 1;
	}
	
	@Override
	public ArgType[] getArgs() {
		return new ArgType[] {ArgType.REWARD_SECTION};
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		Map<String,Boolean> blocks;
		try {
			blocks = new RewardGroup(args[0]).getBlocksWithData();
		} catch (InvalidRewardGroupException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
		if (blocks.size() < 1) {
			sender.sendMessage(ChatColor.RED + "There are no blocks in that group.");
		} else {
			sender.sendMessage(ChatColor.GREEN + "The blocks that trigger rewards are:  ");
			boolean duplicates = false;
			for (Entry<String,Boolean> entry : blocks.entrySet()) {
				if (entry.getKey().contains("$")) duplicates = true;
				String block = entry.getKey().toLowerCase().replace("$", "");
				if (entry.getValue() == null) {
					sender.sendMessage(ChatColor.GREEN + block);
				} else if (entry.getValue() == true) {
					sender.sendMessage(ChatColor.GREEN + block + ", fully grown.");
				} else {
					sender.sendMessage(ChatColor.GREEN + block + ", not fully grown");
				}
			}
			if (duplicates) {
				sender.sendMessage("There seems to be at least one duplicate item in the list.");
				sender.sendMessage("Fixing the duplicate item will help this plugin to run better.");
				sender.sendMessage("Note: This duplicate may take the form of *:true and *:false");
				sender.sendMessage("Instead, try just the block and it will cover both.");
			}
		}
		return true;
	}
}
