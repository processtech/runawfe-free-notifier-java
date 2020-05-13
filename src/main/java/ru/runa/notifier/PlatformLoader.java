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
package ru.runa.notifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ru.runa.notifier.util.ImageManager;

/**
 * Created on 2006
 *
 * @author Gritsenko_S
 */
public class PlatformLoader {

    private static final Log log = LogFactory.getLog(PlatformLoader.class);

    private Display display;
    private Shell invisibleShell;

    private PlatformLoader() {
        Display.setAppName("RUNA Task Notifier");

        display = new Display();

        invisibleShell = new Shell(display, SWT.NONE);

        ImageManager.initIcons();

        Shell shell = new Shell(invisibleShell, SWT.TOOL);
        FillLayout fillLayout = new FillLayout();
        fillLayout.marginHeight = 0;
        fillLayout.marginWidth = 0;
        shell.setLayout(fillLayout);

        Label label = new Label(shell, SWT.NONE);
        label.setImage(ImageManager.imageSplash);

        shell.pack();

        shell.setText("SWT RUNA");

        Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
        Rectangle shellBounds = shell.getBounds();
        int x = displayBounds.x + (displayBounds.width - shellBounds.width) >> 1;
        int y = displayBounds.y + (displayBounds.height - shellBounds.height) >> 1;
        shell.setLocation(x, y);

        shell.open();

        new GUI(display, invisibleShell).showGui();
    }

    public static void main(String[] args) {
        try {
            new PlatformLoader();
        } catch (Throwable th) {
            log.error("Critical error", th);
        }
    }
}
