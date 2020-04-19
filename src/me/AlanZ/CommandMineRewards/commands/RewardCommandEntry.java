package me.AlanZ.CommandMineRewards.commands;

import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.AlanZ.CommandMineRewards.commands.special.SpecialCommand;

public class RewardCommandEntry {
	private SpecialCommand sc = null;
	private String[] scArgs = null;
	private String command = null;
	private boolean placeholder;
	public RewardCommandEntry(String command) {
		this.command = command;
		this.placeholder = command.contains("%player%");
	}
	public RewardCommandEntry(SpecialCommand sc, String[] args) {
		this.sc = sc;
		this.scArgs = args;
		for (int i = 0; i < args.length; i++) {
			if (args[i].contains("%player%")) {
				placeholder = true;
				break;
			}
		}
	}
	public void execute(Player target) { // target only used if it's an SC
		if (sc == null) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), placeholder ? command.replace("%player%", target.getName()) : command);
		} else {
			String[] args = scArgs.clone();
			if (this.placeholder) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].contains("%player%")) {
						args[i] = args[i].replace("%player%", target.getName());
					}
				}
			}
			sc.onCommand(target, args);
		}
	}
	public String getCommand() {
		if (sc == null) {
			return command;
		} else {
			StringJoiner args = new StringJoiner(" ");
			for (String arg : scArgs) {
				args.add(arg);
			}
			return "!" + sc.getName() + " " + args.toString();
		}
	}
}
