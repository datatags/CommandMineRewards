package me.datatags.commandminerewards.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.commands.block.BlockCommand;
import me.datatags.commandminerewards.commands.cmd.CmdCommand;
import me.datatags.commandminerewards.commands.region.RegionCommand;
import me.datatags.commandminerewards.commands.reward.RewardCommand;
import me.datatags.commandminerewards.commands.silktouch.SilkTouchPolicyCommand;
import me.datatags.commandminerewards.commands.special.*;
import me.datatags.commandminerewards.commands.world.WorldCommand;

public class CommandDispatcher implements CommandExecutor {
    private static CommandDispatcher instance = null;
    private List<CMRCommand> commands = new ArrayList<>();
    private PluginDescriptionFile pdf;
    private CommandDispatcher() {
        pdf = CommandMineRewards.getInstance().getDescription();
        registerCommand(new HelpCommand());
        registerCommand(new MultiplierCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new LimitCommand());
        registerCommand(new BlockCommand());
        registerCommand(new CmdCommand());
        registerCommand(new RegionCommand());
        registerCommand(new RewardCommand());
        registerCommand(new SilkTouchPolicyCommand());
        registerCommand(new WorldCommand());
        registerCommand(new SpecialCommand());
        registerCommand(new GUICommand());
        registerCommand(new DebugCommand());
    }
    public static CommandDispatcher getInstance() {
        if (instance == null) {
            instance = new CommandDispatcher();
        }
        return instance;
    }
    protected void registerCommand(CMRCommand command) {
        command.init();
        commands.add(command);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "This server is running " + ChatColor.GOLD + pdf.getName() + " v" + pdf.getVersion() + ChatColor.GREEN + " by " + pdf.getAuthors().get(0));
            sender.sendMessage(ChatColor.GREEN + "Do " + ChatColor.GOLD + "/cmr help" + ChatColor.GREEN + " for commands.");
            return true;
        }
        CMRCommand cmrcmd = getCommand(args[0]);
        if (cmrcmd == null) {
            sender.sendMessage(ChatColor.RED + "Unknown CMR command: " + args[0] + ". Try /cmr help");
            return true;
        }
        if (!(cmrcmd instanceof CompoundCommand)) {
            executeCommand(cmrcmd, sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }
        CompoundCommand ccmd = (CompoundCommand) cmrcmd;
        // only top-level commands are registered here and we only have one level of compound commands
        Stream<CompoundCommand> names = ccmd.getChildren().stream();
        if (args.length == 1) {
            StringJoiner namesJoined = new StringJoiner(", ");
            names.forEach(n -> namesJoined.add(n.getName()));
            sender.sendMessage(ChatColor.YELLOW + "Available subcommands: " + namesJoined.toString() + ". For more info, do '/cmr help " + ccmd.getName() + " <subcommand>");
            return true;
        }
        CompoundCommand[] matching = names.filter(n -> n.getName().equalsIgnoreCase(args[1])).toArray(CompoundCommand[]::new);
        if (matching.length == 0) {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + args[1] + ". Try '/cmr help " + args[0] + "' for subcommands.");
            return true;
        }
        executeCommand(matching[0], sender, Arrays.copyOfRange(args, 2, args.length));
        return true;
    }
    private void executeCommand(CMRCommand cmd, CommandSender sender, String[] args) {
        if (args.length < cmd.getMinArgs()) {
            sender.sendMessage(ChatColor.RED + "Not enough args!");
            sender.sendMessage(ChatColor.RED + "Available args: " + cmd.getUsage());
            return;
        }
        if (args.length > cmd.getMaxArgs() && cmd.getMaxArgs() > -1) {
            sender.sendMessage(ChatColor.RED + "Too many args!");
            sender.sendMessage(ChatColor.RED + "Available args: " + cmd.getUsage());
            return;
        }
        if (!cmd.getPermission().attempt(sender)) return;
        if (!cmd.onCommand(sender, args)) {
            sender.sendMessage(ChatColor.RED + "Available args: " + cmd.getUsage());
        }
    }
    protected List<CMRCommand> getCommands() {
        return commands;
    }
    public CMRCommand getCommand(String name) {
        return getCommand(name, commands);
    }
    public CMRCommand getCommand(String name, List<? extends CMRCommand> commandList) {
        for (CMRCommand cmd : commandList) {
            if (cmd.getName().equalsIgnoreCase(name)) return cmd;
            String[] aliases = cmd.getAliases();
            if (aliases != null && aliases.length > 0) {
                for (String alias : aliases) {
                    if (name.equalsIgnoreCase(alias)) return cmd;
                }
            }
        }
        return null;
    }
    public SpecialCommand getSpecialCommand(String name) {
        CompoundCommand special = (CompoundCommand)getCommand("special");
        if (special == null)  {
            CMRLogger.warning("Couldn't find base special command?!");
            return null;
        }
        SpecialCommand cmd = (SpecialCommand) getCommand(name, special.getChildren());
        if (cmd == null) {
            CMRLogger.debug("Couldn't find special command " + name);
            return null;
        }
        return cmd;
    }
}
