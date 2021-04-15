package me.datatags.commandminerewards;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CMRLogger {
	private static File debugLog = new File(CommandMineRewards.getInstance().getDataFolder().getAbsolutePath() + File.separator + "debug.log");
	private static BufferedWriter debugWriter = null;
	private static SimpleDateFormat asdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
	private static GlobalConfigManager gcm = GlobalConfigManager.getInstance();
	private static Logger logger = CommandMineRewards.getInstance().getLogger();
	private static boolean debugMode = false;
	public static void logMessage(String msg) {
		logMessage(msg, Level.FINE);
	}

	public static void logMessage(String msg, Level level) {
		logMessage(msg, level, true);
	}

	public static void logMessage(String msg, Level level, boolean logToConsole) {
		if (logToConsole) logger.log(level.intValue() < Level.INFO.intValue() ? Level.INFO : level, msg); // nothing lower than info will normally be visible in logs so convert it to info
		if (debugMode) {
			try {
				debugWriter.append("[" + level.toString().replace("FINE", "DEBUG") + "] " + msg + "\n");
				debugWriter.flush();
			} catch (IOException e) {
				logger.severe("Failed to write to CMR debug log! Do we have write permission?");
				e.printStackTrace();
			}
		}
	}

	public static void debug(String msg) {
		debug(msg, true);
	}

	public static void debug(String msg, boolean logToConsole) {
		logMessage(msg, Level.FINE, (debugMode || gcm.isDebug()) && logToConsole); // we use log level FINE then substitute it for debug or INFO where required
	}

	public static void info(String msg) {
		logMessage(msg, Level.INFO);
	}

	public static void warning(String msg) {
		logMessage(msg, Level.WARNING);
	}

	public static void error(String msg) {
		logMessage(msg, Level.SEVERE);
	}
	protected static void closeDebugLog() {
		if (debugWriter != null) { // this check is required because CMR uses this method in onDisable
			debug("Debug logging has been disabled at " + asdf.format(new Date()) + "\n\n\n\n", false);
			try {
				debugWriter.close();
			} catch (IOException e) {
				logger.warning("Failed to close debug log");
				e.printStackTrace();
			}
		}
	}
	private static void enableDebugLog() { // DON'T use logMessage() in this method, it won't work
		try {
			if (debugLog.createNewFile()) { // will check if file exists before creating in a single operation. using an if would be redundant anyway.
				logger.info("CMR debug log was successfully created!");
			}
			debugWriter = new BufferedWriter(new FileWriter(debugLog, true));
			debug("Debug logging has been enabled at " + asdf.format(new Date()), false);
			CommandMineRewards cmr = CommandMineRewards.getInstance();
			debug("CMR version: " + cmr.getDescription().getVersion(), false);
			debug("Minecraft version: " + cmr.getFullMinecraftVersion(), false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe("Failed to create CMR debug log. Do we have write permissions?");
			//e.printStackTrace();
		}
	}
	public static boolean toggleDebugMode() {
		debugMode = !debugMode;
		if (debugMode) {
			enableDebugLog();
		} else {
			closeDebugLog();
		}
		return debugMode;
	}
	public static String getDebugLogPath() {
		return debugLog.getAbsolutePath();
	}
}
