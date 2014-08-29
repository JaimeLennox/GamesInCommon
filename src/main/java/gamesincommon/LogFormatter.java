package gamesincommon;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {
	// Adapted from code taken from:
	// https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/net/minecraft/server/ConsoleLogFormatter.java
	// No credit claimed for this section.

	private SimpleDateFormat b;

	public LogFormatter() {
		this.b = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public String format(LogRecord logrecord) {
		StringBuilder stringbuilder = new StringBuilder();

		stringbuilder.append(this.b.format(Long.valueOf(logrecord.getMillis())));
		stringbuilder.append(" [").append(logrecord.getLevel().getName()).append("] ");
		stringbuilder.append(this.formatMessage(logrecord));
		stringbuilder.append(System.lineSeparator());
		
		Throwable throwable = logrecord.getThrown();
		if (throwable != null) {
			StringWriter stringwriter = new StringWriter();
			throwable.printStackTrace(new PrintWriter(stringwriter));
			stringbuilder.append(stringwriter.toString());
		}
		return stringbuilder.toString();
	}
}