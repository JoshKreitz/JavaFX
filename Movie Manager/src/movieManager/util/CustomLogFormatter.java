package movieManager.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

// Mostly copied from java.util.logging.SimpleFormatter, just added implementation for eclipse-parsable line numbers
public class CustomLogFormatter extends Formatter {

	// format string for printing the log record
	static String getLoggingProperty(String name) {
		return LogManager.getLogManager().getProperty(name);
	}

	@Override
	public String format(LogRecord record) {
		ZonedDateTime zdt = ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());
		String source;
		if (record.getSourceClassName() != null) {
			// ADD LINE NUMBER
			source = "(" + record.getSourceClassName() + ".java:"
					+ Thread.currentThread().getStackTrace()[8].getLineNumber() + ")";

			if (record.getSourceMethodName() != null) {
				source += " " + record.getSourceMethodName();
			}
		} else {
			source = record.getLoggerName();
		}
		String message = formatMessage(record);
		String throwable = "";
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			record.getThrown().printStackTrace(pw);
			pw.close();
			throwable = sw.toString();
		}

		String format = getLoggingProperty("java.util.logging.SimpleFormatter.format");

		return String.format(format, zdt, source, record.getLoggerName(), record.getLevel(), message, throwable);
	}
}