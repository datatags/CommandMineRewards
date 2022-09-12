package me.datatags.commandminerewards.commands.special;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.hook.VaultHook;

public class GiveMoneyCommand extends SpecialCommand {

    @Override
    public String getName() {
        return "givemoney";
    }

    @Override
    public String getBasicDescription() {
        return "Special command, used in reward commands.";
    }

    @Override
    public String getExtensiveDescription() {
        return "Gives (or takes from) the player the specified amount of money using Vault.\n" + ChatColor.RED + "PLEASE SEE /cmr help special FOR USAGE INFORMATION.";
    }

    @Override
    public String getUsage() {
        return "<amount>";
    }

    @Override
    public String[] getExamples() {
        return new String[] { "100", "-1.25" };
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
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
            return true;
        }
        if (args.length == 0) {
            CMRLogger.warning("Command error: Not enough args in /cmr " + getName() + " " + args);
            return true;
        }
        VaultHook hook = CommandMineRewards.getInstance().getVaultHook();
        if (hook == null || !hook.isEconomyLinked()) {
            CMRLogger.warning("Command error: /cmr " + getName() + " cannot be used if no economy is linked.");
            return true;
        }
        double amount;
        try {
            amount = Double.valueOf(args[0]);
        } catch (NumberFormatException e) {
            CMRLogger.warning("Command error: Invalid amount provided for /cmr " + getName());
            return true;
        }
        Player player = (Player) sender;
        if (amount < 0) {
            hook.removeMoney(player, -amount);
        } else {
            hook.addMoney(player, amount);
        }
        return true;
    }

}
