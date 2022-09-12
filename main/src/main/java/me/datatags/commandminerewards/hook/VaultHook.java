package me.datatags.commandminerewards.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultHook {
    private final Economy eco;

    public VaultHook() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            eco = economyProvider.getProvider();
        } else {
            eco = null;
        }
    }

    public boolean isEconomyLinked() {
        return eco != null;
    }

    public void addMoney(Player player, double amount) {
        if (amount < 0) {
            removeMoney(player, -amount);
            return;
        }
        if (isEconomyLinked()) {
            eco.depositPlayer(player, amount);
        }
    }

    public void removeMoney(Player player, double amount) {
        if (isEconomyLinked()) {
            eco.withdrawPlayer(player, amount);
        }
    }
}
