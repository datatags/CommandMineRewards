package me.datatags.commandminerewards.commands.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.Exceptions.InvalidRewardException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class CmdTestCommand extends CmdCommand {
    @Override
    public String getName() {
        return "test";
    }
    @Override
    public String getBasicDescription() {
        return "Runs all commands in the reward.";
    }

    @Override
    public String getExtensiveDescription() {
        return "All commands in the reward are executed from the console just as they would be if the reward triggered by a block break. All prerequisites (world whitelist, etc.) are ignored. Specify an index to only run the command with that index.";
    }

    @Override
    public String getUsage() {
        return "<rewardSection> <reward> [index]";
    }
    
    @Override
    public String[] getExamples() {
        return new String[] {"genericRewards bigReward 0", "genericRewards smallReward"};
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.COMMAND_EXECUTE;
    }
    @Override
    public ArgType[] getArgs() {
        return new ArgType[] {ArgType.REWARD_SECTION, ArgType.REWARD};
    }
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String rewardSection = args[0];
        String rewardName = args[1];
        int index = -1;
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used as a player!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length > 2) {
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid index: " + args[2]);
            }
        }
        Reward reward;
        try {
            reward = new Reward(rewardSection, rewardName);
        } catch (InvalidRewardGroupException | InvalidRewardException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        }
        if (index >= reward.getCommands().size()) {
            sender.sendMessage(ChatColor.RED + "No command with index " + index);
            return true;
        }
        if (index == -1) {
            reward.execute(player, true);
        } else {
            reward.getCommands().get(index).execute(player);
        }
        sender.sendMessage(SUCCESS);
        return true;
    }
}
