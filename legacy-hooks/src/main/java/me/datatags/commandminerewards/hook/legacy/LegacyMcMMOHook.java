package me.datatags.commandminerewards.hook.legacy;

import org.bukkit.block.BlockState;

import com.gmail.nossr50.mcMMO;

import me.datatags.commandminerewards.hook.interfaces.McMMOHook;

public class LegacyMcMMOHook implements McMMOHook {

    @Override
    public boolean isTrue(BlockState state) {
        return mcMMO.getPlaceStore().isTrue(state);
    }

}
