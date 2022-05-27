package me.datatags.commandminerewards.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.block.BlockState;

import com.gmail.nossr50.mcMMO;
import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.GlobalConfigManager;

public class McMMOHook {
    private Object cm;
    private Method isTrueMethod;
    private boolean mcMMOEnabled = false;
    public McMMOHook() {
        if (!GlobalConfigManager.getInstance().isMcMMOHookEnabled()) return;
        try {
            cm = mcMMO.class.getMethod("getPlaceStore").invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e2) {
            CMRLogger.error("Failed to get mcMMO chunk storage");
            e2.printStackTrace();
            return;
        }
        Class<?> chunkManagerClass = cm.getClass();
        CMRLogger.debug("Identified mcMMO chunk store as: " + (chunkManagerClass.getName().contains("chunkmeta") ? "legacy" : "current"));
        try {
            isTrueMethod = chunkManagerClass.getMethod("isTrue", BlockState.class);
        } catch (NoSuchMethodException | SecurityException e) {
            CMRLogger.error("Failed to access chunk manager method isTrue");
            e.printStackTrace();
            return;
        }
        mcMMOEnabled = true;
        CMRLogger.info("Successfully hooked mcMMO!");
    }
    public boolean isTrue(BlockState state) {
        try {
            return (boolean) isTrueMethod.invoke(cm, state);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            CMRLogger.error("Something unexpected happened while checking with mcMMO:");
            e.printStackTrace();
            return false;
        }
    }
    public boolean isMcMMOEnabled() {
        return mcMMOEnabled;
    }
}
