package me.AlanZ.CommandMineRewards.ItemInHand;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemInHand_1_9 implements ItemInHand {

	@Override
	public ItemStack getItemInHand(Player player) {
		return player.getInventory().getItemInMainHand();
	}

}
