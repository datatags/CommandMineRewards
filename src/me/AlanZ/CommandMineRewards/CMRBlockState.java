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
		if (this.type != b.type) return false;
		if (this.growth == null) {
			return b.growth == null; // we can't do null.equals() so we have to do this bit of gymnastics to make this work
		}
		return this.growth.equals(b.growth);
	}
}
