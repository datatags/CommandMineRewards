package me.datatags.commandminerewards.hook;

import org.bukkit.block.BlockState;

import com.gmail.nossr50.mcMMO;

import me.datatags.commandminerewards.hook.interfaces.McMMOHook;

public class McMMO2Hook implements McMMOHook {

    @Override
    public boolean isTrue(BlockState state) {
        return mcMMO.getPlaceStore().isTrue(state);
    }
}
