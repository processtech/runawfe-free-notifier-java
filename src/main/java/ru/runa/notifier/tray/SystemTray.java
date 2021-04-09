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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import ru.runa.notifier.GUI;
import ru.runa.notifier.auth.LoginHelper;
import ru.runa.notifier.checker.Checker;
import ru.runa.notifier.tray.alert.TrayAlert;
import ru.runa.notifier.util.ImageManager;
import ru.runa.notifier.util.ResourcesManager;
import ru.runa.notifier.util.WidgetsManager;
import ru.runa.notifier.view.ViewChangeListener;

/**
 * Created on 2006
 * 
 * @author Gritsenko_S
 */
public class SystemTray implements PropertyChangeListener {

	private static boolean trayIsTeasing = false;

	private static final int MIN_POPUP_GAP = 10000;

	private Display display;

	private MenuItem exitItem;

	private MenuItem openItem;

	private long popupTimeLock;

	private SystemTrayAlert trayPopup;

	private Tray systemTray;

	protected Shell shell;

	private TrayItem systemTrayItem;

	protected Menu systemTrayItemMenu;

    private ViewChangeListener viewChangeListener;

	private long unreadCount;
	
	boolean enableTray = ResourcesManager.getShowTray();

	private final TrayAlert trayAlert;

	public SystemTray(Display display, Shell shell, TrayAlert trayAlert) {
		this.display = display;
		this.shell = shell;
		this.trayAlert = trayAlert;
		initComponents();
		updateTray();

		trayAlert.getInstance(display, this);
	}

    public void setViewChangeListener(ViewChangeListener formClosedListener) {
        this.viewChangeListener = formClosedListener;
    }

	protected void restoreWindow() {
		if (trayPopup != null && !trayPopup.isPopupClosed()) {
            trayPopup.hide();
        }
        viewChangeListener.showBrowserView();
		GUI.restoreWindow();
	}

	private void updateTray() {
		if (!LoginHelper.isLogged()) {
			systemTrayItem.setToolTipText(ResourcesManager.getTooltipPopupNotLoggedText());
			if (systemTrayItem.getImage() != null) {
				if (systemTrayItem.getImage() != ImageManager.iconTrayNotLogged) {
					systemTrayItem.setImage(ImageManager.iconTrayNotLogged);
				}
			} else {
				systemTrayItem.setImage(ImageManager.iconTrayNotLogged);
			}
			return;
		}

		if (unreadCount > 0) {
			systemTrayItem.setToolTipText(trayAlert.getContent() + unreadCount);
			if (systemTrayItem.getImage() != null) {
				if (systemTrayItem.getImage() != ImageManager.iconTrayTasks) {
					systemTrayItem.setImage(ImageManager.iconTrayTasks);
				}
			} else {
				systemTrayItem.setImage(ImageManager.iconTrayTasks);
			}
		} else {
			systemTrayItem.setToolTipText(trayAlert.getContent());
			if (systemTrayItem.getImage() != null) {
				if (systemTrayItem.getImage() != ImageManager.iconTrayNoTasks) {
					systemTrayItem.setImage(ImageManager.iconTrayNoTasks);
				}
			} else {
				systemTrayItem.setImage(ImageManager.iconTrayNoTasks);
			}
		}
	}

	public void setTasks(long unreadCount, long newCount) {
		boolean teasing = (newCount > 0);
		this.unreadCount = unreadCount;
		if (teasing) {
			long curTimeMillis = System.currentTimeMillis();
			if (trayIsTeasing != teasing || curTimeMillis - popupTimeLock > MIN_POPUP_GAP) {
				showTrayPopup(newCount);
				popupTimeLock = curTimeMillis;
			}
		}
		trayIsTeasing = teasing;
		updateTray();
	}

	private void initComponents() {
		systemTray = display.getSystemTray();
		systemTrayItem = new TrayItem(systemTray, SWT.NONE);
		systemTrayItem.setVisible(enableTray);

		systemTrayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				if (WidgetsManager.isset(systemTrayItemMenu)) {
                    systemTrayItemMenu.setVisible(true);
                }
			}
		});

		systemTrayItem.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event event) {
				if (WidgetsManager.isset(systemTrayItemMenu)) {
                    restoreWindow();
                }
			}
		});

		systemTrayItemMenu = new Menu(shell, SWT.POP_UP);

		openItem = new MenuItem(systemTrayItemMenu, SWT.NONE);
		openItem.setText(ResourcesManager.getMenuOpenName());
		openItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				restoreWindow();
			}
		});

		systemTrayItemMenu.setDefaultItem(openItem);

		new MenuItem(systemTrayItemMenu, SWT.SEPARATOR);

		exitItem = new MenuItem(systemTrayItemMenu, SWT.NONE);
		exitItem.setText(ResourcesManager.getMenuExitName());
		exitItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
	}

	private void showTrayPopup(long count) {
		if (enableTray && (trayPopup == null || trayPopup.isPopupClosed())) {
			trayPopup = trayAlert.getInstance(display, this);
			trayPopup.show(count);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (Checker.ERROR_OCCURRED_PROPERTY.equals(propertyName)) {
			boolean oldValue = (Boolean) evt.getOldValue();
			boolean newValue = (Boolean) evt.getNewValue();
			if (!oldValue && newValue) {
				systemTrayItem.setToolTipText(ResourcesManager.getTooltipPopupErrorText());
				if (systemTrayItem.getImage() != null) {
					if (systemTrayItem.getImage() != ImageManager.iconTrayError) {
						systemTrayItem.setImage(ImageManager.iconTrayError);
					}
				} else {
					systemTrayItem.setImage(ImageManager.iconTrayError);
				}
			}
		}
	}
}
