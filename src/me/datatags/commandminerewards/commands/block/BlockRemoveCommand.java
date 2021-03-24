package me.datatags.commandminerewards.commands.block;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.BlockNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class BlockRemoveCommand extends BlockCommand {
	@Override
	public String getName() {
		return "remove";
	}
	@Override
	public String getBasicDescription() {
		return "Removes a reward-triggering block";
	}

	@Override
	public String getExtensiveDescription() {
		return "Removes a reward-triggering block from the specified group. Similar parameters to '/cmr block add' apply.";
	}

	@Override
	public String getUsage() {
		return "<rewardSection> [block] [growth]";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"genericRewards diamond_block", "cropRewards wheat:true"};
	}

	@Override
	public int getMinArgs() {
		return 1;
	}

	@Override
	public int getMaxArgs() {
		return 3;
	}

	@Override
	public ArgType[] getArgs() {
		return new ArgType[] {ArgType.REWARD_SECTION, ArgType.BLOCK, ArgType.BOOLEAN};
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.BLOCK_MODIFY;
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Please specify a block to remove from the list.");
				return true;
			}
			Player player = (Player) sender;
			Material item = getItemInHand(player).getType();
			if (item == null || !item.isBlock()) {
				player.sendMessage(ChatColor.RED + "You are not holding a block!  Please either hold a block or manually specify a block to remove.");
				return true;
			}
			try {
				new RewardGroup(args[0]).removeBlock(item);
			} catch (InvalidRewardGroupException | BlockNotInListException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			player.sendMessage(ChatColor.GREEN + "The item " + item.toString().toLowerCase() + " was successfully removed.");
			return true;
		}
		if (args.length == 2) {
			try {
				new RewardGroup(args[0]).removeBlockRaw(args[1], args[1].contains(":") ? true : false);
			} catch (InvalidRewardGroupException | BlockNotInListException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "The item " + args[1].toLowerCase() + " was successfully removed.");
		} else if (args.length == 3) {
			Boolean data;
			if (args[2].equals("*")) {
				data = null;
			} else {
				if (args[2].equalsIgnoreCase("true")) {
					data = true;
				} else if (args[2].equalsIgnoreCase("false")) {
					data = false;
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid data value! Allowed values: true, false");
					return true;
				}
			}
			try {
				new RewardGroup(args[0]).removeBlock(args[1].toLowerCase(), data);
			} catch (InvalidRewardGroupException | BlockNotInListException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
		return true;
	}

}
