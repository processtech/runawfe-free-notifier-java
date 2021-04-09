package ru.runa.notifier.tray.alert;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import ru.runa.notifier.tray.SystemTray;
import ru.runa.notifier.tray.SystemTrayAlert;

public abstract class TrayAlert {
    private SystemTrayAlert alert = null;

    public SystemTrayAlert getInstance(Display display, SystemTray systemTray) {
        if (alert == null) {
            alert = SystemTrayAlert.getInstance(display, systemTray, getTitle(), getContent(), getImage());
        }
        return alert;
    }

    public abstract String getTitle();
    public abstract String getContent();
    public abstract Image getImage();
}
