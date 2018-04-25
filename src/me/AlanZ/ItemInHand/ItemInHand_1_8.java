package me.AlanZ.ItemInHand;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemInHand_1_8 implements ItemInHand {

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemInHand(Player player) {
		return player.getItemInHand();
	}

}
