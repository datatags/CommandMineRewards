package me.AlanZ.CommandMineRewards;

import org.bukkit.Material;

public class CMRBlockState {
	private Material type;
	private Boolean growth;
	public CMRBlockState(Material type, Boolean growth) {
		this.type = type;
		this.growth = growth;
	}
	public Material getType() {
		return type;
	}
	public Boolean getGrowth() {
		return growth;
	}
	public String toString() {
		if (this.growth == null) {
			return this.getType().toString() + ", null";
		} else {
			return this.getType().toString() + ", " + this.getGrowth().toString();
		}
	}
	public boolean equals(CMRBlockState b) {
		return this.type == b.type && ((this.growth == null && b.growth == null) || this.growth.equals(b.growth));
	}
}
