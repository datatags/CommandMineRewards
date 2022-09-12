package me.datatags.commandminerewards;

import org.bukkit.Material;

import me.datatags.commandminerewards.state.StateManager;

public class CMRBlockState {
    private final Material type;
    private final Boolean growth;
    private final double multiplier;

    public CMRBlockState(String block, StateManager sm) {
        String[] segments = block.split("%", 2);
        String materialName = segments[0];
        String data = null;
        if (segments.length > 1) {
            try {
                multiplier = Double.parseDouble(segments[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid multiplier " + segments[1] + " for block " + materialName, e);
            }
        } else {
            multiplier = 1;
        }
        if (materialName.contains(":")) {
            segments = materialName.split(":", 2);
            materialName = segments[0];
            data = segments[1];
        }
        type = Material.matchMaterial(materialName);
        if (type == null) {
            throw new IllegalArgumentException("Invalid material " + materialName + " found when initializing handlers");
        }
        if (data == null) {
            growth = null;
            return;
        }
        if (!sm.canHaveData(type)) {
            throw new IllegalArgumentException("Type " + type.toString() + " does not grow!");
        }
        if (data.equalsIgnoreCase("true")) { // using if statements instead of Boolean.parse because if the user puts in garbage, Boolean.parse assumes false when we should notify the user and move on
            growth = true;
        } else if (data.equalsIgnoreCase("false")) {
            growth = false;
        } else {
            throw new IllegalArgumentException("Invalid growth identifier for material " + type.toString() + ": " + data + "\n"
                    + "Defaulting to any growth stage for " + type.toString());
        }
    }

    public Material getType() {
        return type;
    }

    public Boolean getGrowth() {
        return growth;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        if (this.growth == null) {
            return this.getType().toString() + ", null";
        } else {
            return this.getType().toString() + ", " + this.getGrowth().toString();
        }
    }

    public String compact() {
        StringBuilder compact = new StringBuilder(getType().toString());
        if (growth != null) {
            compact.append(':').append(growth);
        }
        if (multiplier != 1) {
            compact.append('%').append(multiplier);
        }
        return compact.toString();
    }

    public boolean equals(CMRBlockState b) {
        if (this.type != b.type) return false;
        if (this.growth == null) {
            return b.growth == null; // we can't do null.equals() so we have to do this bit of gymnastics to make this work
        }
        return this.growth.equals(b.growth);
    }
}
