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
import ru.runa.notifier.checker.TaskChecker;
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

	private int unreadTasksCount;
	
	boolean enableTray = ResourcesManager.getShowTray();

	public SystemTray(Display display, Shell shell) {
		this.display = display;
		this.shell = shell;
		initComponents();
		updateTray();

		SystemTrayAlert.getInstance(display, this);
	}

    public void setViewChangeListener(ViewChangeListener formClosedListener) {
        this.viewChangeListener = formClosedListener;
    }

	public void resetPopupTimeLock() {
		popupTimeLock = 0;
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

		if (unreadTasksCount > 0) {
			systemTrayItem.setToolTipText(ResourcesManager.getTooltipPopupTasksText() + unreadTasksCount);
			if (systemTrayItem.getImage() != null) {
				if (systemTrayItem.getImage() != ImageManager.iconTrayTasks) {
					systemTrayItem.setImage(ImageManager.iconTrayTasks);
				}
			} else {
				systemTrayItem.setImage(ImageManager.iconTrayTasks);
			}
		} else {
			systemTrayItem.setToolTipText(ResourcesManager.getTooltipPopupNoTasksText());
			if (systemTrayItem.getImage() != null) {
				if (systemTrayItem.getImage() != ImageManager.iconTrayNoTasks) {
					systemTrayItem.setImage(ImageManager.iconTrayNoTasks);
				}
			} else {
				systemTrayItem.setImage(ImageManager.iconTrayNoTasks);
			}
		}
	}

	public void setTasks(int unreadTasksCount, int newTasksCount) {
		boolean teasing = (newTasksCount > 0);
		this.unreadTasksCount = unreadTasksCount;
		if (teasing) {
			long curTimeMillis = System.currentTimeMillis();
			if (trayIsTeasing != teasing || curTimeMillis - popupTimeLock > MIN_POPUP_GAP) {
				showTrayPopup(newTasksCount);
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

	private void showTrayPopup(int newTasksCount) {
		if (enableTray && (trayPopup == null || trayPopup.isPopupClosed())) {
			trayPopup = SystemTrayAlert.getInstance(display, this);
			trayPopup.show(newTasksCount);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (TaskChecker.ERROR_OCCURED_PROPERTY.equals(propertyName)) {
			boolean oldValue = ((Boolean) evt.getOldValue()).booleanValue();
			boolean newValue = ((Boolean) evt.getNewValue()).booleanValue();
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
