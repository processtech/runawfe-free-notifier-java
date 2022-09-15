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

package ru.runa.notifier.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import ru.runa.notifier.GUI;
import ru.runa.notifier.util.ExtendedThread;
import ru.runa.notifier.util.ImageManager;
import ru.runa.notifier.util.LayoutsManager;
import ru.runa.notifier.util.WidgetsManager;

/**
 * Created on 2006
 * 
 * @author Gritsenko_S
 */
public class SystemTrayAlert {

    private static final int ANIMATION_DELAY = 500;

    private static final int ANIMATION_SLEEP = 10;

    private static final int ANIMATION_STEPS = 35;

    private ExtendedThread autoClosePopup;
    private Display display;
    private boolean mouseInPopup;
    private Font popupBoldFont;
    private Color popupBorderColor;
    private boolean popupClosed;
    private Color popupInnerCircleColor;
    private Color popupOuterCircleColor;
    private Shell popupShell;
    private SystemTray systemTray;
    private CLabel contentLabel;
    private final String title;
    private final String content;
    private final Image image;
    private final boolean isAtTheBottom;

    private SystemTrayAlert(Display display, SystemTray systemTray,
                            String title, String content, Image image, boolean isAtTheBottom) {
        this.display = display;
        this.systemTray = systemTray;
        this.title = title;
        this.content = content;
        this.image = image;
        this.isAtTheBottom = isAtTheBottom;
        mouseInPopup = false;
        popupClosed = false;
        initResources();
        initComponents();
    }

    public static SystemTrayAlert getInstance(Display display, SystemTray rssOwlSystemTray,
                                              String title, String content, Image image, boolean isAtTheBottom) {
        return new SystemTrayAlert(display, rssOwlSystemTray, title, content, image, isAtTheBottom);
    }

    public boolean isPopupClosed() {
        return popupClosed;
    }

    public void show(long count) {
        contentLabel.setText(content + count);
        popupClosed = false;
        moveIn();
        startAutoCloseThread();
    }

    private Rectangle getPrimaryClientArea() {
        Monitor primaryMonitor = display.getPrimaryMonitor();
        return (primaryMonitor != null) ? primaryMonitor.getClientArea() : display.getClientArea();
    }

    private void initComponents() {
        popupShell = new Shell(display, SWT.ON_TOP | SWT.NO_FOCUS);
        popupShell.setBackground(popupBorderColor);
        popupShell.setLayout(LayoutsManager.createGridLayout(1, 1, 1));
        popupShell.addDisposeListener(e -> onDispose());

        Composite outerCircle = new Composite(popupShell, SWT.NO_FOCUS);
        outerCircle.setBackground(popupOuterCircleColor);
        outerCircle.setLayoutData(LayoutsManager.createGridData(GridData.FILL_BOTH, 1));
        outerCircle.setLayout(LayoutsManager.createGridLayout(1, 0, 3, 0));

        Composite titleCircle = new Composite(outerCircle, SWT.NO_FOCUS);
        titleCircle.setLayoutData(LayoutsManager.createGridData(GridData.FILL_HORIZONTAL, 1));
        titleCircle.setBackground(outerCircle.getBackground());
        titleCircle.setLayout(LayoutsManager.createGridLayout(2, 0, 0));

        CLabel titleCircleLabel = new CLabel(titleCircle, SWT.NO_FOCUS);
        titleCircleLabel.setText(title);
        titleCircleLabel.setFont(popupBoldFont);
        titleCircleLabel.setBackground(titleCircle.getBackground());
        titleCircleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        titleCircleLabel.setForeground(display.getSystemColor(SWT.COLOR_WHITE));

        final CLabel closeButton = new CLabel(titleCircle, SWT.NO_FOCUS);
        closeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        closeButton.setBackground(titleCircle.getBackground());
        closeButton.setImage(ImageManager.iconTrayClose);
        closeButton.setCursor(display.getSystemCursor(SWT.CURSOR_HAND));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                close();
            }
        });

        closeButton.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseEnter(MouseEvent e) {
                closeButton.setImage(ImageManager.iconTrayCloseOver);
                mouseInPopup = true;
            }

            @Override
            public void mouseExit(MouseEvent e) {
                closeButton.setImage(ImageManager.iconTrayClose);
                mouseInPopup = false;
            }
        });

        Composite outerContentCircle = new Composite(outerCircle, SWT.NO_FOCUS);
        outerContentCircle.setLayout(LayoutsManager.createGridLayout(1, 3, 0));
        outerContentCircle.setLayoutData(LayoutsManager.createGridData(GridData.FILL_HORIZONTAL, 1));
        outerContentCircle.setBackground(outerCircle.getBackground());

        Composite middleContentCircle = new Composite(outerContentCircle, SWT.NO_FOCUS);
        middleContentCircle.setLayout(LayoutsManager.createGridLayout(1, 1, 1));
        middleContentCircle.setLayoutData(LayoutsManager.createGridData(GridData.FILL_HORIZONTAL, 1));
        middleContentCircle.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

        Composite innerContentCircle = new Composite(middleContentCircle, SWT.NO_FOCUS);
        innerContentCircle.setLayoutData(LayoutsManager.createGridData(GridData.FILL_HORIZONTAL, 1));
        innerContentCircle.setLayout(LayoutsManager.createGridLayout(1, 5, 5));
        innerContentCircle.setBackground(popupInnerCircleColor);

        contentLabel = new CLabel(innerContentCircle, SWT.NO_FOCUS);
        contentLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        contentLabel.setBackground(titleCircle.getBackground());
        contentLabel.setImage(image);
        contentLabel.setBackground(popupInnerCircleColor);
        contentLabel.setFont(popupBoldFont);
        contentLabel.setText(content + 0);
        contentLabel.setCursor(display.getSystemCursor(SWT.CURSOR_HAND));
        contentLabel.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseEnter(MouseEvent e) {
                contentLabel.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
                mouseInPopup = true;
            }

            @Override
            public void mouseExit(MouseEvent e) {
                contentLabel.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
                mouseInPopup = false;
            }
        });

        contentLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                close();
                systemTray.restoreWindow();
            }
        });

        popupShell.pack();
    }

    private void initResources() {
        popupBorderColor = new Color(display, 125, 177, 251);
        popupOuterCircleColor = new Color(display, 73, 135, 234);
        popupInnerCircleColor = new Color(display, 241, 240, 234);
        popupBoldFont = display.getSystemFont();
    }

    private void moveIn() {
        final Rectangle clArea = getPrimaryClientArea();
        final Point shellSize = popupShell.getSize();
        final int maxX = clArea.width + clArea.x;
        final int minX = maxX - shellSize.x;
        final int yPos = clArea.height + clArea.y - shellSize.y - (isAtTheBottom ? 0 : shellSize.y);
        final int pixelPerStep = shellSize.x / ANIMATION_STEPS;

        popupShell.setLocation(maxX, yPos);

        if (!popupShell.isVisible()) {
            popupShell.setVisible(true);
        }

        Thread animator = new Thread() {
            @Override
            public void run() {

                try {
                    sleep(ANIMATION_DELAY);
                } catch (InterruptedException e) {
                    return;
                }

                for (int a = maxX; a > minX; a -= pixelPerStep) {

                    move(a, yPos);

                    try {
                        sleep(ANIMATION_SLEEP);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                move(clArea.width + clArea.x - shellSize.x, yPos);
            }
        };

        animator.setName("Popup Animator Thread");
        animator.setDaemon(true);
        animator.start();
    }

    private void startAutoCloseThread() {
        if (autoClosePopup != null && !autoClosePopup.isStopped()) {
            autoClosePopup.stopThread();
        }
        final int timeout = GUI.setting.getAutoClosePopupTimeout();
        if (timeout <= 0) {
            return;
        }

        autoClosePopup = new ExtendedThread() {
            @Override
            public void run() {
                while (!isStopped() && !popupClosed) {
                    try {
                        sleep(timeout);
                        if (GUI.isAlive() && !isStopped()) {
                            display.asyncExec(new Runnable() {
                                @Override
                                public void run() {
                                    if (!mouseInPopup && !popupClosed && !isStopped()) {
                                        close();
                                    }
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        autoClosePopup.setName("Auto Close Tray Popup Thread");
        autoClosePopup.setDaemon(true);
        autoClosePopup.start();
    }

    void close() {
        popupClosed = true;
        if (autoClosePopup != null) {
            autoClosePopup.stopThread();
        }
        hide();
    }

    void hide() {
        if (GUI.isAlive() && WidgetsManager.isset(popupShell)) {
            Rectangle clArea = getPrimaryClientArea();
            popupShell.setVisible(false);
            popupShell.setLocation(clArea.width + clArea.x, clArea.height + clArea.y);
        }
    }

    void move(final int x, final int y) {
        if (GUI.isAlive()) {
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    if (WidgetsManager.isset(popupShell)) {
                        popupShell.setLocation(x, y);
                    }
                }
            });
        }
    }

    void onDispose() {
        popupBorderColor.dispose();
        popupOuterCircleColor.dispose();
        popupInnerCircleColor.dispose();
        popupBoldFont.dispose();
    }
}
