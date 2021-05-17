package ru.runa.notifier.tray.alert;

import org.eclipse.swt.graphics.Image;
import ru.runa.notifier.util.ImageManager;
import ru.runa.notifier.util.ResourcesManager;

public class MessagesTrayAlert extends TrayAlert {
    @Override
    public String getStartPageUrl() {
        return ResourcesManager.getChatsRelativeUrl();
    }

    @Override
    public String getTitle() {
        return ResourcesManager.getNewMessagesPopupTitle();
    }

    @Override
    public String getContent() {
        return ResourcesManager.getNewMessagesPopupText();
    }

    @Override
    public Image getImage() {
        return ImageManager.iconApplication;
    }

    @Override
    public boolean isLeftAlign() {
        return true;
    }
}
