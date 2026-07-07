package ru.runa.notifier.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class WidgetsManager {
	private static final int REPACK_INITIAL_DELAY_MS = 50;
	private static final int REPACK_RETRY_DELAY_MS = 100;
	private static final int REPACK_MAX_ATTEMPTS = 8;

	public static boolean isset(Widget widget) {
		return (widget != null && !widget.isDisposed());
	}

	public static void repackUntilStable(Shell shell) {
		Display display = shell.getDisplay();
		Point[] lastSize = { shell.getSize() };
		int[] attemptsLeft = { REPACK_MAX_ATTEMPTS };
		Runnable[] repack = new Runnable[1];
		repack[0] = () -> {
			if (shell.isDisposed()) {
				return;
			}

			shell.pack();
			shell.layout(true, true);

			Point size = shell.getSize();
			attemptsLeft[0]--;
			if (!size.equals(lastSize[0]) && attemptsLeft[0] > 0) {
				lastSize[0] = size;
				display.timerExec(REPACK_RETRY_DELAY_MS, repack[0]);
			}
		};
		display.timerExec(REPACK_INITIAL_DELAY_MS, repack[0]);
	}
}
