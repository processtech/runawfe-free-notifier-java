package ru.runa.notifier.tray.alert;

import org.eclipse.swt.widgets.Display;
import ru.runa.notifier.tray.SystemTray;
import ru.runa.notifier.tray.SystemTrayAlert;
import ru.runa.notifier.util.ImageManager;
import ru.runa.notifier.util.ResourcesManager;

public class MessagesTrayAlert extends TrayAlert {
    @Override
    public SystemTrayAlert getSystemTrayAlert(Display display, SystemTray systemTray) {
        return SystemTrayAlert.getInstance(display, systemTray, ResourcesManager.getNewMessagesPopupTitle(),
                ResourcesManager.getNewMessagesPopupText(), ImageManager.iconApplication);
    }
}
