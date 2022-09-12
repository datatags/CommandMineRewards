package me.datatags.commandminerewards.commands.block;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

import me.datatags.commandminerewards.CMRBlockState;
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
        return new String[] { "genericRewards" };
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
        return new ArgType[] { ArgType.REWARD_SECTION };
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        List<CMRBlockState> blocks;
        try {
            blocks = new RewardGroup(args[0]).getBlocks();
        } catch (InvalidRewardGroupException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        }
        if (blocks.size() < 1) {
            sender.sendMessage(ChatColor.RED + "There are no blocks in that group.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "The blocks that trigger rewards are:  ");
            for (CMRBlockState state : blocks) {
                String block = state.getType().toString();
                String message = ChatColor.GREEN + block;
                // Not using standard `true` because that involves unboxing
                if (state.getGrowth() == Boolean.TRUE) {
                    message += ", fully grown";
                } else if (state.getGrowth() == Boolean.FALSE) {
                    message += ", not fully grown";
                }
                if (state.getMultiplier() != 1) {
                    message += ", multiplier " + state.getMultiplier();
                }
                sender.sendMessage(message);
            }
        }
        return true;
    }
}
