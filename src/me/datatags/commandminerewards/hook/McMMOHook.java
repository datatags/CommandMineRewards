package me.datatags.commandminerewards.hook;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.blockmeta.ChunkManager;

public class McMMOHook {
	private ChunkManager cm;
	public McMMOHook() {
		cm = mcMMO.getPlaceStore();
	}
	public ChunkManager getPlaceStore() {
		return cm;
	}
}
