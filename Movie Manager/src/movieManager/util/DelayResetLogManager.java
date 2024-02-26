package movieManager.util;

import java.util.logging.LogManager;

/**
 * A simple subclass of the LogManager which allows control of when the loggers
 * are garbage collected. This is necessary to provide logging functionality in
 * the shutdown hook.
 */
public class DelayResetLogManager extends LogManager {
	static DelayResetLogManager instance;

	public DelayResetLogManager() {
		instance = this;
	}

	@Override
	public void reset() {
		/* don't reset yet. */
	}

	private void reset0() {
		super.reset();
	}

	public static void resetFinally() {
		instance.reset0();
	}
}
