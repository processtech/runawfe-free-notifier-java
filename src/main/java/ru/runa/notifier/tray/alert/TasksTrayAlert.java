package ru.runa.notifier.tray.alert;

import org.eclipse.swt.graphics.Image;
import ru.runa.notifier.util.ImageManager;
import ru.runa.notifier.util.ResourcesManager;

public class TasksTrayAlert extends TrayAlert {
    @Override
    public String getTitle() {
        return ResourcesManager.getNewTasksPopupTitle();
    }

    @Override
    public String getContent() {
        return ResourcesManager.getTeasePopupText();
    }

    @Override
    public Image getImage() {
        return ImageManager.iconTrayTease;
    }
}
