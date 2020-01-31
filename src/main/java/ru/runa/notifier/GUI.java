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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import ru.runa.notifier.auth.LoginHelper;
import ru.runa.notifier.checker.TaskChecker;
import ru.runa.notifier.tray.SystemTray;
import ru.runa.notifier.util.AePlayWave;
import ru.runa.notifier.util.ImageManager;
import ru.runa.notifier.util.ResourcesManager;
import ru.runa.notifier.util.WidgetsManager;
import ru.runa.notifier.view.ViewChangeListener;

/**
 * Created on 2006
 * 
 * @author Gritsenko_S
 */
public class GUI implements PropertyChangeListener, ViewChangeListener, LocationListener {
    private static final String ABOUT_BLANK = "about:blank";

    private static final Log log = LogFactory.getLog(GUI.class);

    public static Display display;

    public static Shell shell;

    private SystemTray systemTray;

    private TaskChecker tasksChecker;

    private static BrowserView browserView;

    public GUI(Display display, Shell splashShell) {
        GUI.display = display;
        initComponents();
        splashShell.dispose();
    }

    public static boolean isAlive() {
        return (!display.isDisposed() && !shell.isDisposed());
    }

    private void startCheckForTasks() {
        tasksChecker = new TaskChecker(systemTray);
        tasksChecker.addPropertyChangeListener(this);
        tasksChecker.addPropertyChangeListener(systemTray);
        tasksChecker.start();
    }

    private void initComponents() {
        shell = new Shell(display);
        shell.setText(ResourcesManager.getApplicationName());
        shell.setImage(ImageManager.iconApplication);
        shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent event) {
                onDispose();
            }
        });

        shell.addListener(SWT.Close, new Listener() {
            @Override
            public void handleEvent(Event event) {
                onClose(event);
            }
        });

        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellIconified(ShellEvent event) {
                onClose(event);
            }
        });

        shell.setLayout(new GridLayout());
        log.info("Running on " + SWT.getPlatform());
    }

    @Override
    public void changed(LocationEvent event) {
    }

    @Override
    public void changing(LocationEvent event) {
    }

    public boolean isChangeUrl() {
        String url = browserView.getBrowser().getUrl();
        boolean changeUrl = true;
        if (url != null && url.length() > 0) {
            if (!url.contains("manage_tasks.do") && !ABOUT_BLANK.equals(url)) {
                changeUrl = false;
            }
        }
        return changeUrl;
    }

    public void openStartPage() {
        if (isChangeUrl()) {
            String targetUrl;
            String serverUrl = ResourcesManager.getDefaultServerUrl() + "/wfe";
            if (LoginHelper.getWebParameters() == null || LoginHelper.getWebParameters().length() == 0) {
                targetUrl = serverUrl + ResourcesManager.getLoginRelativeUrl();
            } else {
                targetUrl = serverUrl + ResourcesManager.getLoginRelativeUrl() + "?" + LoginHelper.getWebParameters();
            }
            browserView.getBrowser().setUrl(targetUrl);
        }
    }

    public void openBlankPage() {
        if (isChangeUrl()) {
            browserView.getBrowser().setUrl(ABOUT_BLANK);
        }
    }

    @Override
    public void showBrowserView() {
        initBrowserView();
        openStartPage();
        maximazeControl(browserView);
        shell.layout(true, true);
    }

    @Override
    public void showInfoPathFormView() {
        openBlankPage();
        minimazeControl(browserView);
        shell.layout(true, true);
    }

    private void initBrowserView() {
        if (browserView == null) {
            browserView = new BrowserView(shell, SWT.FILL);
            browserView.getBrowser().addLocationListener(this);
            browserView.setLayoutData(new GridData());
        }
        browserView.getBrowser().setVisible(true);
    }

    private void maximazeControl(Control control) {
        control.setEnabled(true);
        control.setVisible(true);
        GridData gridData = (GridData) control.getLayoutData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.exclude = false;
        gridData.heightHint = 300;
        gridData.minimumHeight = 300;
        gridData.minimumWidth = 300;
        gridData.widthHint = 300;
        control.setLayoutData(gridData);
    }

    private void minimazeControl(Control control) {
        control.setEnabled(false);
        control.setVisible(false);
        GridData gridData = (GridData) control.getLayoutData();
        gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
        gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;
        gridData.exclude = true;
        gridData.heightHint = 10;
        gridData.minimumHeight = 0;
        gridData.minimumWidth = 0;
        gridData.widthHint = 10;
        control.setLayoutData(gridData);
    }

    private void runEventLoop() {
        try {
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            log.warn("runEventLoop (Unforseen Exception)", e);
        }
        display.dispose();
    }

    protected void onClose(Event event) {
        event.doit = false;
        hideShell();
    }

    protected void onClose(ShellEvent event) {
        event.doit = false;
        hideShell();
    }

    private void hideShell() {
        if (ResourcesManager.isRestartRtnOnClose()) {
            try {
                String commandLine = ResourcesManager.getStartRtnCommand();
                Runtime.getRuntime().exec(commandLine);
                shell.dispose();
            } catch (Throwable th) {
                log.warn("Unable to restart RTN", th);
            }
        } else {
            openBlankPage();
            shell.setVisible(false);
        }
    }

    protected void onDispose() {
        if (shell.isVisible()) {
            shell.setVisible(false);
        }
        shutDown();
    }

    public static void restoreWindow() {
        LoginHelper.setLoginEnable(true);
        if (WidgetsManager.isset(shell)) {
            shell.setVisible(true);
            shell.setMaximized(true);
            shell.setActive();
        }
    }

    void showGui() {
        systemTray = new SystemTray(display, shell);
        systemTray.setViewChangeListener(this);
        AePlayWave.playNotification("/onAppStart.wav");
        startCheckForTasks();
        runEventLoop();
    }

    private void shutDown() {
        AePlayWave.playNotification("/onAppShutdown.wav");
        ImageManager.disposeIcons();
        LoginHelper.setLoginEnable(false);
        if (tasksChecker != null) {
            tasksChecker.stop();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
