package me.datatags.commandminerewards.commands.block;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.BlockAlreadyInListException;
import me.datatags.commandminerewards.Exceptions.InvalidMaterialException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class BlockAddCommand extends BlockCommand {
    @Override
    public String getName() {
        return "add";
    }
    @Override
    public String getBasicDescription() {
        return "Add a reward-triggering block";
    }

    @Override
    public String getExtensiveDescription() {
        return "Adds a block to the specified reward group block list, and updates the config accordingly. If no block is specified, uses the held block. If growth is true, crops must be fully-grown to harvest. If growth is false, crops must NOT be fully grown. If omitted, growth stage is ignored.";
    }

    @Override
    public String getUsage() {
        return "<rewardSection> [block] [growth]";
    }
    
    @Override
    public String[] getExamples() {
        return new String[] {"genericRewards cobblestone", "cropRewards wheat:true"};
    }
    
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.BLOCK_MODIFY;
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
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Please specify a block to add to the list.");
                return true;
            }
            Player player = (Player) sender;
            Material item = getItemInHand(player).getType();
            if (!item.isBlock() || item == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You are not holding a block!  Please either hold a block to add or manually specify one.");
                return true;
            }
            try {
                new RewardGroup(args[0]).addBlock(item);
            } catch (InvalidRewardGroupException | BlockAlreadyInListException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            player.sendMessage(ChatColor.GREEN + "Item " + item.toString().toLowerCase() + " added to blocks list.");
            return true;
        }
        // args.length == 2 or 3
        try {
            if (args.length == 2) {
                new RewardGroup(args[0]).addBlock(args[1].toLowerCase());
            } else { // args.length == 3
                new RewardGroup(args[0]).addBlock(args[1].toLowerCase(), args[2].toLowerCase());
            }
        } catch (InvalidRewardGroupException | BlockAlreadyInListException | InvalidMaterialException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + args[1].toLowerCase() + " successfully added to blocks list!");
        return true;
    }

}
