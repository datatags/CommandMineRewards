package me.datatags.commandminerewards.commands.world;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.AreaNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class WorldRemoveCommand extends WorldCommand {
    @Override
    public String getName() {
        return "remove";
    }
    @Override
    public String getBasicDescription() {
        return "Removes an allowed world";
    }

    @Override
    public String getExtensiveDescription() {
        return "Removes a world in which rewards under a reward group, or globally, are allowed to occur. Adding no arguments will remove your current world from the global list if you are a player.";
    }

    @Override
    public String getUsage() {
        return "[world] [rewardSection]";
    }
    
    @Override
    public String[] getExamples() {
        return new String[] {"", "survival cropRewards"};
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public ArgType[] getArgs() {
        return new ArgType[] {ArgType.WORLD, ArgType.REWARD_SECTION};
    }
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.WORLD_MODIFY;
    }
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        GlobalConfigManager gcm = GlobalConfigManager.getInstance();
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Please specify a world to remove.");
                return false;
            }
            Player player = (Player)sender;
            try {
                gcm.removeGlobalAllowedWorld(player.getWorld().getName());
            } catch (AreaNotInListException e) {
                player.sendMessage(e.getMessage());
            }
            return true;
        }
        String world = args[0];
        if (args.length == 1) {
            try {
                gcm.removeGlobalAllowedWorld(world);
            } catch (AreaNotInListException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            sender.sendMessage(SUCCESS);
        } else if (args.length == 2) {
            String rewardSection = args[1];
            try {
                new RewardGroup(rewardSection).removeAllowedWorld(world);
            } catch (InvalidRewardGroupException | AreaNotInListException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            sender.sendMessage(SUCCESS);
        }
        return true;
    }
}
