/*
 * This file is part of the RUNA WFE project.
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation; version 2.1 
 * of the License. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package ru.runa.notifier.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * Created on 2006
 * 
 * @author Gritsenko_S
 */
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
