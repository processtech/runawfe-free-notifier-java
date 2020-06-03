
package ru.runa.notifier.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class LayoutsManager {

	public static GridLayout createGridLayout(int cols, int marginWidth, int marginHeight) {
		return createGridLayout(cols, marginWidth, marginHeight, 5);
	}

	public static GridLayout createGridLayout(int cols, int marginWidth, int marginHeight, int verticalSpacing) {
		GridLayout g = new GridLayout(cols, false);
		g.marginHeight = marginHeight;
		g.marginWidth = marginWidth;
		g.verticalSpacing = verticalSpacing;
		g.horizontalSpacing = 5;
		return g;
	}

	public static GridData createGridData(int style, int horizontalSpan) {
		return createGridData(style, horizontalSpan, SWT.DEFAULT, SWT.DEFAULT);
	}

	public static GridData createGridData(int style, int horizontalSpan, int widthHint, int heightHint) {
		GridData g = new GridData(style);
		g.horizontalSpan = horizontalSpan;
		g.widthHint = widthHint;
		g.heightHint = heightHint;
		return g;
	}
}
