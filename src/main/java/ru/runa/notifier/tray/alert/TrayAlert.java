package ru.runa.notifier.tray.alert;

import org.eclipse.swt.widgets.Display;
import ru.runa.notifier.tray.SystemTray;
import ru.runa.notifier.tray.SystemTrayAlert;

public abstract class TrayAlert {

    private SystemTrayAlert alert = null;

    public SystemTrayAlert getInstance(Display display, SystemTray systemTray) {
        if (alert == null) {
            alert = getSystemTrayAlert(display, systemTray);
        }
        return alert;
    }

    public abstract SystemTrayAlert getSystemTrayAlert(Display display, SystemTray systemTray);
}
