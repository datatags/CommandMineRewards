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
	private static File debugLog = null;
	private static BufferedWriter debugWriter = null;
	private static SimpleDateFormat asdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
	private static GlobalConfigManager gcm = GlobalConfigManager.getInstance();
	private static Logger logger = CommandMineRewards.getInstance().getLogger();
	static {
		initDebugLog();
	}
	public static void logMessage(String msg) {
		logMessage(msg, Level.FINE);
	}

	public static void logMessage(String msg, Level level) {
		logMessage(msg, level, true);
	}

	public static void logMessage(String msg, Level level, boolean logToConsole) {
		if (logToConsole) logger.log(level.intValue() < Level.INFO.intValue() ? Level.INFO : level, msg); // nothing lower than info will be logged so convert it to info
		if (gcm.isDebugLog()) {
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
		logMessage(msg, Level.FINE, gcm.getDebug() && logToConsole); // we use log level FINE then substitute it for debug or INFO where required
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

	protected static void initDebugLog() { // DON'T use logMessage() in this method, it won't work
		if (debugWriter != null) {
			try {
				debugWriter.close();
			} catch (IOException e) {
				logger.warning("Failed to close debug log");
				e.printStackTrace();
			}
		}
		if (gcm.isDebugLog()) {
			try {
				File log = new File(CommandMineRewards.getInstance().getDataFolder().getAbsolutePath() + File.separator + "debug.log");
				int mega = 1024 * 1024;
				if (log.createNewFile()) { // will check if file exists before creating in a single operation. using an if would be a redundant check anyway.
					logger.info("CMR debug log was successfully created!");
				} else if (log.length() > 8 * mega) {
					logger.severe("Your debug.log is over 8MB, we won't log any more debug messages until you reset it.");
					return;
				} else if (log.length() > mega) {
					logger.warning("Your debug.log is over 1MB, you should probably reset it or disable it if you don't need it.");
				}
				debugLog = log;
				debugWriter = new BufferedWriter(new FileWriter(debugLog, true));
				debug("CMR has started up at " + asdf.format(new Date()), false);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				logger.severe("Failed to create CMR debug log. Do we have write permissions?");
				//e.printStackTrace();
			}
		}
	}
	public static void close() {
		debug("CMR has been shut down at " + asdf.format(new Date()) + "\n\n\n\n", false);
		if (debugWriter != null) {
			try {
				debugWriter.close(); // don't use this.logMessage() past here, it won't work
			} catch (IOException e) {
				logger.warning("Failed to close debug log handle:");
				e.printStackTrace();
			}
		}
	}
}
